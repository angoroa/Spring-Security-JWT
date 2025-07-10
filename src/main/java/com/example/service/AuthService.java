package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.SignupRequest;
import com.example.dto.TokenResponse;
import com.example.entity.User;
import com.example.enums.UserRole;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.UserRepository;
import com.example.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
// final 또는 @NonNull 붙은 필드만 생성자 파라미터로 받음
// 그렇기 때문에 아래의 final 붙은 것들의 생성자
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(LoginRequest request){
        // 1. 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다.", ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("비밀번호가 일치하지 않습니다.", ErrorCode.INVALID_PASSWORD);
        }

        // 3. JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
        System.out.println("✅ 발급된 JWT: " + token);
        return new TokenResponse(token);
    }
    public void signup(SignupRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new CustomException("이미 존재하는 이메일입니다.", ErrorCode.DUPLICATE_EMAIL);
        // 사용자가 입력한 이메일이 이미 존재할 경우 !
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER) // 고정 설정
                .build();

        userRepository.save(user);

        log.info("회원가입 완료 : {}", user.getEmail());
    }

}
