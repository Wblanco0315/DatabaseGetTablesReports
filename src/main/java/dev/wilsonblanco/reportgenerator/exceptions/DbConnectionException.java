package dev.wilsonblanco.reportgenerator.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DbConnectionException extends RuntimeException {

    private final HttpStatus status;

    public DbConnectionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
