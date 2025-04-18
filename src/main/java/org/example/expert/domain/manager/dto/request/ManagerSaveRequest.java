package org.example.expert.domain.manager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerSaveRequest {

    @NotNull(message = "아이디는 필수 입력값입니다.")
    private Long managerUserId; // 일정 작성자가 배치하는 유저 id
}
