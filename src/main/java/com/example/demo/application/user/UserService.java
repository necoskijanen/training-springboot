package com.example.demo.application.user;

import com.example.demo.application.user.dto.CreateUserRequest;
import com.example.demo.application.user.dto.UpdateUserRequest;
import com.example.demo.application.user.dto.UserResponse;
import com.example.demo.application.user.mapper.UserMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserDomainException;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserMapper userMapper;

    /**
     * ユーザを検索
     * 
     * @param name  ユーザ名（部分一致、nullの場合は検索条件に含めない）
     * @param email メールアドレス（部分一致、nullの場合は検索条件に含めない）
     * @return ユーザレスポンス DTO リスト
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String name, String email) {
        log.debug("Searching users: name={}, email={}", name, email);
        List<User> users = userRepository.searchUsers(name, email);
        return userMapper.toUserResponseList(users);
    }

    /**
     * ユーザをIDで取得し、更新用 DTO に変換して返す
     * 
     * @param id ユーザID
     * @return ユーザ更新リクエスト DTO をラップした Optional
     */
    @Transactional(readOnly = true)
    public Optional<UpdateUserRequest> findById(Long id) {
        log.debug("Finding user by id (Optional): {}", id);
        return userRepository.findById(id)
                .map(userMapper::toUpdateUserRequest);
    }

    /**
     * ユーザを作成
     * 
     * @param request ユーザ作成リクエスト
     * @throws UserDomainException ユーザ名が既に存在する場合
     */
    public void createUser(CreateUserRequest request) {
        log.info("Creating user: name={}, email={}", request.getName(), request.getEmail());

        // ユーザ名が既に存在するか確認
        if (userRepository.existsByName(request.getName())) {
            log.warn("User creation failed: name already exists: {}", request.getName());
            throw new UserDomainException(UserErrorCode.DUPLICATE_NAME);
        }

        // ドメインのファクトリメソッドを使用してユーザを作成
        User user = User.createNewUser(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getAdmin());

        // ユーザを挿入
        userRepository.insert(user);

        log.info("User created successfully: id={}", user.getId());
    }

    /**
     * ユーザを更新
     * 
     * @param request ユーザ更新リクエスト
     * @throws UserDomainException ユーザが見つからない、またはビジネスルール違反の場合
     */
    public void updateUser(UpdateUserRequest request, Long updaterId) {
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

        // ユーザを更新
        existingUser.updateUserInfo(request.getName(), request.getEmail(), request.getAdmin(), updaterId);
        userRepository.update(existingUser);

        log.info("User updated successfully: id={}", existingUser.getId());
    }

}
