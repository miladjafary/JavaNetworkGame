# Java Socket Programming

Java Network Game is a simple java client/server game which has been developed by Java Socket.

## Creating packages
Java Network Game can be package by maven. There are three maven profile for creating packages:
- **game-demo.jar** : this package is a simple demo of the game. you can package it and run it through bellow commands: 
```
mvn clean install 
java -jar game-demo.jar 
```
After running this package, A game server will be started on port "4444". The two player, Milad and Elena will be signUp 
on the game server. Then "milad" player start the game with "Elena" player. After sending 10 message to "Elena" and 
receiving back 10 message, the game will be finish.
- **game-server.jar** : A game server will be run on an specific port and players can connect to it. Run bellow 
commands for creating this package
```
mvn install -PgameServer 
java -jar game-server.jar 4040
```  
- **player-client.jar**: this package will be used for connecting to the running game server. It will accept 
"PlayerName", "GameServerHost", "GameServerPort" as inputs and then It will try to connect to the game server and 
register player as "PlayerName". Run bellow commands for creating this package
```
mvn install -PplayerClient
java -jar player-client.jar Elena localhost 4040
```  


## How to run the game as Client/Server game
For running the game as a client/server game, The following steps should be followed:

1. Run ``game-server.jar``
```
java -jar game-server.jar 4040
```
2. Run ``player-client.jar`` for first player(e.g considered "Milad" as a first player)
```
java -jar player-client.jar Milad localhost 4040
```
3. Run ``player-client.jar`` in different console for second player(e.g considered "Elena" as a second player)
```
java -jar player-client.jar Elena localhost 4040
```
4. "Elena" will see list of registered players in game server, choose one of them and start the game.
(e.g considered "Elena" choose "Milad" as her opponent players)
5. After sending and receiving 10 message from "Elena" to "Milad" the game will be finish.

#### Simple output of the game from "Elena" view:
```
2020-01-07 02:08:31.231 INFO  Client[main]: - Connecting to [localhost][4040]...
2020-01-07 02:08:31.343 INFO  Player[main]: - [Elena] Send: [signUpRequest|Elena]
2020-01-07 02:08:31.344 INFO  Player[Thread-0]: - [Elena] Received: [signUpResponse|Elena|OK|Milad,Elena]
Registered players in GameServer:
- Milad
Enter your opponent playerName:
Milad
2020-01-07 02:08:56.331 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me]
2020-01-07 02:08:56.340 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me|1]
2020-01-07 02:08:56.344 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1]
2020-01-07 02:08:56.349 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1|2]
2020-01-07 02:08:56.350 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2]
2020-01-07 02:08:56.357 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2|3]
2020-01-07 02:08:56.360 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3]
2020-01-07 02:08:56.365 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3|4]
2020-01-07 02:08:56.366 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3:4]
2020-01-07 02:08:56.373 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3:4|5]
2020-01-07 02:08:56.374 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3:4:5]
2020-01-07 02:08:56.382 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3:4:5|6]
2020-01-07 02:08:56.383 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3:4:5:6]
2020-01-07 02:08:56.388 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3:4:5:6|7]
2020-01-07 02:08:56.390 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3:4:5:6:7]
2020-01-07 02:08:56.396 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3:4:5:6:7|8]
2020-01-07 02:08:56.398 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3:4:5:6:7:8]
2020-01-07 02:08:56.405 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3:4:5:6:7:8|9]
2020-01-07 02:08:56.410 INFO  Player[Thread-0]: - [Elena] Send: [playRequest|Elena|Milad|Play with me:1:2:3:4:5:6:7:8:9]
2020-01-07 02:08:56.419 INFO  Player[Thread-0]: - [Elena] Received: [playResponse|Elena|Milad|Play with me:1:2:3:4:5:6:7:8:9|10]
2020-01-07 02:08:56.419 INFO  Player[Thread-0]: - [Elena] Game Over!
``` 

     
    

