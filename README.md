#Java Socket Programing

Java Network Game is a simple java client/server game which has been developed by Java Socket.

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
This class can generate commands which are meaningful for ``GameServer`` and ``Player``:
    

 
 
###GameServer Commands
The game server commands are created by ``GameServerMessage``. All commands fields has been separated by ``|`` pipeline character.
Command format followed by bellow rule:

``Command|Field1|Field2|...``
- **SingUp Request**: This command is used for singUp the players in the ``GameServer``. The format of this command is as follow:
    
    ``signUp|PlayerName``: which is
     - ``signUp`` : Command
     - ``PlayerName`` : Name of player who wants register in the GameServer.
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
     
