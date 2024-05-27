package dev.mmieckowski.blockchain.core.blockchain.security;

import dev.mmieckowski.blockchain.core.blockchain.security.exceptions.ValidationException;
import dev.mmieckowski.blockchain.core.blockchain.transaction.Transaction;

import java.security.Signature;

public class SignatureValidator {
    public boolean verifySignature(Transaction transaction) {
        Signature sig;
        try {
            sig = Signature.getInstance("SHA1withRSA");

            sig.initVerify(transaction.publicKey());
            sig.update(transaction.data().toString().getBytes());
            return sig.verify(transaction.signature());
        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }
}