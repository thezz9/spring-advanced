package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentServiceTest")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private CommentService commentService;

    @Nested
    @DisplayName("댓글 저장 테스트")
    class saveComment {

        @Test
        @DisplayName("할 일을 찾을 수 없으면 예외를 던진다.")
        public void shouldThrowException_whenTodoNotFound() {

            // given
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("contents");
            AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

            given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                commentService.saveComment(authUser, todoId, request);
            });

            // then
            assertEquals("일정이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("정상적으로 코멘트를 저장한다.")
        public void shouldSaveComment_whenValidInput() {

            // given
            long todoId = 1;
            CommentSaveRequest request = new CommentSaveRequest("testContents");
            AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            Todo mockTodo = new Todo("testTitle", "testContents", "testWeather", mockUser);
            Comment mockComment = new Comment(request.getContents(), mockUser, mockTodo);

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(mockTodo));
            given(commentRepository.save(any())).willReturn(mockComment);

            // when
            CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

            // then
            assertNotNull(result);
        }

    }

    @Nested
    @DisplayName("댓글 조회 테스트")
    class getComments {

        @Test
        @DisplayName("등록된 코멘트가 없으면 빈 리스트를 반환한다.")
        public void shouldReturnEmptyList_whenNoCommentsExist() {

            // given
            given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(Collections.emptyList());

            // when
            List<CommentResponse> result = commentService.getComments(1L);

            // then
            assertThat(result).isEmpty();

        }

        @Test
        @DisplayName("정상적으로 코멘트를 반환한다.")
        public void shouldReturnCommentList_whenCommentsExist() {

            // given
            AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            Todo mockTodo = new Todo("testTitle", "testContents", "testWeather", mockUser);
            Comment mockComment = new Comment("testContents", mockUser, mockTodo);

            given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(List.of(mockComment));

            // when
            List<CommentResponse> result = commentService.getComments(1L);

            // then
            assertThat(result.get(0).getContents()).isEqualTo("testContents");
            assertThat(result.get(0).getUser().getEmail()).isEqualTo("test@email.com");
        }

    }
}
