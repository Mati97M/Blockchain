package dev.mmieckowski.blockchain.actors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Actors {
    public static final int CLIENTS_NUMBER = 5;
    public static final int MINERS_NUMBER = 20;
    public static final List<Client> CLIENTS;
    public static final List<Miner> MINERS;

    static {
        CLIENTS = Stream.iterate(0, i -> i < CLIENTS_NUMBER, i -> i + 1)
                .map(number -> new Client("Client" + number, number))
                .toList();

        MINERS = Stream.iterate(0, i -> i < MINERS_NUMBER, i -> i + 1)
                .map(Miner::new)
                .toList();
    }
}