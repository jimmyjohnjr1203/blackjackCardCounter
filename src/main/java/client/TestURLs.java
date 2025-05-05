package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestURLs 
{
    static final String baseUrl = "http://euclid.knox.edu:8080/api/blackjack";
    static final String username = "dnduong";
    static final String password = "f1484a0";
    static final String params = "?username=" + username + "&password=" + password;

    static String makeURL(String endpoint) 
    {
        return baseUrl + endpoint + params;
    }

    public static void main(String[] args) throws Exception
    {
        rawJson();
        //convertJson();
        //makeBet();
    }

    public static void makeBet() throws Exception
    {
        String response = sendHTTPPostRequest(makeURL("/start"));
        GameState state = GameState.fromJson(response);
        System.out.println("GameState: " + state);
        System.out.println("Session ID: " + state.sessionId);
        System.out.println("Balance: " + state.balance);
        System.out.println("Cards remaining: " + state.cardsRemaining);
        
        // Make a bet
        String betResponse = sendHTTPPostRequest(makeURL("/" + state.sessionId + "/bet/10"));
        System.out.println("Bet Response: " + betResponse);
    }

    public static void convertJson()
    {
        String response = sendHTTPPostRequest(makeURL("/start"));
        GameState state = GameState.fromJson(response);
        System.out.println("Response: " + state);
    }

    public static void rawJson() throws Exception
    {
        String response = sendHTTPPostRequest(makeURL("/start"));
        System.out.println("Response: " + response);
    }

    public static String sendHTTPPostRequest(String url) 
    {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
