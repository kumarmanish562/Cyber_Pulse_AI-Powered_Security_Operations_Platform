package cyberpulse.common.exception;

import cyberpulse.common.response.ApiErrorResponse;
import cyberpulse.common.response.ApiErrorResponse.FieldErrorResponse;
import cyberpulse.common.web.CorrelationId;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
            BusinessException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "BUSINESS_RULE_VIOLATION",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        List<FieldErrorResponse> errors =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(this::toFieldError)
                        .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                request,
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {

        return build(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Request body is invalid or malformed",
                request,
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {

        return build(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "Invalid request parameter: " + exception.getName(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {

        return build(
                HttpStatus.BAD_REQUEST,
                "MISSING_PARAMETER",
                "Required parameter is missing: "
                        + exception.getParameterName(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You do not have permission to perform this operation",
                request,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {

        log.error(
                "Unexpected application error",
                exception
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request,
                List.of()
        );
    }

    private FieldErrorResponse toFieldError(
            FieldError error
    ) {
        return new FieldErrorResponse(
                error.getField(),
                error.getDefaultMessage()
        );
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> errors
    ) {

        String correlationId =
                request.getHeader(CorrelationId.HEADER);

        ApiErrorResponse response =
                new ApiErrorResponse(
                        Instant.now(),
                        status.value(),
                        code,
                        message,
                        request.getRequestURI(),
                        correlationId,
                        errors
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}