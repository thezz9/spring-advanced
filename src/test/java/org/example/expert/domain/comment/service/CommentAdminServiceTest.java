package org.example.expert.domain.comment.service;

import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentAdminServiceTest")
class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class deleteComment {

        @Test
        @DisplayName("댓글이 존재하지 않으면 예외가 발생한다.")
        public void shouldThrowException_whenCommentDoesNotExist() {

            // given
            given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
                commentAdminService.deleteComment(1L);
            });

            // then
            assertEquals("댓글이 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("댓글이 존재하면 정상적으로 삭제된다.")
        public void shouldDeleteComment_whenCommentExists() {

            // given
            AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.USER);
            User mockUser = User.fromAuthUser(authUser);
            Todo mockTodo = new Todo("testTitle", "testContents", "testWeather", mockUser);
            Comment mockComment = new Comment("testContents", mockUser, mockTodo);

            given(commentRepository.findById(anyLong())).willReturn(Optional.of(mockComment));

            // when
            commentAdminService.deleteComment(1L);

            // then
            verify(commentRepository).deleteById(1L);
        }

    }
}