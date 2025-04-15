package Se2.MovieTicket.impl;

import Se2.MovieTicket.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class UserDetailsImpl implements UserDetails {
    private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + user.getRole().toUpperCase());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getResetTokenExpire() == null || user.getResetTokenExpire().after(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus().equals("ACTIVE");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getResetTokenExpire() == null || user.getResetTokenExpire().after(new Date());
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals("ACTIVE");
    }

    public User getUser () {
        return user;
    }

    public Long getId() {
        return user.getUserId();
    }
}