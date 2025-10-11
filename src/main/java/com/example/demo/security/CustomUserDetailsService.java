package com.example.demo.security;

import com.example.demo.mapper.UserMapper;
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

    // MyBatis MapperをDI
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 認証ロジックの実装
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. MyBatisを使ってDBからユーザー情報を取得
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. ユーザーIDを使ってロールを取得
        List<String> roles = userMapper.findRolesByUserId(user.id());

        // 3. Spring SecurityのUserDetailsオブジェクトを生成
        List<org.springframework.security.core.GrantedAuthority> authorities = roles.stream()
                // ロール名に "ROLE_" プレフィックスを付けてAuthorityとして登録
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        // Spring Securityが理解できるユーザー情報を返却
        return new org.springframework.security.core.userdetails.User(
                user.email(), // ユーザー名 (ここではメールアドレス)
                user.password(), // ハッシュ化されたパスワード
                user.isActive(), // isEnabled (アカウント有効/無効)
                true, true, true, // その他のフラグ (期限切れ、ロックなど)
                authorities // ロール/権限情報
        );
    }
}
