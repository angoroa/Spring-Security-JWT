package org.example.springjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration // 이 클래스가 Spring한테 Config 클래스로 등록되기 위해서
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // csrf disable
        http
                .csrf((auth)->auth.disable());

        // Form 로그인 방식 disalbe
        http
                .formLogin((auth)->auth.disable());

        // http basic 인증 방식 disable
        http
                .httpBasic((auth)->auth.disable());


        //  ! 람다식의 형태로 !
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll() // 로그인 , root, joim 한테는 모두 허용
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());
        // 세션 설정 ( 세션을 STATELESS로 설정해둬야함 )
        http
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
