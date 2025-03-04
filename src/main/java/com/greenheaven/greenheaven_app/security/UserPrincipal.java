package com.greenheaven.greenheaven_app.security;

import com.greenheaven.greenheaven_app.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class UserPrincipal implements UserDetails { // UserDetails 인터페이스를 구현하여 인증 및 권한 부여에 필요한 메서드를 제공, Spring Secuiry의 인증 프로세스에서 사용자 정보를 전달하는 역할
    private final UUID id;
    private final String name;
    private final String email;
    private final String password;

    public UserPrincipal(User user) { // User 엔티티 객체를 받아, UserPrincipal 객체의 각 필드를 초기화
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    @Override // UserDetailsService에서 요구하는 메서드로, 사용자의 권한을 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 현재는 Collections.emptyList()를 반환하여, 권한이 없음을 의미
    }

    @Override // 사용자 이름을 반환하는 메서드, 
    public String getUsername() {
        return email; // 이메일을 username으로 이용하므로, email을 반환
    }

    @Override
    public String getPassword() {
        return password;  // 암호화된 비밀번호 반환
    }


    @Override // 계정의 만료 여부를 판단
    public boolean isAccountNonExpired() {
        return true; // true를 반환하면 계정이 만료되지 않았음을 의미, 현재는 특별한 만료 로직이 없으므로 기본적으로 true를 반환
    }

    @Override // 계정이 잠겨 있는지 여부를 판단
    public boolean isAccountNonLocked() {
        return true; // true를 반환하면 계정이 잠겨있지 않음을 의미, 현재는 계정 잠금 처리를 구현하지 않았으므로 true를 반환
    }

    @Override // 자격 증명의 만료 여부를 판단
    public boolean isCredentialsNonExpired() {
        return true; // true를 반환하면 자격 증명이 만료되지 않았음을 의미, 비밀번호 만료 등의 로직이 당장은 필요하지 않으므로 true를 반환
    }

    @Override // 사용자의 계정이 활성화 되어있는지 여부를 판단
    public boolean isEnabled() { 
        return true; // true를 반환하면 사용자가 활성 상태임을 의미, 계정 비활성화(탈퇴, 정지) 기능에 따라 추후 로직 변경 예정
    }
}
