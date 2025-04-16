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
        // Trả về danh sách quyền của người dùng
        // Giả sử rằng role của người dùng là một chuỗi, bạn có thể tạo một GrantedAuthority từ role
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
        // Giả sử bạn có một thuộc tính `accountExpiryDate` trong User để kiểm tra
        return user.getResetTokenExpire() == null || user.getResetTokenExpire().after(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        // Giả sử bạn có một thuộc tính `locked` trong User để kiểm tra
        return user.getStatus().equals("ACTIVE"); // Trả về true nếu tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Giả sử bạn có một thuộc tính `credentialsExpiryDate` trong User để kiểm tra
        return user.getResetTokenExpire() == null || user.getResetTokenExpire().after(new Date());
    }

    @Override
    public boolean isEnabled() {
        // Trả về trạng thái của người dùng
        return user.getStatus().equals("ACTIVE"); // Trả về true nếu người dùng đang hoạt động
    }

    // Phương thức để lấy đối tượng User
    public User getUser () {
        return user;
    }

    // Phương thức để lấy ID của người dùng
    public Long getId() {
        return user.getUserId(); // Trả về ID của người dùng
    }
}