package com.restauranthub.restaurant_user_api.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.restauranthub.restaurant_user_api.exceptions.DomainValidationException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDomain_devePreencherInstanceNoCampoPadraoDoProblemDetail() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/users/1");

        ProblemDetail body = handler.handleDomain(new DomainValidationException("Email já cadastrado"), request)
                .getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals(URI.create("/api/v1/users/1"), body.getInstance());
        assertNull(body.getProperties());
    }
}
