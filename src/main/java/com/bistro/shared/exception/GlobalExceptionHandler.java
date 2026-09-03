package com.bistro.shared.exception;

import com.bistro.reservations.ReservationNotFoundException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toError)
                .toList();

        ProblemDetail problem = ProblemDetailConfig.validationProblem(errors);
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleReservationNotFound(ReservationNotFoundException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetailConfig.notFoundProblem(ex.getMessage());
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetailConfig.genericProblem(
                500,
                "Error interno del servidor",
                "Se produjo un error inesperado. Intente nuevamente más tarde."
        );
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    private Map<String, String> toError(FieldError fieldError) {
        return Map.of(
                "field", fieldError.getField(),
                "message", fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Valor inválido"
        );
    }
}
