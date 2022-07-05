package ru.petsowner.exception;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final ErrorAttributes errorAttributes;

  @NonNull
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
    return handleBindingErrors(ex.getBindingResult(), request);
  }

  @NonNull
  @Override
  protected ResponseEntity<Object> handleBindException(
      BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status,
      @NonNull WebRequest request) {
    return handleBindingErrors(ex.getBindingResult(), request);
  }

  @ExceptionHandler(TooManyLoginAttemptsException.class)
  public ResponseEntity<?> tooManyLoginAttemptsException(WebRequest request) {
    return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null),
        HttpStatus.TOO_MANY_REQUESTS);
  }

  @ExceptionHandler(AppException.class)
  public ResponseEntity<?> appException(WebRequest request, AppException ex) {
    return createResponseEntity(getDefaultBody(request, ex.getOptions(), null), ex.getStatus());
  }

  private Map<String, Object> getDefaultBody(WebRequest request, ErrorAttributeOptions options,
      String msg) {
    Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
    if (msg != null) {
      body.put("message", msg);
    }
    return body;
  }

  private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
    String msg = result.getFieldErrors().stream()
        .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
        .collect(Collectors.joining("\n"));
    return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), msg),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @SuppressWarnings("unchecked")
  private <T> ResponseEntity<T> createResponseEntity(Map<String, Object> body, HttpStatus status) {
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
  }

}
