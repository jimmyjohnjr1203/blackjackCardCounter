package client;

import java.security.InvalidParameterException;

public enum Card
{
    TWO_OF_CLUBS("2C"),
    THREE_OF_CLUBS("3C"),
    FOUR_OF_CLUBS("4C"),
    FIVE_OF_CLUBS("5C"),
    SIX_OF_CLUBS("6C"),
    SEVEN_OF_CLUBS("7C"),
    EIGHT_OF_CLUBS("8C"),
    NINE_OF_CLUBS("9C"),
    TEN_OF_CLUBS("10C"),
    JACK_OF_CLUBS("JC"),
    QUEEN_OF_CLUBS("QC"),
    KING_OF_CLUBS("KC"),
    ACE_OF_CLUBS("AC"),
    TWO_OF_DIAMONDS("2D"),
    THREE_OF_DIAMONDS("3D"),
    FOUR_OF_DIAMONDS("4D"),
    FIVE_OF_DIAMONDS("5D"),
    SIX_OF_DIAMONDS("6D"),
    SEVEN_OF_DIAMONDS("7D"),
    EIGHT_OF_DIAMONDS("8D"),
    NINE_OF_DIAMONDS("9D"),
    TEN_OF_DIAMONDS("10D"),
    JACK_OF_DIAMONDS("JD"),
    QUEEN_OF_DIAMONDS("QD"),
    KING_OF_DIAMONDS("KD"),
    ACE_OF_DIAMONDS("AD"),
    TWO_OF_HEARTS("2H"),
    THREE_OF_HEARTS("3H"),
    FOUR_OF_HEARTS("4H"),
    FIVE_OF_HEARTS("5H"),
    SIX_OF_HEARTS("6H"),
    SEVEN_OF_HEARTS("7H"),
    EIGHT_OF_HEARTS("8H"),
    NINE_OF_HEARTS("9H"),
    TEN_OF_HEARTS("10H"),
    JACK_OF_HEARTS("JH"),
    QUEEN_OF_HEARTS("QH"),
    KING_OF_HEARTS("KH"),
    ACE_OF_HEARTS("AH"),
    TWO_OF_SPADES("2S"),
    THREE_OF_SPADES("3S"),
    FOUR_OF_SPADES("4S"),
    FIVE_OF_SPADES("5S"),
    SIX_OF_SPADES("6S"),
    SEVEN_OF_SPADES("7S"),
    EIGHT_OF_SPADES("8S"),
    NINE_OF_SPADES("9S"),
    TEN_OF_SPADES("10S"),
    JACK_OF_SPADES("JS"),
    QUEEN_OF_SPADES("QS"),
    KING_OF_SPADES("KS"),
    ACE_OF_SPADES("AS");

    private String string;
    Card(String card) {
        this.string = card;
    }

    public static Card fromString(String card) {
        card = card.toUpperCase().replace(' ', '_');
        //convert text cards recieved from server to internal card representation strings
        String card_string = wordToValue(card.split("_")[0]) + wordToValue(card.split("_")[2]);
        for (Card c : Card.values()) {
            if (c.string.equals(card_string)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid card: " + card);
    }

    public String toString() {
        return string;
    }

    public String getFilename() {
        return string + ".png";
    }

    /** return the value of the given card, returns 11 for aces
     * 
    */
    public int getValue(){
        int card_val;
        try {
            card_val = Integer.parseInt(string.substring(0, 1));
        } catch (NumberFormatException e){ //the first char was J, Q, K or A
            if (string.charAt(0) == 'A')
                card_val = 11;
            else 
                card_val = 10;
        }
        return card_val;
    }
    private static String wordToValue(String word) throws InvalidParameterException {
        if (word.equals("TWO")) return "2";
        if (word.equals("THREE")) return "3";
        if (word.equals("FOUR")) return "4";
        if (word.equals("FIVE")) return "5";
        if (word.equals("SIX")) return "6";
        if (word.equals("SEVEN")) return "7";
        if (word.equals("EIGHT")) return "8";
        if (word.equals("NINE")) return "9";
        if (word.equals("TEN")) return "10";
        if (word.equals("JACK")) return "J";
        if (word.equals("QUEEN")) return "Q";
        if (word.equals("KING")) return "K";
        if (word.equals("ACE")) return "A";
        
        if (word.equals("CLUBS")) return "C";
        if (word.equals("DIAMONDS")) return "D";
        if (word.equals("HEARTS")) return "H";
        if (word.equals("SPADES")) return "S";

        throw new InvalidParameterException("Invalid word provided: "+word+" (should be all caps, name of a card)");
    }
}
