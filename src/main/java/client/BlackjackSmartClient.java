package client;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartFactory;


public class BlackjackSmartClient {
    private static final String BASE_URL = "http://euclid.knox.edu:8080/api/blackjack";
    private static final String USERNAME = "jklubbs"; // replace with your username
    private static final String PASSWORD = "fdde45f"; // replace with your from the file posted to Classroom

    // strategies based on https://www.blackjackapprenticeship.com/wp-content/uploads/2018/08/BJA_Basic_Strategy.jpg 
    // soft means you have an ace counted as 11, if no ace or ace counted as 1, use hard
    // the arrays are flipped so row indices are low for low cards, false = stand and true = hit
    // x is dealer card, y is player total
    private static final boolean[][] hardStrategy = {
        // 2  ,  3  ,  4  ,  5  ,  6  ,  7  ,  8  ,  9  ,  10 ,  A
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //4 (2 and 2)
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //5
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //6
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //7
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //8
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //9
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //10
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //11
        {true ,true ,false,false,false,true ,true ,true ,true ,true }, //12
        {false,false,false,false,false,true ,true ,true ,true ,true }, //13
        {false,false,false,false,false,true ,true ,true ,true ,true }, //14
        {false,false,false,false,false,true ,true ,true ,true ,true }, //15
        {false,false,false,false,false,true ,true ,true ,true ,true }, //16
        {false,false,false,false,false,false,false,false,false,false}, //17
        {false,false,false,false,false,false,false,false,false,false}, //18
        {false,false,false,false,false,false,false,false,false,false}, //19
        {false,false,false,false,false,false,false,false,false,false}, //20 (10 and 10)
    };
    private static final boolean[][] softStrategy = {
        // 2  ,  3  ,  4  ,  5  ,  6  ,  7  ,  8  ,  9  ,  10 ,  A
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //12 (Ace and Ace)
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //13
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //14
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //15
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //16
        {true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, //17
        {false,false,false,false,false,false,false,true ,true ,true }, //18
        {false,false,false,false,false,false,false,false,false,false}, //19
        {false,false,false,false,false,false,false,false,false,false}  //20 (Ace and 9) (Ace and 10 = blackjack, not needed here)
    };

    //initialize strategy lists
    public static void main(String[] args) throws Exception {
        ClientConnecter clientConnecter = new ClientConnecter(BASE_URL, USERNAME, PASSWORD);
        
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the Smart Blackjack game!");
        System.out.println(
            "Do you want to start a new session or connect to an old session? \nI will connect to a session and play 100 rounds doing my best to make money");

        // List sessions
        System.out.println("Available sessions:");
        List<SessionSummary> sessions = clientConnecter.listSessions();
        int sessionNum = 1;
        for (SessionSummary session : sessions) {
            System.out.println("session number: " + sessionNum + " with Session ID: " + session.sessionId + ", Balance: " + session.balance);
            sessionNum++;
        }
        System.out.println("Enter session ID to connect to an old session or 'new' for a new session:");
        String sessionIdInput = input.nextLine().trim();
        UUID sessionId = null;
        GameState state = null;
        
        if (sessionIdInput.equalsIgnoreCase("new")) {
            // Start a new session
            System.out.println("A new session! Great idea.");
        } else if (sessionIdInput.matches("\\d+")) {
            // If the input is a number, treat it as an index
            int sessionIndex = Integer.parseInt(sessionIdInput) - 1;
            if (sessionIndex >= 0 && sessionIndex < sessions.size()) {
                sessionId = sessions.get(sessionIndex).sessionId;
                System.out.println("Connecting to session ID: " + sessionId);
            } else {
                System.out.println("Invalid session number. Starting a new session.");
            }
        } else {
            try {
                sessionId = UUID.fromString(sessionIdInput);
                System.out.println("Connecting to session ID: " + sessionId);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid session ID. Starting a new session.");
            }
        }
        if (sessionId == null) {
            // Start a new session
            System.out.println("Starting a new session...");
            state = clientConnecter.startGame();
        } else {
            // Connect to an existing session
            System.out.println("Connecting to session ID: " + sessionId);
            state = clientConnecter.resumeSession(sessionId);
        }
        //chart stuff

        XYSeries balanceData = new XYSeries("Balance", true, false);
        XYSeries bettingtData = new XYSeries("Bets", true, false);
        
        //Smart stuff

        int cardCount = 0; //increases or decreases if a deck is more or less favorable to you
        int defaultBet = 10; 

        int round = 1;

        int start_balance = state.balance;

        balanceData.add(0, start_balance);
        bettingtData.add(0, defaultBet);

        while (round <= 100) {
            System.out.println("\nYour balance: " + state.balance + " units");
            System.out.println("Cards remaining: " + state.cardsRemaining);
            if (state.cardsRemaining == 52){
                System.out.println("Cards reshuffled!");
                //reset card count
                cardCount = 0;
            }

            //int bet = defaultBet + (cardCount * 20); //if cardCount is positive, deck is favorable, should use a bigger bet,
            int bet;
            if (cardCount <= 0) bet = 10; //if the deck is not favorable, bet the minimum
            else bet = 10 + (cardCount/2) * 10; //if the deck is favorable, increase depending on how favorable, (half as much, integer division makes it ok)

            state = clientConnecter.placeBet(state.sessionId, bet);
            System.out.println("Bet: " + bet);
            //keep track of betting data for graph for debugging
            bettingtData.add(round, bet);
            printState(state);

            // Player turn loop
            while (!state.gameOver && state.canHit) {

                if (shouldIHit(state)) {
                    System.out.println("I hit!");
                    state = clientConnecter.hit(state.sessionId);
                } else {
                    System.out.println("I stand!");
                    state = clientConnecter.stand(state.sessionId);
                }
                printState(state);
            }
            // update the state of the deck after the round (based on all the cards on the table)
            List<Card> allCards = getCards(state.playerCards);
            allCards.addAll(getCards(state.dealerCards)); //so I dont need 2 for loops
            for (Card card : allCards){
                int val = card.getValue(); //assumes 11 for aces
                if (val <= 5) cardCount += 1;
                if (val >= 10) cardCount -= 1;
            }
            // System.out.println("Cards remaining: " + state.cardsRemaining);
            System.out.println("==> Outcome: " + state.outcome);
            System.out.println("Balance: " + state.balance + " units");

            balanceData.add(round, state.balance); //add the rounds info to data

            state = clientConnecter.newGame(state.sessionId);
            round++;
        }

        System.out.println("Original value: "+start_balance);
        System.out.println("Final value: "+state.balance);
        System.out.println("Difference: "+(state.balance-start_balance));
        System.out.println("Thanks for playing!");
        input.close();
        clientConnecter.finishGame(state.sessionId);

        //put data in dataset and chart
        XYSeriesCollection chartDataset = new XYSeriesCollection();
        chartDataset.addSeries(balanceData);
        chartDataset.addSeries(bettingtData);
        JFreeChart chartPlot = ChartFactory.createXYLineChart("Balance over Rounds", "Round", "Balance ($)", chartDataset);
        //show chart
        ChartFrame frame = new ChartFrame("Balance over Rounds", chartPlot);
        frame.pack();
        frame.setVisible(true); 
    }    

    private static void printState(GameState state) {
        // this is a stupid AI generated method
        try {
            System.out.println("Your cards: " + String.join(", ", state.playerCards) + " (value: " + state.playerValue + ")");
            if (state.dealerValue != null) {
                System.out.println("Dealer cards: " + String.join(", ", state.dealerCards) + " (value: " + state.dealerValue + ")");
            } else {
                System.out.println("Dealer shows: " + state.dealerCards.get(0));
            }
        } catch (RuntimeException e) {
            System.out.println(state);
            throw e;
        }
    }

    /** return whether or not the player should hit based on a player state
     * 
     */
    private static boolean shouldIHit(GameState state){ //returns true if the game state indicates that the player should hit
        
        //get player card values
        List<Card> playerCards = getCards(state.playerCards);
        //get dealer card values
        List<Card> dealerCards = getCards(state.dealerCards); //convert dealer cards to actual cards

        //decide whether the player value is soft/hard (soft means ace is currently counted as 11)
        boolean softState = false;
        if (playerCards.contains(Card.ACE_OF_DIAMONDS)
            || playerCards.contains(Card.ACE_OF_HEARTS)
            || playerCards.contains(Card.ACE_OF_CLUBS)
            || playerCards.contains(Card.ACE_OF_SPADES)){
                //check if the ace is being counted as a 1 or an 11
                //count as if the ace was an 11 and check if it matches the player value
                int highValue = 0;
                for (Card myCard : playerCards){
                    highValue += myCard.getValue();
                }
                if (highValue == state.playerValue) //the player value is a soft value
                    softState = true;
        } 


        //strategy based on this: https://www.blackjackapprenticeship.com/wp-content/uploads/2018/08/BJA_Basic_Strategy.jpg
        
        if (softState) {
            System.out.println("This hand is soft!");
            return getSoftStrategy(dealerCards.get(0), state.playerValue);
        }
        return getHardStrategy(dealerCards.get(0), state.playerValue);
    }

    /** 
     * get whether you should hit or stand according to the soft strategy array based on your player value and the dealers shown card
     */
    private static boolean getSoftStrategy(Card dealerCard, int playerValue){
        char dealerChar = dealerCard.toString().charAt(0); //'1' = 10
        int dealerIndex;
        if (dealerChar == 'A') dealerIndex = 9; //last column
        else if (dealerChar == '1' || dealerChar == 'J' || dealerChar == 'K' || dealerChar == 'Q') dealerIndex = 8;
        else dealerIndex = Integer.valueOf(String.valueOf(dealerChar)) - 2;

        int playerIndex = playerValue - 12;

        return softStrategy[playerIndex][dealerIndex];
    }
    
    /**
     *  get whether you should hit or stand according to the hard strategy array based on your player value and the dealers shown card 
     */ 
    private static boolean getHardStrategy(Card dealerCard, int playerValue){
        char dealerChar = dealerCard.toString().charAt(0); //'1' = 10
        int dealerIndex;
        if (dealerChar == 'A') dealerIndex = 9; //last column
        else if (dealerChar == '1' || dealerChar == 'J' || dealerChar == 'K' || dealerChar == 'Q') dealerIndex = 8;
        else dealerIndex = Integer.valueOf(String.valueOf(dealerChar)) - 2;

        int playerIndex = playerValue - 4; //hard strategy starts in a different spot

        return hardStrategy[playerIndex][dealerIndex];
    }
    
    // convert "THREE OF HEARTS" from server (or list of cards) to Card.THREE_OF_HEARTS
    private static List<Card> getCards(List<String> cardName) {
        List<Card> cards = new LinkedList<>();
        for (String card : cardName){
            if (card.equals("???")) continue; //skip explicitly unknown cards i.e. second dealer card
            cards.add(Card.fromString(card));
        }
        return cards;
    }
}
