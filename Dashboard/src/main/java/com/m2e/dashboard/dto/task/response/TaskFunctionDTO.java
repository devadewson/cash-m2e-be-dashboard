package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskFunctionDTO implements Serializable {
    private String id;
    private String code;
    private String desc;
}
