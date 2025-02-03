package com.project.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MasterResponseDto<T> {
    private T data;
    private int statusCode;
    private boolean status;
    private String message;

    public MasterResponseDto(T data, int statusCode, Boolean status, String message) {
        this.data = data;
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
    }
}
