package dev.mmieckowski.blockchain.core.blockchain.transaction;

public record TransactionInfo(long transactionID, String senderName, String receiverName, long transactionAmount) {
    @Override
    public String toString() {
        return String.format("%d\n%s\n%s\n%d",
                transactionID,
                senderName,
                receiverName,
                transactionAmount
        );
    }
}