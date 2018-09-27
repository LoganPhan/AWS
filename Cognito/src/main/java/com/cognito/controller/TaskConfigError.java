package com.cognito.controller;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskConfigError {

    private String exceptionType;

    private String message;
}
