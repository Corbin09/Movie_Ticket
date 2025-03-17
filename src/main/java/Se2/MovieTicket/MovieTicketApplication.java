package Se2.MovieTicket;

import Se2.MovieTicket.models.User;
import Se2.MovieTicket.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieTicketApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword("admin123"); // Tạm thời chưa mã hóa mật khẩu
				userRepository.save(admin);
			}
			if (userRepository.findByUsername("user").isEmpty()) {
				User user = new User();
				user.setUsername("user");
				user.setPassword("user123");
				userRepository.save(user);
			}
		};
	}
}
