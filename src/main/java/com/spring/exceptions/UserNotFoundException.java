package com.spring.exceptions;

public class UserNotFoundException extends RuntimeException{
    /*
     * Required when we want to add a custom message when throwing the exception
     * as throw new UserNotFoundException(" Custom Unchecked Exception ");
     */
    public UserNotFoundException(String message) {
        // calling super invokes the constructors of all super classes
        // which helps to create the complete stacktrace.
        super(message);
    }
}
