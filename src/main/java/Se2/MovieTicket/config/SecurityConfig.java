package Se2.MovieTicket.config;

import Se2.MovieTicket.repository.UserRepository;
import Se2.MovieTicket.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import Se2.MovieTicket.model.User;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/chart.html", "/login",  "/register", "/css/**", "/js/**").permitAll()
                        .requestMatchers( "/home", "/detail-actor/**", "/detail-director/**", "/news/**", "/View-movie-ticket/**", "/showtime**", "/create-order", "/view-ticket", "/detail-movie/**", "/user-dashboard", "/profile", "/pick-seat", "pick-payment-method.css/**", "/user-tickets/**").hasRole("USER")
                        .requestMatchers( "showtimes/**", "/users/update-role/**",  "/manage-showtimes/**", "/manage-cinema/**", "/cinemas/save", "cinemas/edit/**",  "/cinemas/**", "/welcome-admin", "/admin-dashboard", "/reports/**", "/manage-users", "/manage-orders/**", "/manage-rooms.css/**", "/delete-rooms").hasRole("ADMIN")
                        .requestMatchers("/account").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                // Add CSRF configuration
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            System.out.println("✅ Đăng nhập thành công cho user: " + username);

            // Save user to session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            // Get user information from UserRepository
            Optional<User> userOptional = userRepository.findByUsername(username);
            userOptional.ifPresent(user -> session.setAttribute("user", user));

            // Navigate based on role
            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/welcome-admin");
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
                response.sendRedirect("/home");
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

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

}