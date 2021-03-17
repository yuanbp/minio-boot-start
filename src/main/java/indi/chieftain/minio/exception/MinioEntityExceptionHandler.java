package indi.chieftain.minio.exception;

import io.minio.errors.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MinioEntityExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(value = {ErrorResponseException.class})
    protected ResponseEntity<Object> handleResponseError(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Received an error response while executing the requested operation!";
        MinioError error = new MinioError(HttpStatus.SERVICE_UNAVAILABLE, "The given endpoint number is not valid.");
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(value = {InsufficientDataException.class})
    protected ResponseEntity<Object> handleInsufficientDataError(RuntimeException ex, WebRequest request) {
        MinioError error = new MinioError(HttpStatus.SERVICE_UNAVAILABLE, "Reading given InputStream gets EOFException before reading given length.");
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentError(RuntimeException ex, WebRequest request) {
        MinioError error = new MinioError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleError(RuntimeException ex, WebRequest request) {
        MinioError error = new MinioError(HttpStatus.BAD_REQUEST, "An unexpected internal error occured while processing the request");
        ex.printStackTrace();
        return handleExceptionInternal(ex, error,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
