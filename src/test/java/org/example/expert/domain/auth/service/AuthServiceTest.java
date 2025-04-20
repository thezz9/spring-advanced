package org.example.expert.domain.auth.service;

import org.example.expert.common.exception.AuthException;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.common.util.JwtUtil;
import org.example.expert.common.util.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceTest")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("회원가입 테스트")
    class signup {

        @Test
        @DisplayName("이미 가입된 이메일이면 예외가 발생한다.")
        public void shouldThrowException_whenEmailAlreadyExists() {

            // given
            SignupRequest request = new SignupRequest();

            given(userRepository.existsByEmail(any())).willReturn(true);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                authService.signup(request);
            });

            // then
            assertEquals("이미 가입된 이메일입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("정상적으로 회원가입을 완료한다.")
        public void shouldSignupSuccessfully_whenValidRequest() {

            // given
            SignupRequest request = new SignupRequest("test@email.com", "1234", "USER");
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            UserRole userRole = UserRole.of(request.getUserRole());

            User mockUser = new User(request.getEmail(), encodedPassword, userRole);

            given(userRepository.save(any())).willReturn(mockUser);

            String token = jwtUtil.createToken(mockUser.getId(), mockUser.getEmail(), userRole);

            // when
            SignupResponse response = authService.signup(request);

            // then
            assertNotNull(response);
            assertEquals(token, response.getBearerToken());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class signin {

        @Test
        @DisplayName("존재하지 않는 이메일이면 예외가 발생한다.")
        public void shouldThrowException_whenEmailNotFound() {

            // given
            given(userRepository.findByEmail(any())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
               authService.signin(new SigninRequest());
            });

            // then
            assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다.")
        public void shouldThrowException_whenPasswordDoesNotMatch() {

            // given
            SigninRequest request = new SigninRequest("test@email.com", "1234");
            User mockUser = new User("test@email.com", "1234", UserRole.USER);
            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(any(), any())).willReturn(false);

            // when
            AuthException exception = assertThrows(AuthException.class, () -> {
               authService.signin(request);
            });

            // then
            assertEquals("이메일 또는 비밀번호가 일치하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("정상적인 입력으로 로그인에 성공한다.")
        public void signin_shouldSucceed_whenValidInput() {

            // given
            SigninRequest request = new SigninRequest("test@email.com", "1234");
            User mockUser = new User("test@email.com", "1234", UserRole.USER);
            given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(any(), any())).willReturn(true);
            given(jwtUtil.createToken(mockUser.getId(), mockUser.getEmail(), mockUser.getUserRole())).willReturn("mockToken");

            // when
            SigninResponse response = authService.signin(request);

            // then
            assertNotNull(response);
            assertEquals("mockToken", response.getBearerToken());
        }

    }
}