package dev.mmieckowski.blockchain.actors;

import dev.mmieckowski.blockchain.actors.exceptions.SecurityManagerNotInitializedException;
import dev.mmieckowski.blockchain.actors.security.SecurityManager;
import dev.mmieckowski.blockchain.core.blockchain.Blockchain;
import dev.mmieckowski.blockchain.core.blockchain.transaction.VCReceiver;
import dev.mmieckowski.blockchain.core.blockchain.transaction.VCSender;
import lombok.Getter;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static dev.mmieckowski.blockchain.actors.Actors.CLIENTS;
import static dev.mmieckowski.blockchain.actors.Actors.CLIENTS_NUMBER;

public class Client implements Runnable, VCSender, VCReceiver {
    private static final Random RANDOM = new Random();

    @Getter
    private final String name;
    private final int id;
    private static final Blockchain blockchain = Blockchain.getInstance();
    private final SecurityManager securityManager;
    @Getter
    private final int generousityForClientsLevel;

    public Client(String name, int id) {
        this.generousityForClientsLevel = RANDOM.nextInt(CLIENTS_NUMBER);
        this.name = name;
        this.id = id;
        try {
            securityManager = new SecurityManager(1024);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityManagerNotInitializedException(e);
        }
    }

    @Override
    public void run() {
        while (blockchain.isOpen()) {
            int anotherClientIndex = getRandomlyAnotherClientIndex();
            sendVCTo(CLIENTS.get(anotherClientIndex), blockchain, securityManager);
        }
    }

    public static int getRandomlyClientIndex() {
        return RANDOM.nextInt(CLIENTS_NUMBER);
    }

    private int getRandomlyAnotherClientIndex() {
        int clientIndex;
        do {
            clientIndex = RANDOM.nextInt(CLIENTS_NUMBER);
        } while (clientIndex == id);
        return clientIndex;
    }

    @Override
    public String getType() {
        return "client";
    }
}