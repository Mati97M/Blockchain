package dev.mmieckowski.blockchain.core.blockchain.transaction;

import dev.mmieckowski.blockchain.actors.security.SecurityManager;
import dev.mmieckowski.blockchain.core.blockchain.Blockchain;
import dev.mmieckowski.blockchain.core.blockchain.transaction.exceptions.TransactionCreationException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.function.Supplier;

public interface VCSender {
    int getGenerousityForClientsLevel();

    String getName();

    default void sendVC(Blockchain blockchain, Supplier<Transaction> transactionSupplier) {
        blockchain.acceptTransaction(transactionSupplier);
    }

    default void sendVCTo(VCReceiver receiver, Blockchain blockchain, SecurityManager securityManager) {
        String receiverType = receiver.getType();
        if (!receiverType.equals("miner") && !receiverType.equals("client")) {
            return;
        }
        int generousityLevel;
        if (receiverType.equals("miner")) {
            generousityLevel = getGenerousityForMinersLevel();
        } else {
            generousityLevel = getGenerousityForClientsLevel();
        }

        for (int i = 0; i < generousityLevel; i++) {
            sendVC(blockchain, () -> {
                TransactionInfo transactionDetails = new TransactionInfo(
                        blockchain.getIdentifier(),
                        getName(),
                        receiver.getName(),
                        Blockchain.getInstance().getRandomAmount()
                );
                try {
                    return new Transaction(
                            transactionDetails,
                            signTransaction(transactionDetails.toString(), securityManager),
                            getPublicKey(securityManager));

                } catch (Exception e) {
                    throw new TransactionCreationException(e);
                }
            });
        }
    }

    default int getGenerousityForMinersLevel() {
        return 0;
    }

    default byte[] signTransaction(String data, SecurityManager securityManager) {
        try {
            return securityManager.sign(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new TransactionCreationException(e);
        }
    }

    default PublicKey getPublicKey(SecurityManager securityManager) {
        return securityManager.getPublicKey();
    }
}