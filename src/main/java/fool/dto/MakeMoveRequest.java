package fool.dto;

import fool.gameLogic.*;

public class MakeMoveRequest {
    private long deckID;
    private int playerNumber;
    private int cardNumber;

    public long getDeckID() {
        return deckID;
    }

    public void setDeckID(long deckID) {
        this.deckID = deckID;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }
}