package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.common.annotation.AdminLoggingTarget;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @AdminLoggingTarget
    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<Void> changeUserRole(@PathVariable("userId") long userId,
                                               @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        userAdminService.changeUserRole(userId, userRoleChangeRequest);
        return ResponseEntity.ok().build();
    }
}
