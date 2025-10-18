package com.example.demo.authentication;

import com.example.demo.domain.user.User;
import com.example.demo.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

                // Spring SecurityのUserDetailsオブジェクトを生成
                var authorities = user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                                .collect(Collectors.toList());

                return new org.springframework.security.core.userdetails.User(
                                user.getName(),
                                user.getPassword(),
                                user.getIsActive(),
                                true, true, true,
                                authorities);
        }
}
