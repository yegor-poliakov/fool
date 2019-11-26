package fool.dto;

import fool.gameLogic.*;
import java.util.ArrayList;

public class GameState {
    private final GameStage stage;
    private final long deckId;
    private final Player player1;
    private final Player player2;
    private final ArrayList<Card> table;
    private final Card trumpCard;
    private final int remainingCards;

    public GameState(long deckID, GameStage gameStage, Deck deck){
        this.stage = gameStage;
        this.deckId = deckID;
        this.player1 = deck.firstPlayer;
        this.player2 = deck.secondPlayer;
        this.trumpCard = deck.deckOfCards.get(deck.deckOfCards.size() - 1);
        this.remainingCards = deck.deckOfCards.size();
        this.table = deck.table;
    }

    public long getDeckId() {
        return deckId;
    }

    public GameStage getStage() {
        return stage;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public ArrayList<Card> getTable() {
        return table;
    }

    public int getRemainingCards() {
        return remainingCards;
    }

    public Card getTrumpCard() {
        return trumpCard;
    }
}