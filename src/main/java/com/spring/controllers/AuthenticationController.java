package com.spring.controllers;

import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.response.Response;
import com.spring.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private static final Logger logger = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<Response> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        logger.debug("User is trying to login " + loginRequest.getUsername());
        ResponseEntity<Response> responseEntity = authenticationService.authenticateUser(loginRequest, httpServletRequest);
        return responseEntity;
    }

    @PostMapping("/signup")
    public ResponseEntity<Response> registerUser(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest httpServletRequest) {
        logger.debug("User is trying to register " + signupRequest.getUsername());
        ResponseEntity<Response> responseEntity = authenticationService.registerUser(signupRequest, httpServletRequest);
        return responseEntity;
    }

    @PostMapping("/signout")
    public ResponseEntity<Response> logoutUser(HttpServletRequest httpServletRequest) {
        logger.debug("User is trying to logout ");
        ResponseEntity<Response> responseEntity = authenticationService.logoutUser(httpServletRequest);
        return responseEntity;
    }
}