## Classes Responsibilities
In following paragraph you can find the responsibilities of each class:
- ``Server`` : this class is used for creating a TCP server and listing on a specific port
- ``Client`` : this class is used for making a TCP client connection to a TCP server. 
- ``Connection`` : this class accept an instance of ``java.net.Socket`` and act as connection handler.
In other word, thanks to this class you can read from a connection and also write on it.
- ``GameServer`` : this class is used for creating a TCP server which act as GameServer and players 
can connect to it.Generally ``GameServer`` act as switch between players.
In the following paragraph request processing by ``GameServer`` has been mentioned: 
    1. ``GameServer`` received play request from one player(Initiator) and send the request to the opponent.
    2. Opponent player received the request, process it, make the response and finally send it to to the ``GameServer``.
    3. ``GameServer`` send the response to the player(Initiator).
- ``Player``: this class is representative of real person player which can run the game scenario.
It accept a ``Client`` object which is connected to ``GameServer``. The game scenario is as follow:
    1. Player should register in the game sever through ``Player.signUp()`` method. 
    By calling this method a signUp request will send to the server in order to register in ``GameServer``.
    2. Player start game with other player by calling ``Player.playGameWith(opponentName, customeMessage)``. By calling 
    this method a playRequest will send to the ``GameServer`` in order to forward the request opponent player.
    3. When opponent player receives a message, it should reply with a message that contains the received message 
    concatenated with the value of a counter holding the number of messages this player already sent(i.e: ``Player.countOfReceivedMessages`)
    4. Player after receiving the response, increase ``Player.countOfReceivedMessages`` and if it less that 
    ``Player.finishThreshold`` repeat step 2, otherwise it will stop sending request and finish the game with opponent.
- ``GameServerMesssage``: is a class which is used making standard request between ``Player`` and ``GameServer``. 
This class can generate commands which are meaningful for ``GameServer`` and ``Player``.
- ``GameDemoRunner``: This class is used for running the game server on port "4444". The two player, Milad and Elena will be signUp 
 on the game server. Then "milad" player start the game with "Elena" player. After sending 10 message to "Elena" and 
 receiving back 10 message from "Milad" the game will be finish.
- ``GameServerRunner ``: This class can start a game server on an specific port. 
- ``PlayerRunner``: is a class which is used for connecting to a ``GameServer``. It can be accepted PlayerName, 
    GameServerHost and GameServerPort as parameters and then It will try to connect to the game server and registered player 
    as "PlayerName".    
 
### GameServer Commands
The game server commands are created by ``GameServerMessage``. All commands fields has been separated by ``|`` pipeline character.
Command format followed by bellow rule:

``Command|Field1|Field2|...``
- **SingUp Request**: This command is used for singUp the players in the ``GameServer``. The format of this command is as follow:
    
    ``signUpRequest|PlayerName``: which is
     - ``signUpRequest`` : Command
     - ``PlayerName`` : Name of player who wants register in to the GameServer.
- **SingUp Response**: This command is used for notifying player about singUp request . The format of this command is as follow:
    ``signUpResponse|PlayerName|SingUpResult|RegisteredPlayers``: which is
     - ``signUpResponse`` : Command
     - ``PlayerName`` : Name of player who wants register in to the GameServer.
     - ``SingUpResult`` : ``OK`` in case of player has successfully registered, otherwise ``PlayerNameExist``.
     - ``RegisteredPlayers``: in case of player has successfully registered, list of registered players will return by GameServer.
- **Play Request**: This command is used for sending a play request to an opponent through the ``GameServer``.
    The format of this command is as follow:
    
    ``playRequest|PlayerName|OpponentName|Message``: which is
    - ``playRequest``: Command
    - ``PlayerName``: Name of player who wants send request to an opponent.
    - ``OpponentName``: Name of opponent player who is responsible for processing the player request.
    - ``Message``: A custom message which is received by Opponent.
    
    **Example**: 
    
    - ``playRequest|Milad|Elena|Play with me``
- **Play response**: This command is used for sending a play request to an opponent through the ``GameServer``.
    The format of this command is as follow:
    
    ``playResponse|Player|Opponent|Message|countOfReceivedMessagesByOpponent``: which is
    - ``playRequest``: Command
    - ``PlayerName``: Name of player who sent the request to an opponent player.
    - ``OpponentName``: Name of opponent player who was generate the response of player request.
    - ``Message``: A custom message which is sent by player.
    
    **Example**: 
    
    - ``playResponse|Milad|Elena|Play with me|1``
     
