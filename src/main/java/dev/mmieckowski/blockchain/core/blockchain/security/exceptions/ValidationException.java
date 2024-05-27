package dev.mmieckowski.blockchain.core.blockchain.security.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException(Exception e) {
        super(e);
    }
}
