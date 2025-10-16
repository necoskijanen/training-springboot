package com.example.demo.security;

import com.example.demo.mapper.UserMapper;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        private final UserMapper userMapper;

        public CustomUserDetailsService(UserMapper userMapper) {
                this.userMapper = userMapper;
        }

        // 認証ロジックの実装
        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                User user = userMapper.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with email: " + email));
                List<Role> roles = user.getRoles();

                // Spring SecurityのUserDetailsオブジェクトを生成
                var authorities = roles.stream()
                                // ロール名をAuthorityとして登録
                                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                role.getAuthName()))
                                .collect(Collectors.toList());

                return new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                user.getIsActive(),
                                true, true, true,
                                authorities);
        }
}
