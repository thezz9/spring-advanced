package org.example.expert.domain.manager.service;

import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ManagerServiceTest")
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Nested
    @DisplayName("매니저 저장 테스트")
    class saveManager {

        @Test
        @DisplayName("todo가 존재하지 않으면 예외가 발생한다.")
        public void shouldThrowException_whenTodoDoesNotExist() {

            // given
            long todoId = 1L;
            given(todoRepository.findById(todoId)).willReturn(Optional.empty());

            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
            assertEquals("일정이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("todo의 user가 null인 경우 예외가 발생한다.")
        public void shouldThrowException_whenTodosUserIsNull() {

            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            long todoId = 1L;
            long managerUserId = 2L;

            Todo todo = new Todo();
            ReflectionTestUtils.setField(todo, "user", null);

            ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

            // when & then
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
            );

            assertEquals("일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("등록하려는 담당자 유저가 존재하지 않을 경우 예외가 발생한다.")
        public void shouldThrowException_whenManagerUserDoesNotExist() {

            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            Todo mockTodo = new Todo("title", "contents", "weather", mockUser);
            long todoId = 1L;

            given(todoRepository.findById(any())).willReturn(Optional.of(mockTodo));
            given(userRepository.findById(any())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
               managerService.saveManager(authUser, todoId, new ManagerSaveRequest());
            });

            // then
            assertEquals("등록하려는 담당자 유저가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("등록하려는 담당자 유저가 작성자와 같을 경우 예외가 발생한다.")
        public void shouldThrowException_whenManagerUserSameAsTodoUser() {

            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            Todo mockTodo = new Todo("title", "contents", "weather", mockUser);
            ManagerSaveRequest request = new ManagerSaveRequest(1L);
            long todoId = 1L;

            given(todoRepository.findById(any())).willReturn(Optional.of(mockTodo));
            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                managerService.saveManager(authUser, todoId, request);
            });

            // then
            assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("매니저가 정상적으로 등록된다.")
        public void shouldSaveManager_whenValidInput() {

            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

            long todoId = 1L;
            Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

            long managerUserId = 2L;
            User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
            ReflectionTestUtils.setField(managerUser, "id", managerUserId);

            ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
            given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

            // then
            assertNotNull(response);
            assertEquals(managerUser.getId(), response.getUser().getId());
            assertEquals(managerUser.getEmail(), response.getUser().getEmail());
        }

    }

    @Nested
    @DisplayName("매니저 조회 테스트")
    class getManagers {

        @Test
        @DisplayName("매니저 목록 조회에 성공한다.")
        public void shouldReturnManagerList_whenManagersExist() {

            // given
            long todoId = 1L;
            User user = new User("user1@example.com", "password", UserRole.USER);
            Todo todo = new Todo("Title", "Contents", "Sunny", user);
            ReflectionTestUtils.setField(todo, "id", todoId);

            Manager mockManager = new Manager(todo.getUser(), todo);
            List<Manager> managerList = List.of(mockManager);

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

            // when
            List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

            // then
            assertEquals(1, managerResponses.size());
            assertEquals(mockManager.getId(), managerResponses.get(0).getId());
            assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
        }
    }

    @Nested
    @DisplayName("매니저 삭제 테스트")
    class deleteManager {

        @Test
        @DisplayName("todo의 user가 null인 경우 예외가 발생한다")
        public void shouldThrowException_whenTodoUserIsNull() {

            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            Todo mockTodo = new Todo("title", "contents", "weather", null); // 작성자 null

            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
            given(todoRepository.findById(any())).willReturn(Optional.of(mockTodo));

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                managerService.deleteManager(1L, 1L, 2L);
            });

            // then
            assertEquals("일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("todo 작성자와 요청한 유저가 다를 경우 예외가 발생한다")
        public void shouldThrowException_whenUserIsNotWriter() {

            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User requestUser = User.fromAuthUser(authUser);

            User todoOwner = new User("owner@email.com", "1234", UserRole.USER);

            Todo mockTodo = new Todo("title", "contents", "weather", todoOwner);

            given(userRepository.findById(any())).willReturn(Optional.of(requestUser));
            given(todoRepository.findById(any())).willReturn(Optional.of(mockTodo));

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                managerService.deleteManager(1L, 1L, 2L);
            });

            // then
            assertEquals("일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("매니저가 해당 todo에 속하지 않으면 예외가 발생한다.")
        public void shouldThrowException_whenManagerIsNotAssignedToTodo() {
            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User requestUser = User.fromAuthUser(authUser);

            Todo requestTodo = new Todo("request-todo", "content", "weather", requestUser);
            ReflectionTestUtils.setField(requestTodo, "id", 100L);

            Todo managerTodo = new Todo("manager-todo", "content", "weather", requestUser);
            ReflectionTestUtils.setField(managerTodo, "id", 200L);

            User managerUser = new User("manager@email.com", "1234", UserRole.USER);
            Manager manager = new Manager(managerUser, managerTodo);

            given(userRepository.findById(any())).willReturn(Optional.of(requestUser));
            given(todoRepository.findById(any())).willReturn(Optional.of(requestTodo));
            given(managerRepository.findById(any())).willReturn(Optional.of(manager));

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                managerService.deleteManager(1L, 100L, 2L);
            });

            // then
            assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
        }

        @Test
        @DisplayName("담당자 삭제에 성공한다.")
        public void shouldDeleteManager_whenValidInput() {

            // given
            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            User mockManagerUser = new User("test@email.com", "1234", UserRole.USER);
            Todo mockTodo = new Todo("title", "contents", "weather", mockUser);
            Manager mockManager = new Manager(mockManagerUser, mockTodo);

            given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
            given(todoRepository.findById(any())).willReturn(Optional.of(mockTodo));
            given(managerRepository.findById(any())).willReturn(Optional.of(mockManager));

            // when
            managerService.deleteManager(1L, 1L, 2L);

            // then
            verify(managerRepository).delete(mockManager);
        }
    }

}
