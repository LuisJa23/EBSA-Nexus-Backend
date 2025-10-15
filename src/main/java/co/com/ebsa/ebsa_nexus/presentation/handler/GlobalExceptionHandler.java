package co.com.ebsa.ebsa_nexus.presentation.handler;

import co.com.ebsa.ebsa_nexus.application.dto.response.ValidationErrorResponse;
import co.com.ebsa.ebsa_nexus.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Centraliza el manejo de todas las excepciones de dominio y validación,
 * proporcionando respuestas HTTP consistentes y logging apropiado.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Maneja excepciones cuando un usuario no es encontrado.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Usuario no encontrado",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Maneja excepciones cuando un usuario ya existe (email o username duplicado).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Usuario ya existe",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Maneja excepciones de operaciones no autorizadas.
     */
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOperation(UnauthorizedOperationException ex) {
        log.error("Unauthorized operation: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Operación no autorizada",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Maneja excepciones de autenticación existentes.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Error de autenticación",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Maneja excepciones cuando la contraseña es inválida.
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex) {
        log.error("Invalid password error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Contraseña inválida",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja excepciones cuando se intenta crear/actualizar con campos duplicados.
     * Siempre devuelve el formato con validationErrors para consistencia.
     */
    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<co.com.ebsa.ebsa_nexus.application.dto.response.MultipleDuplicateFieldsResponse> 
            handleDuplicateField(DuplicateFieldException ex) {
        log.error("Duplicate field error: {} - {}", ex.getField(), ex.getValue());
        // Convertir a formato de Map para consistencia
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getField(), ex.getMessage());
        
        co.com.ebsa.ebsa_nexus.application.dto.response.MultipleDuplicateFieldsResponse error = 
            new co.com.ebsa.ebsa_nexus.application.dto.response.MultipleDuplicateFieldsResponse(
                "DUPLICATE_FIELDS",
                "Se encontraron campos duplicados",
                errors
            );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja excepciones cuando múltiples campos tienen valores duplicados.
     * Retorna todos los campos duplicados en una sola respuesta.
     */
    @ExceptionHandler(MultipleDuplicateFieldsException.class)
    public ResponseEntity<co.com.ebsa.ebsa_nexus.application.dto.response.MultipleDuplicateFieldsResponse> 
            handleMultipleDuplicateFields(MultipleDuplicateFieldsException ex) {
        log.error("Multiple duplicate fields error: {}", ex.getDuplicateFields());
        co.com.ebsa.ebsa_nexus.application.dto.response.MultipleDuplicateFieldsResponse error = 
            new co.com.ebsa.ebsa_nexus.application.dto.response.MultipleDuplicateFieldsResponse(
                "DUPLICATE_FIELDS",
                "Se encontraron campos duplicados",
                ex.getDuplicateFields()
            );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja excepciones cuando el WorkRole no coincide con el WorkType.
     */
    @ExceptionHandler(InvalidWorkRoleException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidWorkRole(InvalidWorkRoleException ex) {
        log.error("Invalid work role error: {} for {}", ex.getWorkRole(), ex.getWorkType());
        ValidationErrorResponse error = new ValidationErrorResponse(
            "INVALID_WORK_ROLE",
            ex.getMessage(),
            "workRole",
            ex.getWorkRole()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja errores de validación de datos de entrada.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseOld> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponseOld errorResponse = new ValidationErrorResponseOld(
            HttpStatus.BAD_REQUEST.value(),
            "Datos de entrada inválidos",
            errors,
            LocalDateTime.now()
        );
        
        log.error("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Maneja excepciones generales no capturadas específicamente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ha ocurrido un error inesperado. Por favor contacte al administrador.",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * DTO para respuestas de error estándar.
     */
    public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
    ) {}
    
    /**
     * DTO para respuestas de errores de validación (legacy para MethodArgumentNotValidException).
     */
    public record ValidationErrorResponseOld(
        int status,
        String error,
        Map<String, String> validationErrors,
        LocalDateTime timestamp
    ) {}
}