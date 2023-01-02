package com.myjbjy.exceptions;

import com.myjbjy.grace.result.ResponseStatusEnum;

/**
 * @author myj
 */
public class GraceException {

    public static void display(ResponseStatusEnum statusEnum){
        throw new MyCustomException(statusEnum);
    }
}
