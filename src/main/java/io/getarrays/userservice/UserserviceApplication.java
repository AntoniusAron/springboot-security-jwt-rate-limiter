package io.getarrays.userservice;

import io.getarrays.userservice.domain.RoleData;
import io.getarrays.userservice.domain.UserData;
import io.getarrays.userservice.repo.UserRepo;
import io.getarrays.userservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootApplication
public class UserserviceApplication {

	private final UserRepo userRepo;

	public UserserviceApplication(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

	//This will run everytime application finish run.initialize
	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			RoleData roleUser = new RoleData(null, "ROLE_USER");
			RoleData roleManager = new RoleData(null, "ROLE_MANAGER");
			RoleData roleAdmin = new RoleData(null, "ROLE_ADMIN");
			RoleData roleSuperAdmin = new RoleData(null, "ROLE_SUPER_ADMIN");

			if(userService.getRole(roleUser.getName()) == null)
				userService.saveRole(roleUser);
			if(userService.getRole(roleManager.getName()) == null)
				userService.saveRole(roleManager);
			if(userService.getRole(roleAdmin.getName()) == null)
				userService.saveRole(roleAdmin);
			if(userService.getRole(roleSuperAdmin.getName()) == null)
				userService.saveRole(roleSuperAdmin);

			UserData john = new UserData(null, "John Travolta", "john", "1234", new HashSet<>());
			UserData will = new UserData(null, "Will Smith", "will", "1234", new HashSet<>());
			UserData jim = new UserData(null, "Jim Carry", "jim", "1234", new HashSet<>());
			UserData arnold = new UserData(null, "Arnold Schwarzenegger", "arnold", "1234", new HashSet<>());

			if(userService.getUser(john.getUsername()) == null)
				userService.saveUser(john);
			if(userService.getUser(will.getUsername()) == null)
				userService.saveUser(will);
			if(userService.getUser(jim.getUsername()) == null)
				userService.saveUser(jim);
			if(userService.getUser(arnold.getUsername()) == null)
				userService.saveUser(arnold);

			userService.addRoleToUser(john.getUsername(), roleUser.getName());
			userService.addRoleToUser(jim.getUsername(), roleAdmin.getName());
			userService.addRoleToUser(arnold.getUsername(), roleSuperAdmin.getName());
			userService.addRoleToUser(arnold.getUsername(), roleAdmin.getName());
			userService.addRoleToUser(arnold.getUsername(), roleUser.getName());
		};
	}

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
