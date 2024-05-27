package dev.mmieckowski.blockchain;

import dev.mmieckowski.blockchain.actors.Actors;
import dev.mmieckowski.blockchain.actors.Client;
import dev.mmieckowski.blockchain.actors.Miner;
import dev.mmieckowski.blockchain.core.blockchain.Blockchain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Actors.CLIENTS_NUMBER + Actors.MINERS_NUMBER);

        for (Client client : Actors.CLIENTS) {
            executorService.execute(client);
        }
        for (Miner miner : Actors.MINERS) {
            executorService.execute(miner);
        }

        executorService.shutdown();
        try {
            while(!executorService.awaitTermination(240L, TimeUnit.SECONDS)) {
                if(!Blockchain.getInstance().isOpen()) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Blockchain.getInstance());
    }
}