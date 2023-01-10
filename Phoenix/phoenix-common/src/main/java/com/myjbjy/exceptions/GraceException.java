package com.myjbjy.exceptions;

import com.myjbjy.grace.result.ResponseStatusEnum;

import java.util.Stack;

/**
 * @author myj
 */
public class GraceException {

    public static void display(ResponseStatusEnum statusEnum){
        throw new MyCustomException(statusEnum);
    }
}
