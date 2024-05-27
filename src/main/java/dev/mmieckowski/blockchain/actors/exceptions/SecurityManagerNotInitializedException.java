package dev.mmieckowski.blockchain.actors.exceptions;

public class SecurityManagerNotInitializedException extends RuntimeException {
    public SecurityManagerNotInitializedException(Exception e) {
        super(e);
    }
}