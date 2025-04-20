package org.example.expert.domain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.service.CommentAdminService;
import org.example.expert.common.annotation.AdminLoggingTarget;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @AdminLoggingTarget
    @DeleteMapping("/admin/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") long commentId) {
        commentAdminService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
