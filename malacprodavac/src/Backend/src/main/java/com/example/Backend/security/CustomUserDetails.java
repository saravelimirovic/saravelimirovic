package com.example.Backend.security;

import com.example.Backend.entity.User;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.util.*;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Getter
@Setter
public class CustomUserDetails extends User implements UserDetails {

    private final String credentials;
    private String sessionIdentifier;
    private List<String> roles;

    public CustomUserDetails(User user, String credentials) {
        super(user);
        this.credentials = credentials;
        this.sessionIdentifier = EMPTY;
    }


    @Builder
    private CustomUserDetails(User user, String credentials, List<String> roles) {
        super(user);
        this.credentials = credentials;
        this.roles = roles;
        sessionIdentifier = EMPTY;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        String roles = StringUtils.collectionToCommaDelimitedString(getRoles());
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
