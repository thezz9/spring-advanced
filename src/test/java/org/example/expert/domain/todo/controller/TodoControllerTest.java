package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @MockBean
    private TodoService todoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("할 일 저장 요청 성공")
    public void shouldSaveTodoSuccessfully() throws Exception {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
        TodoSaveRequest request = new TodoSaveRequest("title", "contents");

        // when
        mockMvc.perform(post("/todos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(todoService).saveTodo(any(AuthUser.class), any(TodoSaveRequest.class));
    }

    @Test
    @DisplayName("할 일 전체 조회 요청 성공")
    public void shouldGetTodosSuccessfully() throws Exception {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
        int page = 1;
        int size = 10;

        // when
        mockMvc.perform(get("/todos")
                .contentType("application/json")
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(todoService).getTodos(page, size);
    }

    @Test
    @DisplayName("할 일 단건 조회 요청 성공")
    public void shouldGetTodoSuccessfully() throws Exception {

        // given
        long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);

        // when
        mockMvc.perform(get("/todos/" + todoId)
                .contentType("application/json")
                .requestAttr("userId", authUser.getId())
                .requestAttr("email", authUser.getEmail())
                .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(todoService).getTodo(anyLong());
    }

}