package com.tqtadka.platform;

import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TqtadkaPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TqtadkaPlatformApplication.class, args);
	}

	// ✅ ONE-TIME ADMIN BOOTSTRAP
	@Bean
	CommandLineRunner initAdmin(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		return args -> {

			if (userRepository.findByEmail("admin@tqtadka.com").isEmpty()) {

				User admin = User.builder()
						.email("admin@tqtadka.com")
						.password(passwordEncoder.encode("admin123"))
						.name("Platform Admin")
						.role(Role.ADMIN)
						.enabled(true)
						.build();

				userRepository.save(admin);
			}
		};
	}
}
