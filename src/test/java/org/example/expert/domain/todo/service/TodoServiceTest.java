package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceTest")
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Nested
    @DisplayName("할 일 저장 테스트")
    class saveTodo {

        @Test
        @DisplayName("todo를 성공적으로 저장한다.")
        public void shouldSaveTodoSuccessfully() {

            // given
            AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            String todayWeather = weatherClient.getTodayWeather();
            TodoSaveRequest request = new TodoSaveRequest("title", "contents");
            Todo mockTodo = new Todo(request.getTitle(), request.getContents(), todayWeather, mockUser);

            given(todoRepository.save(any())).willReturn(mockTodo);

            // when
            TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, request);

            // then
            assertEquals(mockTodo.getTitle(), todoSaveResponse.getTitle());
            assertEquals(mockTodo.getContents(), todoSaveResponse.getContents());
        }
    }

    @Nested
    @DisplayName("할 일 전체 조회 테스트")
    class getTodos {

        @Test
        @DisplayName("todos를 성공적으로 불러온다.")
        public void shouldFetchTodosSuccessfully() {

            // given
            int page = 10, size = 10;
            Pageable pageable = PageRequest.of(page - 1, size);

            AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            String todayWeather = "testWeather";

            Todo mockTodo = new Todo("title", "contents", todayWeather, mockUser);
            List<Todo> todoList = List.of(mockTodo);
            Page<Todo> todoPage = new PageImpl<>(todoList, pageable, todoList.size());

            given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todoPage);

            // when
            Page<TodoResponse> result = todoService.getTodos(page, size);

            // then
            assertEquals(1, result.getContent().size());
            TodoResponse response = result.getContent().get(0);
            assertEquals("title", response.getTitle());
            assertEquals("contents", response.getContents());
        }
    }

    @Nested
    @DisplayName("할 일 단건 조회 테스트")
    class getTodo {

        @Test
        @DisplayName("todo가 존재하지 않으면 예외를 던진다.")
        public void shouldThrowException_whenTodoDoesNotExist() {

            // given
            given(todoRepository.findByIdWithUser(any())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                todoService.getTodo(1L);
            });

            // then
            assertEquals("일정이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("todo를 성공적으로 불러온다.")
        public void shouldFetchTodoSuccessfully() {

            // given
            User mockUser = new User("test@email.com", "1234", UserRole.USER);
            Todo mockTodo = new Todo("title", "contents", "weather", mockUser);

            given(todoRepository.findByIdWithUser(any())).willReturn(Optional.of(mockTodo));

            // when
            TodoResponse response = todoService.getTodo(1L);

            // then
            assertEquals(mockTodo.getTitle(), response.getTitle());
            assertEquals(mockTodo.getContents(), response. getContents());

        }
    }
}