package dev.mmieckowski.blockchain.core.blockchain.transaction.exceptions;

public class TransactionCreationException extends RuntimeException{
    public TransactionCreationException(Exception e) {
        super(e);
    }
}
