package client;

import java.time.LocalDateTime;
import java.util.UUID;



public class SessionSummary {
    public UUID sessionId;
    public int balance;
    public int currentBet;
    public String phase;
    public String outcome;
    public LocalDateTime lastAccess;
}
