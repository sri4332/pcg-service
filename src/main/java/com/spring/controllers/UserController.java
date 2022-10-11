package com.spring.controllers;

import com.spring.exceptions.UserExistException;
import com.spring.exceptions.UserNotFoundException;
import com.spring.payload.request.UserRequestPayload;
import com.spring.payload.response.FailureResponse;
import com.spring.payload.response.Response;
import com.spring.payload.response.SuccessResponse;
import com.spring.payload.response.UserInfoResponse;
import com.spring.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        logger.debug("Trying to access user details, id= " + id);
        try {
            UserInfoResponse userInfoResponse = userService.getUser(id, httpServletRequest);
            Response response = new SuccessResponse();
            response.setData(userInfoResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            Response response = new FailureResponse();
            response.setData(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createUser(@Valid @RequestBody UserRequestPayload userRequestPayload, HttpServletRequest httpServletRequest) {
        logger.debug("Trying to create new user, username= " + userRequestPayload.getUsername());
        try {
            UserInfoResponse userInfoResponse = userService.createUser(userRequestPayload, httpServletRequest);
            Response response = new SuccessResponse();
            response.setData(userInfoResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserExistException e) {
            e.printStackTrace();
            Response response = new FailureResponse();
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestPayload userRequestPayload, HttpServletRequest httpServletRequest) {
        logger.debug("Trying to update user details, username= " + userRequestPayload.getUsername());
        try {
            UserInfoResponse userInfoResponse = userService.updateUser(id, userRequestPayload, httpServletRequest);
            Response response = new SuccessResponse();
            response.setData(userInfoResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            Response response = new FailureResponse();
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        logger.debug("Trying to delete user, id= " + id);
        try {
            Boolean deleted = userService.deleteUser(id, httpServletRequest);
            Response response = new SuccessResponse();
            response.setData(deleted);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            Response response = new FailureResponse();
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}
