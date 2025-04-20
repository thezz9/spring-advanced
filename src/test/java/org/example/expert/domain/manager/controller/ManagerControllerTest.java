package org.example.expert.domain.manager.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.util.JwtUtil;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("매니저 등록 성공")
    public void shouldSaveManagerSuccessfully() throws Exception {

        // given
        long todoId = 1L;
        long managerId = 2L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
        ManagerSaveRequest request = new ManagerSaveRequest(managerId);

        // when
        mockMvc.perform(post("/todos/" + todoId + "/managers")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(managerService).saveManager(any(AuthUser.class), eq(todoId), any(ManagerSaveRequest.class));
    }

    @Test
    @DisplayName("매니저 조회 성공")
    public void shouldGetManagersSuccessfully() throws Exception {

        // given
        long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);

        // when
        mockMvc.perform(get("/todos/" + todoId + "/managers")
                .contentType("application/json")
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(managerService).getManagers(eq(todoId));
    }

    @Test
    @DisplayName("매니저 삭제 성공")
    public void shouldDeleteManagerSuccessfully() throws Exception {

        // given
        long todoId = 1L;
        long managerId = 2L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);

        String fakeToken = "Bearer fakefakefakefake";
        String mockToken = fakeToken.substring(7);

        when(jwtUtil.createToken(anyLong(), any(), any())).thenReturn(mockToken);

        Claims mockClaims = Jwts.claims().setSubject(authUser.getId().toString());
        mockClaims.put("email", authUser.getEmail());
        mockClaims.put("role", authUser.getUserRole().name());

        when(jwtUtil.extractClaims(mockToken)).thenReturn(mockClaims);

        // when
        mockMvc.perform(delete("/todos/" + todoId + "/managers/" + managerId)
                .contentType("application/json")
                .header("Authorization", fakeToken))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(managerService).deleteManager(anyLong(), anyLong(), anyLong());
    }

}