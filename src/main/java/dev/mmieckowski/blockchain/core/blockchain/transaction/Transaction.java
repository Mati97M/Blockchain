package dev.mmieckowski.blockchain.core.blockchain.transaction;

import java.security.PublicKey;

public record Transaction(TransactionInfo data, byte[] signature, PublicKey publicKey) {
    @Override
    public String toString() {
        return data.toString();
    }
}