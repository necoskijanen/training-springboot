package com.example.demo.authentication.application;

import com.example.demo.authentication.domain.Role;
import com.example.demo.authentication.domain.User;
import com.example.demo.authentication.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Securityのユーザ詳細サービス実装
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetailsServiceImpl() {
    }

    // 認証ロジックの実装
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByName(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        List<Role> roles = user.getRoles();

        // Spring SecurityのUserDetailsオブジェクトを生成
        var authorities = roles.stream()
                // ロール名をAuthorityとして登録
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        role.getAuthority()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                user.getIsActive(),
                true, true, true,
                authorities);
    }
}
