package client;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;


public class BlackjackSmartClient {
    private static final String BASE_URL = "http://euclid.knox.edu:8080/api/blackjack";
    private static final String USERNAME = "jklubbs"; // replace with your username
    private static final String PASSWORD = "fdde45f"; // replace with your from the file posted to Classroom

    public static void main(String[] args) throws Exception {
        ClientConnecter clientConnecter = new ClientConnecter(BASE_URL, USERNAME, PASSWORD);
        
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the Smart Blackjack game!");
        System.out.println(
            "Do you want to start a new session or connect to an old session? \n I will connect to a session and play 50 rounds doing my best to make money");

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
        
        //Smart stuff

        //keep track of number of 10s, if there are many 

        int round = 0;

        int start_balance = state.balance;

        while (round < 50) {
            System.out.println("\nYour balance: " + state.balance + " units");
            System.out.println("Cards remaining: " + state.cardsRemaining);

            //System.out.print("Enter bet (must be multiple of 10): ");
            int bet = 10;
            // try {
            //     bet = Integer.parseInt(input.nextLine());
            //     if (bet % 10 != 0) {
            //         System.out.print("Bet was not a multiple of 10, bet set to 10");
            //         bet = 10;
            //     }
            // } catch (NumberFormatException e) {
            //     System.out.print("Bet was not a number, bet set to 10");
            //     bet = 10;
            // }
            state = clientConnecter.placeBet(state.sessionId, bet);
            printState(state);

            boolean hasReshuffled = false;
            // Player turn loop
            while (!state.gameOver && state.canHit) {
                // System.out.print("Action hit(h) / stand(s): ");
                // String action = input.nextLine().trim().toLowerCase();

                if (shouldIHit(state)) {
                    state = clientConnecter.hit(state.sessionId);
                    if (state.reshuffled) {
                        hasReshuffled = true;
                    }
                } else {
                    state = clientConnecter.stand(state.sessionId);
                    if (state.reshuffled) {
                        hasReshuffled = true;
                    }
                }
                printState(state);
                if (hasReshuffled) {
                    System.out.println("Cards reshuffled!");
                    hasReshuffled = false;
                } else {
                    System.out.println("Cards not reshuffled.");
                }
            }
            // System.out.println("Cards remaining: " + state.cardsRemaining);
            System.out.println("==> Outcome: " + state.outcome);
            System.out.println("Balance: " + state.balance + " units");

            // System.out.print("\nPlay again? yes(y) / no(n): ");
            // String playAgain = input.nextLine().trim().toLowerCase();
            // if (!playAgain.equals("yes") && !playAgain.equals("y")) {
            //     break;
            // }
            state = clientConnecter.newGame(state.sessionId);
            round++;
        }

        System.out.println("Original value: "+start_balance);
        System.out.println("Final value: "+state.balance);
        System.out.println("Difference: "+(state.balance-start_balance));
        System.out.println("Thanks for playing!");
        input.close();
        clientConnecter.finishGame(state.sessionId);

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

    private static boolean shouldIHit(GameState state){
        
        boolean have_ace = false;
        // check for aces
        for (String card : state.playerCards) {
            if (card.toLowerCase().contains("ace"))
                have_ace = true;
        }
        if (have_ace && state.playerValue < 21 && state.dealerValue != null && state.playerValue < state.dealerValue) { // a "soft" value
            return true;
        }
        if (state.playerValue <= 13 && state.dealerValue != null && state.playerValue < state.dealerValue) {
            return true;
        }

        return false;
    }
}
