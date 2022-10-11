package com.spring.services;

import com.spring.payload.request.UserRequestPayload;
import com.spring.payload.response.UserInfoResponse;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    UserInfoResponse getUser(Long id, HttpServletRequest httpServletRequest);

    UserInfoResponse createUser(UserRequestPayload userRequestPayload, HttpServletRequest httpServletRequest);

    UserInfoResponse updateUser(Long id, UserRequestPayload userRequestPayload, HttpServletRequest httpServletRequest);

    Boolean deleteUser(Long id, HttpServletRequest httpServletRequest);
}
