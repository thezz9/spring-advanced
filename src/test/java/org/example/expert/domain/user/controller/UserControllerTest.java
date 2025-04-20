package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 조회 요청 성공")
    public void shouldGetUserSuccessfully() throws Exception {

        // given
        long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);

        // when
        mockMvc.perform(get("/users/" + userId)
                        .contentType("application/json")
                        .requestAttr("userId", authUser.getId())
                        .requestAttr("email", authUser.getEmail())
                        .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(userService).getUser(anyLong());
    }

    @Test
    @DisplayName("비밀번호 변경 요청 성공")
    public void shouldChangePasswordSuccessfully() throws Exception {

        // given
        long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("1234", "12345ABC!");

        // when
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr("userId", authUser.getId())
                        .requestAttr("email", authUser.getEmail())
                        .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(userService).changePassword(eq(userId), any(UserChangePasswordRequest.class));
    }
}