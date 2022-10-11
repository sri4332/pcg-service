package com.spring.services;

import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.response.Response;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    ResponseEntity<Response> authenticateUser(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

    ResponseEntity<Response> registerUser(SignupRequest signupRequest, HttpServletRequest httpServletRequest);

    ResponseEntity<Response> logoutUser(HttpServletRequest httpServletRequest);
}
