package com.example.kanban.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (uri != null && uri.startsWith("/api/")) || (accept != null && accept.contains("application/json"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        logger.warn("Resource not found: {} for path: {}", ex.getMessage(), request.getRequestURI());

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            body.put("path", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        model.addAttribute("status", 404);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler({UnauthorizedAccessException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleAccessDenied(Exception ex, HttpServletRequest request, Model model) {
        logger.warn("Access denied for path: {} - {}", request.getRequestURI(), ex.getMessage());

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.FORBIDDEN.value());
            body.put("error", "Forbidden");
            body.put("message", "You do not have permission to access or modify this resource");
            body.put("path", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }

        model.addAttribute("status", 403);
        model.addAttribute("error", "Access Denied");
        model.addAttribute("message", "You do not have permission to access or modify this resource.");
        model.addAttribute("path", request.getRequestURI());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        logger.error("Unhandled exception for path {}: ", request.getRequestURI(), ex);

        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", "Internal Server Error");
            body.put("message", "An unexpected error occurred while processing your request");
            body.put("path", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred. Our team has been notified.");
        model.addAttribute("path", request.getRequestURI());
        return "error/error";
    }
}
