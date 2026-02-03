package com.m2e.dashboard.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.enums.ErrorMessageEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@ControllerAdvice
public class BizGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BizGlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleMethodArgumentNotValidException(final
                                                                   MethodArgumentNotValidException ex, WebRequest webRequest) {
        List<String> list = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
        logger.info("Error handleMethodArgumentNotValidException->{}",list);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1001"));
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleResourceNotFoundException(final
                                                             ResourceNotFoundException ex, WebRequest webRequest) {
        logger.info("Error handleResourceNotFoundException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1002"));
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<?> handleSQLException(HttpServletRequest request, Exception ex, WebRequest webRequest) throws IOException {
        logger.info("Error handleSQLException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1003"));
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseEntity<?> handleIOException(HttpServletRequest request, Exception ex, WebRequest webRequest){
        logger.info("Error handleIOException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1004"));
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpMessageNotReadableException(JsonProcessingException ex, WebRequest webRequest) {
        logger.info("Error handleHttpMessageNotReadableException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1005"));
        apiResponse.setMsgBody(ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public ResponseEntity<?> handleBizException(BizException ex, WebRequest webRequest) {
        logger.info("Error handleBizException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1006"));
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PaginationException.class)
    public ResponseEntity<String> handlePaginationException(PaginationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EnumNotFoundException.class)
    public ResponseEntity<String> handleEnumNotFoundException(EnumNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


    @ExceptionHandler(DowntimeException.class)
    @ResponseBody
    public ResponseEntity<?> handleDowntimeException(DowntimeException ex, WebRequest webRequest) {
        logger.info("Error handleDowntimeException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1070"));
        apiResponse.setMsgBody(ErrorMessageEnum.DOWNTIME_ERROR.getDescription());
        return new ResponseEntity<>(apiResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(OperationHourException.class)
    @ResponseBody
    public ResponseEntity<?> handleOperationHourException(OperationHourException ex, WebRequest webRequest) {
        logger.info("Error handleDowntimeException->{}",ex.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1080"));
        apiResponse.setMsgBody(ErrorMessageEnum.OPERATION_HOUR_ERROR.getDescription());
        return new ResponseEntity<>(apiResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(EntitlementException.class)
    public ResponseEntity<?> handleEntitlementException(EntitlementException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMsgHeader(new ErrorResponse("1008"));
        apiResponse.setMsgBody(ex.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }
}
