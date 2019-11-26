package fool.gameLogic;

public class Card {
    Suit suit;
    int rank;
    boolean isVisible;
    boolean isTrump;
    boolean inPlay;

    public Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visibility) {
        this.isVisible = visibility;
    }

    public boolean isTrump() {
        return isTrump;
    }

}