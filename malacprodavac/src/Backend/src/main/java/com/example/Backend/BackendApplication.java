package com.example.Backend;

import com.example.Backend.config.FeatureSwitchProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
@RequiredArgsConstructor
public class BackendApplication {

	private final FeatureSwitchProvider featureSwitchProvider;

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(new ClassPathResource("serviceAccountKey.json").getInputStream());
		FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(googleCredentials).build();
		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
		return FirebaseMessaging.getInstance(app);
	}

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		SpringApplicationBuilder builder = new SpringApplicationBuilder(BackendApplication.class);
		builder.headless(true)
				.run(args);
	}

}
