package Se2.MovieTicket.config;

import Se2.MovieTicket.repository.UserRepository;
import Se2.MovieTicket.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import java.io.IOException;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.UserService;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .successHandler(authenticationSuccessHandler())
//                        .failureUrl("/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .logoutSuccessUrl("/login?logout")
//                        .permitAll()
//                )
//                .sessionManagement(session -> session
//                        .sessionFixation().migrateSession()
//                        .maximumSessions(1)
//                )
//                .securityContext(security -> security
//                        .securityContextRepository(httpSessionSecurityContextRepository())
//                        .requireExplicitSave(true)
//                )
//                // ✅ BẬT lại CSRF
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**") // Nếu có API cần bỏ qua CSRF
//                );
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Các trang công khai
                        .requestMatchers("/", "/home", "/login", "/View-movie-ticket", "/register", "/css/**", "/js/**").permitAll()

                        // Các trang dành cho USER
                        .requestMatchers("/index", "/user-dashboard", "/profile", "/user-tickets/**").hasRole("USER")

                        // Các trang dành cho ADMIN
                        .requestMatchers("/pay-ticket", "/admin-dashboard", "/reports/**", "/manage-users").hasRole("ADMIN")

                        // Các trang chung cho cả USER và ADMIN
                        .requestMatchers("/account", "/change-password", "/notifications").hasAnyRole("USER", "ADMIN")

                        // Các request khác cần xác thực
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler()) // Sử dụng success handler này
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            System.out.println("✅ Đăng nhập thành công cho user: " + username);

            // Lưu user vào session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            // Lấy thông tin user từ UserRepository
            Optional<User> userOptional = userRepository.findByUsername(username);
            userOptional.ifPresent(user -> session.setAttribute("user", user));

            // Điều hướng dựa trên vai trò
            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/pay-ticket"); // Trang khởi đầu cho ADMIN
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
                response.sendRedirect("/home"); // Trang khởi đầu cho USER
            } else {
                response.sendRedirect("/index");
            }
        };
    }
    @Bean
    public SecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler() {
//        return (request, response, authentication) -> {
//            String username = authentication.getName();
//            System.out.println("✅ Đăng nhập thành công cho user: " + username);
//
//            // Lưu user vào session
//            HttpSession session = request.getSession();
//            session.setAttribute("username", username);
//
//            // Lấy thông tin user trực tiếp từ UserRepository
//            Optional<User> userOptional = userRepository.findByUsername(username);
//            userOptional.ifPresent(user -> session.setAttribute("user", user));
//
//            boolean isAdmin = authentication.getAuthorities().stream()
//                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//            boolean isUser = authentication.getAuthorities().stream()
//                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
//
//            if (isAdmin) {
//                response.sendRedirect("/pay-ticket");
//            } else if (isUser) {
//                response.sendRedirect("/index");
//            } else {
//                response.sendRedirect("/home");
//            }
//        };
//    }
@Bean
public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
}

}