package com.study.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
// 데이터베이스 테이블 기준
public class RoleRegister {
    private int roleRegisterId;
    private int userId;
    private int roleId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private Role role;
}
