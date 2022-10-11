package com.spring.services;

import com.spring.controllers.AuthenticationController;
import com.spring.exceptions.UserExistException;
import com.spring.exceptions.UserNotFoundException;
import com.spring.models.UserRoles;
import com.spring.models.Role;
import com.spring.models.User;
import com.spring.payload.request.UserRequestPayload;
import com.spring.payload.response.UserInfoResponse;
import com.spring.repository.RoleRepository;
import com.spring.repository.UserRepository;
import com.spring.security.jwt.JwtUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
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
    public UserInfoResponse getUser(Long id, HttpServletRequest httpServletRequest) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("Error: Username is not found with given id!");
        }
        User user = optionalUser.get();
        logger.info("User details returned successfully, username=  "+user.getUsername());
        return new UserInfoResponse(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(Role::toString).collect(Collectors.toList()));

    }

    @Override
    public UserInfoResponse createUser(UserRequestPayload userRequestPayload, HttpServletRequest httpServletRequest) {
        if (userRepository.existsByUsername(userRequestPayload.getUsername())) {
            throw new UserExistException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(userRequestPayload.getEmail())) {
            throw new UserExistException("Error: Email is already taken!");
        }

        // Create new user's account
        User user = new User(userRequestPayload.getUsername(),
                userRequestPayload.getEmail(),
                encoder.encode(userRequestPayload.getPassword()),
                userRequestPayload.getFirstName(),
                userRequestPayload.getLastName());

        Set<String> strRoles = userRequestPayload.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(UserRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role eachRole = roleRepository.findByName(UserRoles.valueOf(role))
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(eachRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        logger.info("User created successfully, username=  "+user.getUsername());
        return new UserInfoResponse(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles.stream().map(Role::toString).collect(Collectors.toList()));
    }

    @Override
    public UserInfoResponse updateUser(Long id, UserRequestPayload userRequestPayload, HttpServletRequest httpServletRequest) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Error: Username is not found with given id!");
        }

        User user = new User(userRequestPayload.getUsername(),
                userRequestPayload.getEmail(),
                encoder.encode(userRequestPayload.getPassword()),
                userRequestPayload.getFirstName(),
                userRequestPayload.getLastName());
        user.setId(id);

        Set<String> strRoles = userRequestPayload.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(UserRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role eachRole = roleRepository.findByName(UserRoles.valueOf(role))
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(eachRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        logger.info("User updated successfully, username= "+user.getUsername());
        return new UserInfoResponse(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles.stream().map(Role::toString).collect(Collectors.toList()));

    }

    @Override
    public Boolean deleteUser(Long id, HttpServletRequest httpServletRequest) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Error: Username is not found with given id!");
        }
        userRepository.deleteById(id);
        logger.info("User deleted successfully, id="+id);
        return true;
    }
}
