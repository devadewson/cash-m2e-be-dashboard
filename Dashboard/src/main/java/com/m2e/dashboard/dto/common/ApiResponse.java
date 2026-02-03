package com.m2e.dashboard.dto.common;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponse<T> {
    private ErrorResponse msgHeader;
    private T msgBody;

    public <T> ApiResponse<T> buildSuccess(T body) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("0"));
        response.setMsgBody(body);
        return response;
    }

    public ApiResponse<String> buildError(String code, String message) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse(code));
        response.setMsgBody(message);
        return response;
    }


}
