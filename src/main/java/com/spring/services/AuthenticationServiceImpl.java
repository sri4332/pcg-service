package com.spring.services;

import com.spring.controllers.AuthenticationController;
import com.spring.models.UserRoles;
import com.spring.models.Role;
import com.spring.models.User;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.payload.response.*;
import com.spring.repository.RoleRepository;
import com.spring.repository.UserRepository;
import com.spring.security.jwt.JwtUtils;
import com.spring.security.services.UserDetailsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LogManager.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<Response> authenticateUser(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        Response response = new SuccessResponse();
        response.setData(new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        logger.info("User successfully logged in " + loginRequest.getUsername());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response> registerUser(SignupRequest signupRequest, HttpServletRequest httpServletRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            Response response = new FailureResponse();
            response.setMessage("Error: Username is already taken!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            Response response = new FailureResponse();
            response.setMessage("Error: Email is already in used!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Adding new user
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()),signupRequest.getFirstName(),signupRequest.getLastName());

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(UserRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role adminRole = roleRepository.findByName(UserRoles.valueOf(role))
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        Response response = new SuccessResponse();
        response.setMessage("User registered successfully!");
        response.setData(new UserInfoResponse(user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles.stream().map(Role::toString).collect(Collectors.toList())));
        logger.info("User registered successfully " + user.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Response> logoutUser(HttpServletRequest httpServletRequest) {
        // invalidating cookie
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        Response response = new SuccessResponse();
        response.setMessage("You've been signed out!");
        logger.info("User logged out successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
