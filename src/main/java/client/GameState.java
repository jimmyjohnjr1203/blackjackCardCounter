package client;

import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

public class GameState {

    public UUID sessionId;
    public List<String> playerCards;
    public int playerValue;
    public List<String> dealerCards;
    public Integer dealerValue;
    public String phase;
    public String outcome;
    public int balance;
    public int currentBet;
    public boolean canHit;
    public boolean canStand;
    public boolean gameOver;
    public int cardsRemaining;
    public boolean reshuffled;

    public static GameState fromJson(String json) {
        return new Gson().fromJson(json, GameState.class);
    }

    @Override
    public String toString() 
    {
        return sessionId + "\n"+
            playerCards+" ("+playerValue+")\n"+
            dealerCards+" ("+dealerValue+")\n"+
            phase+"\n"+
            "outcome: "+outcome+"\n"+
            "balance: "+balance+"\n"+
            "currentBet: "+currentBet+"\n"+
            "canHit: "+canHit+"\n"+
            "canStand: "+canStand+"\n"+
            "gameOver: "+gameOver+"\n"+
            "cardsRemaining: "+cardsRemaining+"\n"+
            "reshuffled: "+reshuffled+"\n";
    }
}
