package org.example.expert.domain.user.service;

import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAdminServiceTest")
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Nested
    @DisplayName("유저 권한 변경 테스트")
    class changeUserRole {

        @Test
        @DisplayName("유저가 없으면 예외를 던진다.")
        public void shouldThrowException_whenUserNotFound() {

            // given
            given(userRepository.findById(any())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                userAdminService.changeUserRole(1L, new UserRoleChangeRequest());
            });


            // then
            assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("userRole을 성공적으로 변경한다.")
        public void shouldChangeUserRoleSuccessfully() {

            // given
            User mockUser = new User("test@email.com", "1234", UserRole.USER);
            UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));

            mockUser.updateRole(UserRole.of(request.getRole()));

            // when
            userAdminService.changeUserRole(1L, request);

            // then
            verify(userRepository).findById(any());
            assertEquals("ADMIN", mockUser.getUserRole().toString());
        }
    }
}