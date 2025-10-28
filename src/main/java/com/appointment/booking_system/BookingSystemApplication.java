package com.appointment.booking_system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.appointment.booking_system.model.User;
import com.appointment.booking_system.repository.UserRepository;

@SpringBootApplication
public class BookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingSystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(UserRepository userRepository) {
		return args -> {
			
			if (userRepository.count() > 0) {
				System.out.println("Dummy data already exists. Skipping...");
				return;
			}
			


			System.out.println("Creating dummy users...");
			
			
			for (int i = 1; i <= 10; i++) {
				User student = new User();
				student.setName("Student " + i);
				student.setEmail("student" + i + "@gmail.com");
				student.setPassword("student123");
				student.setRole("STUDENT");
				userRepository.save(student);
			}
			
			
			for (int i = 1; i <= 2; i++) {
				User ta = new User();
				ta.setName("TA " + i);
				ta.setEmail("ta" + i + "@gmail.com");
				ta.setPassword("ta123");
				ta.setRole("TA");
				userRepository.save(ta);
			}
			
			
			User professor = new User();
			professor.setName("Professor Hamid");
			professor.setEmail("professor@gmail.com");
			professor.setPassword("prof123");
			professor.setRole("PROFESSOR");
			userRepository.save(professor);
			
			System.out.println("Dummy data created successfully!");
			System.out.println("Students: student1@gmail.com to student10@gmail.com (password: student123)");
			System.out.println("TAs: ta1@gmail.com, ta2@gmail.com (password: ta123)");
			System.out.println("Professor: professor@gmail.com (password: prof123)");
		};
	}

}
