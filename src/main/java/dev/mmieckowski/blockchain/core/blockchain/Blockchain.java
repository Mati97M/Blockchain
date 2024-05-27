package dev.mmieckowski.blockchain.core.blockchain;

import dev.mmieckowski.blockchain.core.Block;
import dev.mmieckowski.blockchain.core.blockchain.security.SignatureValidator;
import dev.mmieckowski.blockchain.core.blockchain.transaction.Transaction;
import dev.mmieckowski.blockchain.core.blockchain.transaction.TransactionInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static dev.mmieckowski.blockchain.core.blockchain.utilities.StringUtil.applySha256;


public class Blockchain {
    @Getter
    private static final Blockchain instance = new Blockchain();
    private static final int TARGET_SIZE = 15;
    private long totalValueVC = 0;
    private final StringBuilder output = new StringBuilder();
    private final Deque<Block> blockList = new LinkedList<>();
    @Getter
    private String prefix;
    @Getter
    private int leadingZerosNum;
    private final List<TransactionInfo> transactions = new LinkedList<>();
    @Getter
    private long identifier = 1;
    private final SignatureValidator signatureValidator = new SignatureValidator();
    private static final Random RANDOM = new Random();
    private final ReentrantLock transactionLock = new ReentrantLock();
    public static final long BLOCK_VALUE = 100L;

    @Override
    public String toString() {
        return output.toString();
    }

    public int getRandomAmount() {
        int amount;
        do {
            amount = RANDOM.nextInt((int) totalValueVC + 1);
        } while (amount == 0);
        return amount;
    }

    private Blockchain() {
        this.leadingZerosNum = 0;
        this.prefix = generatePrefix();
    }

    public int size() {
        return blockList.size();
    }

    public synchronized void addNewBlock(Block block) {
        if (!isOpen()) {
            return;
        }
        if (!isValid(block)) {
            return;
        }
        if (block.id() != blockList.size() + 1) {
            return;
        }
        int previousN = getLeadingZerosNum();
        adjustLeadingZeros(block);
        List<TransactionInfo> transactionInfoList = null;
        if (!blockList.isEmpty()) {
            transactionInfoList = getTransactionsSummary();
            if (transactionInfoList.isEmpty()) {
                transactionInfoList = null;
            }
        }
        block = new Block(
                block.id(),
                block.timeStamp(),
                block.magicNum(),
                block.previousBlockHash(),
                block.hash(),
                block.seconds(),
                block.minerName(),
                transactionInfoList,
                getNInfo(previousN, getLeadingZerosNum())
        );
        totalValueVC += BLOCK_VALUE;
        blockList.addLast(block);
        output.append(String.format("%s%n%n", block));
    }

    public String getNInfo(int previousN, int currentN) {
        String nInfo;
        if (currentN > previousN) {
            nInfo = "N was increased to " + currentN;
        } else if (currentN == previousN) {
            nInfo = "N stays the same";
        } else {
            nInfo = "N was decreased by " + (previousN - currentN);
        }
        return nInfo;
    }

    public Optional<Block> getLastBlock() {
        return Optional.ofNullable(blockList.peekLast()).map(Block::new);
    }

    private void adjustLeadingZeros(Block block) {
        long seconds = block.seconds();
        if (seconds >= 0 && seconds <= 2 /*&& leadingZerosNum < 5*/) { //&& leadingZerosNum < 5) {
            leadingZerosNum++;
            prefix = generatePrefix();

        } else if (seconds > 2 && leadingZerosNum > 0) {
            leadingZerosNum--;
            prefix = generatePrefix();
        }
    }

    private String generatePrefix() {
        return "0".repeat(leadingZerosNum);
    }

    private boolean isValid(Block block) {
        if (blockList.size() + 1 != block.id()) {
            return false;
        }
        if (!validateHashInBlock(block)) {
            return false;
        }

        Optional<Block> lastBlock = getLastBlock();
        if (lastBlock.isEmpty()) {
            return block.previousBlockHash().equals("0");
        }
        return isPreviousHashEqualToLast(block, lastBlock.get())
                && hashOfTheBlockMatchesThePatternHash(block);
    }

    private static boolean isPreviousHashEqualToLast(Block block, Block lastBlock) {
        return block.previousBlockHash().equals(lastBlock.hash());
    }

    private boolean validateHashInBlock(Block block) {
        String stringToHash = String.valueOf(block.id()) + block.timeStamp() + block.previousBlockHash() + block.magicNum();
        String expectedCurrHash = applySha256(stringToHash);
        return expectedCurrHash.equals(block.hash());
    }

    boolean hashOfTheBlockMatchesThePatternHash(Block block) {
        return block.hash().startsWith(prefix);
    }

    public synchronized List<TransactionInfo> getTransactionsSummary() {
        ArrayList<TransactionInfo> transactionInfos = new ArrayList<>(transactions);
        transactions.clear();
        return transactionInfos;
    }

    public boolean isOpen() {
        return TARGET_SIZE > blockList.size();
    }

    public void acceptTransaction(Supplier<Transaction> transactionSupplier) {
        transactionLock.lock();
        try {
            Transaction transaction = transactionSupplier.get();
            if (!signatureValidator.verifySignature(transaction)) {
                return;
            }
            TransactionInfo transactionDetails = transaction.data();
            if (!senderHasEnoughVC(transactionDetails)) {
                return;
            }
            transactions.add(transactionDetails);
            identifier++;
        } finally {
            transactionLock.unlock();
        }
    }

    private boolean senderHasEnoughVC(TransactionInfo transactionDetails) {
        return transactionDetails.transactionAmount() <= getAvailableVC(transactionDetails.senderName());
    }

    private long getAvailableVC(String senderName) {
        long senderAvailableVC = 0;
        for (Block block : blockList) {
            if (block.minerName().equals(senderName)) {
                senderAvailableVC += 100;
            }
            senderAvailableVC += getSenderBalanceFromTransactionsSummary(senderName, block.transactionInfoList());
        }
        senderAvailableVC += getSenderBalanceFromNotSubmittedTransactions(senderName);
        return senderAvailableVC;
    }

    private long getSenderBalanceFromNotSubmittedTransactions(String senderName) {
        long senderBalanceFromNotSubmittedTransactions = 0;
        for (TransactionInfo transactionInfo : transactions) {
            if (transactionInfo.senderName().equals(senderName)) {
                senderBalanceFromNotSubmittedTransactions -= transactionInfo.transactionAmount();
            }
            if (transactionInfo.receiverName().equals(senderName)) {
                senderBalanceFromNotSubmittedTransactions += transactionInfo.transactionAmount();
            }
        }
        return senderBalanceFromNotSubmittedTransactions;
    }

    private long getSenderBalanceFromTransactionsSummary(String name, List<TransactionInfo> transactionInfoList) {
        long balance = 0;
        if (transactionInfoList == null) {
            return 0;
        }
        for (TransactionInfo transactionSummary : transactionInfoList) {
            if (transactionSummary.senderName().equals(name)) {
                balance -= transactionSummary.transactionAmount();
            }
            if (transactionSummary.receiverName().equals(name)) {
                balance += transactionSummary.transactionAmount();
            }
        }
        return balance;
    }
}