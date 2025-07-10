package com.example.config;


import com.example.security.JwtAuthenticationFilter;
import com.example.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 이 클래스가 설정 클래스임을 명시해줌
@EnableWebSecurity // Spring Security를 활성화하는 애너테이션
@RequiredArgsConstructor // final 필드를 가진 생성자를 자동 생성 ( 여기선 jwtTokenProvider 주입)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Spring Security의 필터 체인을 커스터마이징하는 메서드
    // HttpSecurity를 통해 세부 보안 규칙을 설정할 수 있다.
        http
                .csrf(csrf -> csrf.disable())
                // REST API에서 JWT인증을 쓸 경우에는 CSRF 토큰이 필요없기 떄문에 끈다. (CSRF 보호 비활성화)

                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화 -> 브라우저 기본 팝업으로 ID/PW 입력 받는 방식은 사용하지 않음
                .formLogin(formLogin -> formLogin.disable()) // form 기반 로그인 비활성화 -> Spring Security의 기본 로그인 폼도 사용 안함 ( 프론트엔드와 API 통신 전제로 하는 설정 )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/auth/**",
                            // swagger를 사용하기 위해서는 아래의 세 가지 엔드포인트를 허용해줘야 한다.
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html").permitAll()
                    // /auth/** 경로는 인증 없이 접근 가능 (회원가입, 로그인 등)
                    .anyRequest().authenticated()
                )
                    // 그 외 나머지 요청은 JWT 토큰을 통한 인증이 필요함
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
                // 요청의 JWT 토큰을 검사해서 인증 처리하는 커스텀 필터
                // UsernamePasswordAuthenticationFilter 앞에 등록해서, 스프링의 기존 인증 로직보다 먼저 실행되도록 함
                // 즉 요청마다 JWT 토큰을 꺼내서 유저 인증 여부를 판단하는 필터를 적용 !!
        return http.build();
    }

    @Bean
    // 비밀번호를 BCrypt 해서 알고리즘으로 암호화하는 인코더 제공
    // 회원가입 시 비밀번호를 안전하게 저장하고, 로그인 시 입력된 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교할 떄 사용
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
