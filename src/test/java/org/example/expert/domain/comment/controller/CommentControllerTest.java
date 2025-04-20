package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 저장 요청 성공")
    public void shouldSaveCommentSuccessfully() throws Exception {

        // given
        long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
        CommentSaveRequest request = new CommentSaveRequest("contents");

        // when
        mockMvc.perform(post("/todos/" + todoId + "/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(commentService).saveComment(any(AuthUser.class), eq(todoId), any(CommentSaveRequest.class));
    }

    @Test
    @DisplayName("댓글 조회 요청 성공")
    public void shouldGetCommentsSuccessfully() throws Exception {

        // given
        long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);

        // when
        mockMvc.perform(get("/todos/" + todoId + "/comments")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(commentService).getComments(todoId);
    }

}