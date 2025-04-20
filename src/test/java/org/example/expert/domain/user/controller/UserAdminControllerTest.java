package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {

    @MockBean
    private UserAdminService userAdminService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("권한 변경 요청 성공")
    public void shouldChangeUserRoleSuccessfully() throws Exception {

        // given
        long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
        UserRoleChangeRequest request = new UserRoleChangeRequest("USER");

        // when
        mockMvc.perform(patch("/admin/users/" + userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(userAdminService).changeUserRole(anyLong(), any());
    }
}