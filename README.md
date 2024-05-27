# Blockchain
Blockchain written in Java. Multi threaded application

## Stage 6/6:Local currency
### Description
Today, the most common application of blockchains is cryptocurrencies. A cryptocurrency’s blockchain contains a list of transactions: everyone can see the transactions but no one is able to change them. In addition, no one can send a transaction as another person; this is possible using digital signatures. You have actually implemented all of this functionality in the previous stages.

A miner who creates a new block should be awarded some virtual money, for example, 100 virtual coins. This can be remembered in the blockchain if the block stores information about the miner who created this block. Of course, this message also should be proved, so the miner adds this information to the blockchain before starting a search for a magic number.

After that, a miner can spend these 100 virtual coins by giving them to someone else. In the real world, he can buy things and pay for them using these virtual coins instead of real money. These virtual coins go to the company that sells the things, and the company can pay salaries with these virtual coins. The circulation of these coins starts here and suddenly the virtual coins become more popular than real money!

To check how many coins a person has, you need to check all of his transactions and all of the transactions to him, assuming that the person started with zero virtual coins. The transaction should be rejected when the person tries to spend more money than he has at the moment. Create a special method that returns how many coins the person has.

In this stage, you need to implement transactions like this instead of text messages like in the previous stage. For testing reasons you can assume that everyone starts with 100 virtual coins, not 0. But as described above, all the money of the blockchain is initially awards for creating blocks of the blockchain.
### Example
In the output example, VC stands for Virtual Coins. To be tested successfully, program should output information about first fifteen blocks of the blockchain. Blocks should be separated by an empty line:
</br>
Block:</br>
Created by: miner9</br>
miner9 gets 100 VC</br>
Id: 1</br>
Timestamp: 1539866031047</br>
Magic number: 76384756</br>
Hash of the previous block:</br>
0</br>
Hash of the block:</br>
1d12cbbb5bfa278734285d261051f5484807120032cf6adcca5b9a3dbf0e7bb3</br>
Block data:</br>
No transactions</br>
Block was generating for 0 seconds</br>
N was increased to 1</br>

Block:</br>
Created by: miner7</br>
miner7 gets 100 VC</br>
Id: 2</br>
Timestamp: 1539866031062</br>
Magic number: 92347234</br>
Hash of the previous block:</br>
1d12cbbb5bfa278734285d261051f5484807120032cf6adcca5b9a3dbf0e7bb3</br>
Hash of the block:</br>
04a6735424357bf9af5a1467f8335e9427af714c0fb138595226d53beca5a05e</br>
Block data:</br>
miner9 sent 30 VC to miner1</br>
miner9 sent 30 VC to miner2</br>
miner9 sent 30 VC to Nick</br>
Block was generating for 0 seconds</br>
N was increased to 2</br>

Block:</br>
Created by: miner1</br>
miner1 gets 100 VC</br>
Id: 3</br>
Timestamp: 1539866031063</br>
Magic number: 42374628</br>
Hash of the previous block:</br>
04a6735424357bf9af5a1467f8335e9427af714c0fb138595226d53beca5a05e</br>
Hash of the block:</br>
0061924d48d5ce30e97cfc4297f3a40bc94dfac6af42d7bf366d236007c0b9d3</br>
Block data:</br>
miner9 sent 10 VC to Bob</br>
miner7 sent 10 VC to Alice</br>
Nick sent 1 VC to ShoesShop</br>
Nick sent 2 VC to FastFood</br>
Nick sent 15 VC to CarShop</br>
miner7 sent 90 VC to CarShop</br>
Block was generating for 0 seconds</br>
N was increased to 3</br>

Block:</br>
Created by miner2</br>
miner2 gets 100 VC</br>
Id: 4</br>
Timestamp: 1539866256729</br>
Magic number: 45382978</br>
Hash of the previous block:</br>
0061924d48d5ce30e97cfc4297f3a40bc94dfac6af42d7bf366d236007c0b9d3</br>
Hash of the block:</br>
000856a20d767fbbc38e0569354400c1750381100984a09a5d8b1cdf09b0bab6</br>
Block data:</br>
CarShop sent 10 VC to Worker1</br>
CarShop sent 10 VC to Worker2</br>
CarShop sent 10 VC to Worker3</br>
CarShop sent 30 VC to Director1</br>
CarShop sent 45 VC to CarPartsShop</br>
Bob sent 5 VC to GamingShop</br>
Alice sent 5 VC to BeautyShop</br>
Block was generating for 5 seconds</br>
N was increased to 4</br>
</br>
... (another 10 blocks, so the output contains 15 blocks)
