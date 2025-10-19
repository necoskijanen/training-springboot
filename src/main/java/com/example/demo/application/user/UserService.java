package com.example.demo.application.user;

import com.example.demo.application.user.dto.CreateUserRequest;
import com.example.demo.application.user.dto.UpdateUserRequest;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserDomainException;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * ユーザを検索
     * 
     * @param name  ユーザ名（部分一致、nullの場合は検索条件に含めない）
     * @param email メールアドレス（部分一致、nullの場合は検索条件に含めない）
     * @return ユーザリスト
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String name, String email) {
        log.debug("Searching users: name={}, email={}", name, email);
        return userRepository.searchUsers(name, email);
    }

    /**
     * ユーザをIDで取得（Optional版）
     * 
     * @param id ユーザID
     * @return ユーザをラップしたOptional
     */
    @Transactional(readOnly = true)
    public Optional<User> findByIdOptional(Long id) {
        log.debug("Finding user by id (Optional): {}", id);
        return userRepository.findById(id);
    }

    /**
     * ユーザを作成
     * 
     * @param request ユーザ作成リクエスト
     * @return 作成されたユーザ
     * @throws UserDomainException ユーザ名が既に存在する場合
     */
    public User createUser(CreateUserRequest request) {
        log.info("Creating user: name={}, email={}", request.getName(), request.getEmail());

        // ユーザ名が既に存在するか確認
        if (userRepository.existsByName(request.getName())) {
            log.warn("User creation failed: name already exists: {}", request.getName());
            throw new UserDomainException(UserErrorCode.DUPLICATE_NAME);
        }

        // ユーザオブジェクトを構築
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setAdmin(request.getAdmin());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // ユーザを挿入
        userRepository.insert(user);

        // ロール割り当てを処理
        assignRoles(user);

        log.info("User created successfully: id={}", user.getId());
        return user;
    }

    /**
     * ユーザを更新
     * 
     * @param request ユーザ更新リクエスト
     * @return 更新されたユーザ
     * @throws UserDomainException ユーザが見つからない、またはビジネスルール違反の場合
     */
    public User updateUser(UpdateUserRequest request) {
        log.info("Updating user: id={}", request.getId());

        // ユーザが存在するか確認
        User existingUser = userRepository.findById(request.getId())
                .orElseThrow(() -> {
                    log.warn("User update failed: user not found with id: {}", request.getId());
                    return new UserDomainException(UserErrorCode.USER_NOT_FOUND);
                });

        // ユーザ名が変更されている場合、新しいユーザ名が既に存在するか確認
        if (!existingUser.getName().equals(request.getName())) {
            if (userRepository.existsByName(request.getName())) {
                log.warn("User update failed: name already exists: {}", request.getName());
                throw new UserDomainException(UserErrorCode.DUPLICATE_NAME);
            }
        }

        // 管理者が自身の権限を削除しようとしていないか確認
        if (existingUser.getId().equals(request.getId()) && existingUser.getAdmin() && !request.getAdmin()) {
            log.warn("User update failed: admin user cannot revoke own admin rights: id={}", request.getId());
            throw new UserDomainException(UserErrorCode.CANNOT_REVOKE_OWN_ADMIN_RIGHTS);
        }

        // ユーザオブジェクトを更新
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setAdmin(request.getAdmin());
        existingUser.setUpdatedAt(LocalDateTime.now());

        // ユーザを更新
        userRepository.update(existingUser);

        // ロール割り当てを処理
        assignRoles(existingUser);

        log.info("User updated successfully: id={}", existingUser.getId());
        return existingUser;
    }

    /**
     * ユーザにロールを割り当てる
     * 
     * @param user ユーザオブジェクト
     */
    private void assignRoles(User user) {
        // 既存のロール割り当てを削除
        userRepository.deleteUserRoles(user.getId());

        // 常に USER ロールを割り当てる
        Optional.ofNullable(userRepository.findRoleIdByName("USER"))
                .ifPresentOrElse(
                        userRoleId -> {
                            userRepository.insertUserRole(user.getId(), userRoleId);
                            log.debug("USER role assigned to user: id={}", user.getId());
                        },
                        () -> log.warn("USER role not found in role_definition table"));

        // 管理者の場合、ADMIN ロールも割り当てる
        if (user.getAdmin()) {
            Optional.ofNullable(userRepository.findRoleIdByName("ADMIN"))
                    .ifPresentOrElse(
                            adminRoleId -> {
                                userRepository.insertUserRole(user.getId(), adminRoleId);
                                log.debug("ADMIN role assigned to user: id={}", user.getId());
                            },
                            () -> log.warn("ADMIN role not found in role_definition table"));
        }
    }
}
