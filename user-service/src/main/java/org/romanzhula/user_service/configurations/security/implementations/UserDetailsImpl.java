package org.romanzhula.user_service.configurations.security.implementations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.romanzhula.user_service.models.User;
import org.romanzhula.user_service.models.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;


public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L; // for session authentication

    private final String username;

    @JsonIgnore
    private final String password;

    private final UserRole role;

    public UserDetailsImpl(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
