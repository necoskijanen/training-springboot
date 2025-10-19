package com.example.demo.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.user.ApplicationRole;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.repository.UserRepository;

/**
 * Spring Securityのユーザ詳細サービス実装
 * 認可判定は user_master テーブルの admin フラグのみに基づいて実施
 * role_definition は将来の機能ごと認可に備えて予約済み
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        public UserDetailsServiceImpl() {
        }

        /**
         * ユーザ名（メール）でユーザを検索し、Spring Security用の UserDetails を生成する
         * 
         * @param email ユーザのメール（ユーザ名として使用）
         * @return UserDetails オブジェクト
         * @throws UsernameNotFoundException ユーザが見つからない場合
         */
        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                User user = userRepository.findByName(email)
                                .orElseThrow(() -> new UsernameNotFoundException(email));

                // ユーザの admin フラグに基づいて権限を判定
                // true: ROLE_ADMIN、false: ROLE_USER
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                ApplicationRole role = user.hasAdminRole() ? ApplicationRole.ADMIN : ApplicationRole.USER;
                authorities.add(new SimpleGrantedAuthority(role.getAuthority()));

                // Spring Security の UserDetails オブジェクトを生成
                return new org.springframework.security.core.userdetails.User(
                                user.getName(),
                                user.getPassword(),
                                user.getIsActive(),
                                true, // accountNonExpired
                                true, // credentialsNonExpired
                                true, // accountNonLocked
                                authorities);
        }
}
