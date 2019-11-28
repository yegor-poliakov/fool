package fool.gameLogic;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public ArrayList<Card> deckOfCards;
    public ArrayList<Card> table = new ArrayList<Card>();
    public Player firstPlayer = new Player();
    public Player secondPlayer = new Player();

    public Deck() {
        deckOfCards = createDeck();

        int lowestTrump1 = 11;
        int highestRank1 = 0;
        for (int i = 0; i < 6; i++) {
            if (deckOfCards.get(0).isTrump && deckOfCards.get(0).rank < lowestTrump1) {
                lowestTrump1 = deckOfCards.get(0).rank;
            }
            if (deckOfCards.get(0).rank > highestRank1) {
                highestRank1 = deckOfCards.get(0).rank;
            }
            firstPlayer.playerHand.add(deckOfCards.remove(0));
        }

        int lowestTrump2 = 11;
        int highestRank2 = 0;
        for (int i = 0; i < 6; i++) {
            if (deckOfCards.get(0).isTrump && deckOfCards.get(0).rank < lowestTrump2) {
                lowestTrump2 = deckOfCards.get(0).rank;
            }
            if (deckOfCards.get(0).rank > highestRank2) {
                highestRank2 = deckOfCards.get(0).rank;
            }
            secondPlayer.playerHand.add(deckOfCards.remove(0));
        }

        if (lowestTrump1 < lowestTrump2) {
            firstPlayer.activityStatus = true;
            firstPlayer.offence = true;
        } else if (highestRank1 > highestRank2) {
            firstPlayer.activityStatus = true;
            firstPlayer.offence = true;
        } else {
            secondPlayer.activityStatus = true;
            secondPlayer.offence = true;
        }
        sortHand(firstPlayer);
        sortHand(secondPlayer);
    }

    public static ArrayList<Card> createDeck() {
        ArrayList<Card> deck = new ArrayList<Card>();

        for (int i = 0; i < 36; i++) {
            Card card = new Card(-1, Suit.Spades);
            switch (i % 4) {
                case 0:
                    card.rank = i / 4;
                    card.suit = Suit.Spades;
                    deck.add(card);
                    break;
                case 1:
                    card.rank = i / 4;
                    card.suit = Suit.Diamonds;
                    deck.add(card);
                    break;
                case 2:
                    card.rank = i / 4;
                    card.suit = Suit.Hearts;
                    deck.add(card);
                    break;
                case 3:
                    card.rank = i / 4;
                    card.suit = Suit.Clubs;
                    deck.add(card);
                    break;
            }
        }
        Collections.shuffle(deck);

        for (Card card : deck) {
            if (deck.get(35).suit == card.suit) {
                card.isTrump = true;
            }
        }
        return deck;
    }

    public Stage move(int playerNumber, int cardNumber) throws Exception {
        Player player;
        switch (playerNumber) {
            case 0:
                player = firstPlayer;
                break;
            case 1:
                player = secondPlayer;
                break;
            default:
                throw new Exception("Someone has tried to feed false data instead of player number");
        }
        if (player.offence) {
            return attack(player, player.playerHand.get(cardNumber));
        } else {
            return defend(player, player.playerHand.get(cardNumber));
        }
    }

    public Stage move(int playerNumber) throws Exception {
        Player player;
        switch (playerNumber) {
            case 0:
                player = firstPlayer;
                break;
            case 1:
                player = secondPlayer;
                break;
            default:
                throw new Exception("Someone has tried to feed false data instead of player number");
        }
        if (player.offence) {
            return retreat(player);
        } else {
            return giveUp(player);
        }
    }

    public Stage attack(Player player, Card card) {
        if (table.size() == 0) {
            card.inPlay = true;
            table.add(card);
            player.playerHand.remove(card);
            switchPlayers();
        } else {
            for (Card tableCard : table) {
                tableCard.inPlay = false;
            }
            for (int i = 0; i < table.size(); i++) {
                if (card.rank == table.get(i).rank) {
                    card.inPlay = true;
                    table.add(card);
                    player.playerHand.remove(card);
                    switchPlayers();
                    break;
                }
            }
        }
        return Stage.Continue;
    }

    public Stage defend(Player player, Card card) {
        Card cardAttacking = new Card();
        for (Card tableCard : table) {
            if (tableCard.inPlay) {
                cardAttacking = tableCard;
            }
        }
        if ((cardAttacking.suit == card.suit && cardAttacking.rank < card.rank) ||
                (cardAttacking.suit != card.suit && card.isTrump)) {
            deactivateCard();
            table.add(card);
            player.playerHand.remove(card);
            switchPlayers();
        }
        return Stage.Continue;
    }

    public Stage retreat(Player player) {
        if (findActiveCard() == false) {
            table.clear();
            switchOffender();
            switchPlayers();
            return replenish();
        }
        return Stage.Continue;
    }

    public Stage giveUp(Player player) {
        if (findActiveCard() == true) {
            player.playerHand.addAll(table);
            table.clear();
            switchPlayers();
            return replenish();
        }
        return Stage.Continue;
    }

    public Stage replenish() {
        if (firstPlayer.playerHand.size() < 6){
            int missingCards = 6 - firstPlayer.playerHand.size();
            for (int i = 0; i < missingCards; i++) {
                if (deckOfCards.size() > 0) {
                    firstPlayer.playerHand.add(deckOfCards.remove(0));
                }
            }
        }

        if (secondPlayer.playerHand.size() < 6){
            int missingCards = 6 - secondPlayer.playerHand.size();
            for (int i = 0; i < missingCards; i++) {
                if (deckOfCards.size() > 0) {
                    secondPlayer.playerHand.add(deckOfCards.remove(0));
                }
            }
        }

        if (deckOfCards.size() == 0) {
            if (firstPlayer.playerHand.size() == 0 && secondPlayer.playerHand.size() == 0) {
                return Stage.Draw;
            }
            if (firstPlayer.playerHand.size() == 0 && secondPlayer.playerHand.size() > 0) {
                return Stage.Victory;
            }

            if (firstPlayer.playerHand.size() > 0 && secondPlayer.playerHand.size() == 0) {
                return Stage.Loss;
            }
        }
        return Stage.Continue;
    }

    public void switchPlayers() {
        firstPlayer.activityStatus = !(firstPlayer.activityStatus);
        secondPlayer.activityStatus = !(secondPlayer.activityStatus);
    }

    public void switchOffender() {
        firstPlayer.offence = !(firstPlayer.offence);
        secondPlayer.offence = !(secondPlayer.offence);

    }

    public void deactivateCard() {
        for (Card tableCard : table) {
            if (tableCard.inPlay) {
                tableCard.inPlay = false;
            }
        }
    }

    public boolean findActiveCard() {
        for (Card tableCard : table) {
            if (tableCard.inPlay) {
                return true;
            }
        }
        return false;
    }

    public void sortHand(Player player) {
        Collections.sort(player.playerHand, new CardComparator());
    }
}