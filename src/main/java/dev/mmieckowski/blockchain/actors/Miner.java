package dev.mmieckowski.blockchain.actors;

import dev.mmieckowski.blockchain.actors.exceptions.SecurityManagerNotInitializedException;
import dev.mmieckowski.blockchain.actors.security.SecurityManager;
import dev.mmieckowski.blockchain.core.Block;
import dev.mmieckowski.blockchain.core.blockchain.Blockchain;
import dev.mmieckowski.blockchain.core.blockchain.transaction.VCReceiver;
import dev.mmieckowski.blockchain.core.blockchain.transaction.VCSender;
import dev.mmieckowski.blockchain.core.blockchain.utilities.Timer;
import lombok.Getter;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static dev.mmieckowski.blockchain.actors.Actors.CLIENTS;
import static dev.mmieckowski.blockchain.actors.Actors.CLIENTS_NUMBER;
import static dev.mmieckowski.blockchain.actors.Actors.MINERS;
import static dev.mmieckowski.blockchain.actors.Actors.MINERS_NUMBER;
import static dev.mmieckowski.blockchain.core.blockchain.utilities.StringUtil.applySha256;

public class Miner implements Runnable, VCSender, VCReceiver {
    private static final Random RANDOM = new Random();
    private final SecurityManager securityManager;
    @Getter
    private final int generousityForClientsLevel;
    @Getter
    private final int generousityForMinersLevel;

    private static final Blockchain blockchain = Blockchain.getInstance();
    private final Timer timer = new Timer();
    @Getter
    public final String name;
    private final int id;

    public Miner(int id) {
        this.id = id;
        this.name = "miner" + id;
        this.generousityForMinersLevel = RANDOM.nextInt(MINERS_NUMBER - 1);
        this.generousityForClientsLevel = RANDOM.nextInt(CLIENTS_NUMBER);
        try {
            securityManager = new SecurityManager(1024);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityManagerNotInitializedException(e);
        }
    }

    @Override
    public void run() {
        while (blockchain.isOpen()) {
            Block newBlock = null;
            try {
                newBlock = mineNewBlock();
            } catch (InterruptedException e) {
                System.out.println(e);
                return;
            }
            blockchain.addNewBlock(newBlock);
            sendVCTo(MINERS.get(getRandomlyMinerIndex()), blockchain, securityManager);
            sendVCTo(CLIENTS.get(Client.getRandomlyClientIndex()), blockchain, securityManager);
        }
    }

    private int getRandomlyMinerIndex() {
        int index;
        do {
            index = RANDOM.nextInt(MINERS_NUMBER);
        } while (index == this.id);
        return index;
    }

    Block mineNewBlock() throws InterruptedException {
        timer.restart();
        int lastBlockChainSize = blockchain.size();
        String prefix = blockchain.getPrefix();
        Optional<Block> lastBlock = blockchain.getLastBlock();
        String previousBlockHash = "0";
        if (lastBlock.isPresent()) {
            previousBlockHash = lastBlock.get().hash();
        }
        int magicNum;
        String hash = "";
        long timeStamp;
        long blockID = lastBlockChainSize + 1L;
        do {
            if(Thread.interrupted()) {
                throw new InterruptedException();
            }
            magicNum = RANDOM.nextInt();
            timeStamp = new Date().getTime();
            String stringToHash = String.valueOf(blockID) + timeStamp + previousBlockHash + magicNum;
            if (blockchain.isOpen()) {
                hash = applySha256(stringToHash);
            }
        } while (hashDoesNotMatchPrefix(hash, prefix) && blockchain.isOpen());

        long durationInSecs = timer.restart().toSeconds();

        return new Block(
                blockID,
                timeStamp,
                magicNum,
                previousBlockHash,
                hash,
                durationInSecs,
                name,
                null,
                ""
        );
    }

    private static boolean hashDoesNotMatchPrefix(String hash, String prefix) {
        return !hash.startsWith(prefix);
    }

    @Override
    public String getType() {
        return "miner";
    }
}