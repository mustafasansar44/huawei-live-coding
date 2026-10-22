package com.huawei.olympicranking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateMedalException extends RuntimeException {
    public DuplicateMedalException(String message) {
        super(message);
    }
}
