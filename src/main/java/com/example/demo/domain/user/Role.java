package com.example.demo.domain.user;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * ロールのドメインエンティティ
 * ロール情報と権限検査ロジックを担当する
 */
@Data
@Alias("AuthRole")
public class Role {
    private int id;
    private String name;

    /**
     * Spring Security形式の権限文字列を取得する
     * 
     * @return "ROLE_"プレフィックス付きのロール名
     */
    public String getAuthority() {
        return "ROLE_" + name;
    }

}
