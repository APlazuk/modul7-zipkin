package pl.aplazuk.orderms.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(String message) {
        super(message);
    }
}
