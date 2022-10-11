package com.spring.exceptions;

public class UserExistException extends RuntimeException{
    /*
     * Required when we want to add a custom message when throwing the exception
     * as throw new UserExistException(" Custom Unchecked Exception ");
     */
    public UserExistException(String message) {
        // calling super invokes the constructors of all super classes
        // which helps to create the complete stacktrace.
        super(message);
    }
}
