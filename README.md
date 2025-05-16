# Blackjack Client

Connects to a RESTful Blackjack server to play blackjack.

`BlackjackClient.java` is a simple command line client.

`BlackjackClientGUI.java` is a framework for building a simple GUI client.

You can try to build a command-line client that counts cards.

Or you can build a GUI client that looks great and provides a great user experience.

## Server commands
Our server is behind the campus firewall so you can only see it on campus.

You can see the full API here:

http://euclid.knox.edu:8080/api/blackjack/swagger-ui/index.html

Again, this only works on campus.

Here is the REST API:
![API](.github/images/blackjack-api.png)

Remember that the {id} in the URL is the session id of the game you are playing.

So this URL as a post would start a new session:

```
http://euclid.knox.edu:8080/api/blackjack/start?username=superman&password=12347
```

And you would get back a JSON file that looks something like this:
```
{
  "sessionId": "fb2e4f1c-0aa4-4157-96b5-4692f47cc801",
  "playerCards": [],
  "playerValue": 0,
  "dealerCards": [],
  "dealerValue": 0,
  "phase": "BETTING",
  "outcome": null,
  "balance": 0,
  "currentBet": 10,
  "canHit": false,
  "canStand": false,
  "gameOver": false,
  "cardsRemaining": 52,
  "reShuffled": false
}
```

The file `ClientConnector.java` handles all of the
communication with the server. It converts the JSON
response into an instance of the `GameState` class.


## Smart Blackjack Client

Run with gradle (runSmartCLI)

Will run 500 rounds showing bets over rounds, balance over rounds, and your balance over rounds if you weren't using card counting

Card counting is unfortunately not very effective

Uses hit/stand strategy based on https://www.blackjackapprenticeship.com/wp-content/uploads/2018/08/BJA_Basic_Strategy.jpg 

Uses card counting strategy based on Hi-Opt II system