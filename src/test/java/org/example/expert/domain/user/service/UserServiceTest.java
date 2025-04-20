package org.example.expert.domain.user.service;

import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.common.util.PasswordEncoder;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
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
@DisplayName("UserServiceTest")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("유저 조회 테스트")
    class getUser {

        @Test
        @DisplayName("유저가 존재하지 않으면 예외를 던진다.")
        public void shouldThrowException_whenUserNotFound() {

            // given
            given(userRepository.findById(any())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                userService.getUser(1L);
            });

            // then
            assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("유저를 성공적으로 불러온다.")
        public void shouldSucceed_whenUserExists() {

            // given
            User mockUser = new User("test@email", "1234", UserRole.USER);
            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));

            // when
            UserResponse response = userService.getUser(1L);

            // then
            assertNotNull(response);
            assertEquals("test@email", response.getEmail());
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class changePassword {

        @Test
        @DisplayName("유저가 존재하지 않으면 예외를 던진다.")
        public void shouldThrowException_whenUserNotFound() {

            // given
            given(userRepository.findById(any())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                userService.changePassword(1L, new UserChangePasswordRequest());
            });

            // then
            assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("기존 비밀번호와 변경할 비밀번호가 같으면 예외를 던진다.")
        public void shouldThrowException_whenOldPasswordSameAsNewPassword() {

            // given
            User mockUser = new User("test@email", "1234", UserRole.USER);
            UserChangePasswordRequest request = new UserChangePasswordRequest("1234", "1234");

            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(request.getNewPassword(), mockUser.getPassword())).willReturn(true);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                userService.changePassword(1L, request);
            });

            // then
            assertEquals("기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호 검증에 실패하면 예외를 던진다.")
        public void shouldThrowException_whenPasswordDoesNotMatch() {

            // given
            User mockUser = new User("test@email", "1234", UserRole.USER);
            UserChangePasswordRequest request = new UserChangePasswordRequest("1234", "1234");

            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(request.getOldPassword(), mockUser.getPassword())).willReturn(false);

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                userService.changePassword(1L, request);
            });

            // then
            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("비밀번호를 성공적으로 변경한다.")
        public void shouldSucceed_whenPasswordIsChanged() {

            // given
            User mockUser = new User("test@email", "1234", UserRole.USER);
            UserChangePasswordRequest request = new UserChangePasswordRequest("1234", "12345");

            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches(request.getOldPassword(), mockUser.getPassword())).willReturn(true);
            given(passwordEncoder.matches(request.getNewPassword(), mockUser.getPassword())).willReturn(false);
            given(passwordEncoder.encode(request.getNewPassword())).willReturn("12345");

            // when
            userService.changePassword(1L, request);

            // then
            assertEquals(request.getNewPassword(), mockUser.getPassword());
        }
    }
}