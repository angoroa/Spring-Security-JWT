package com.example;

import com.example.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class LoginPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginPracticeApplication.class, args);
    }

}
