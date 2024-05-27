package dev.mmieckowski.blockchain.core;

import dev.mmieckowski.blockchain.core.blockchain.transaction.TransactionInfo;

import java.util.List;
import java.util.stream.Collectors;

public record Block(long id, long timeStamp, int magicNum, String previousBlockHash, String hash, long seconds,
                    String minerName, List<TransactionInfo> transactionInfoList, String nInfo) {
    @Override
    public String toString() {
        String stringTransactionsSummary = "No transactions";
        if (transactionInfoList != null) {
            stringTransactionsSummary = transactionInfoList.stream()
                    .map(transactionInfo ->
                            String.format("%s sent %d VC to %s",
                                    transactionInfo.senderName(),
                                    transactionInfo.transactionAmount(),
                                    transactionInfo.receiverName()))
                    .collect(Collectors.joining("\n"));
        }

        return String.format(
                """
                        Block:
                        Created by %s
                        %s gets 100 VC
                        Id: %d
                        Timestamp: %d
                        Magic number: %d
                        Hash of the previous block:
                        %s
                        Hash of the block:
                        %s
                        Block data:
                        %s
                        Block was generating for %d seconds
                        %s""",
                minerName, minerName, id, timeStamp, magicNum, previousBlockHash, hash, stringTransactionsSummary, seconds, nInfo
        );
    }

    public Block(Block other) {
        this(other.id, other.timeStamp, other.magicNum, other.previousBlockHash, other.hash, other.seconds, other.minerName, other.transactionInfoList, other.nInfo);
    }
}