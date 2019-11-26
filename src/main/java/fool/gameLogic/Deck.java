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
        /*        secondPlayer.playerName = nameSecondPlayer;*/
        /*        secondPlayer.isHuman = true;*/

        int lowestTrump1 = 11;
        int highestRank1 = 0;
        for (int i = 0; i < 6; i++) {
            if (deckOfCards.get(0).isTrump && deckOfCards.get(0).rank < lowestTrump1) {
                lowestTrump1 = deckOfCards.get(0).rank;
            }
            if (deckOfCards.get(0).rank > highestRank1) {
                highestRank1 = deckOfCards.get(0).rank;
            }
            deckOfCards.get(0).isVisible = true;
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
            deckOfCards.get(0).isVisible = true;
            secondPlayer.playerHand.add(deckOfCards.remove(0));
        }

        if (lowestTrump1 < lowestTrump2) {
            firstPlayer.activityStatus = true;
        } else if (highestRank1 > highestRank2) {
            firstPlayer.activityStatus = true;
        } else {
            secondPlayer.activityStatus = true;
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
        deck.get(35).isVisible = true;

        for (Card card : deck) {
            if (deck.get(35).suit == card.suit) {
                card.isTrump = true;
            }
        }
        return deck;
    }

    public Stage makeMove(Player player, Card card) {
        if (player.activityStatus && card != null) {
            return attack(player, card);
        } else if (player.activityStatus && card == null) {
            return retreat(player, null);
        } else if (card != null) {
            return defend(player, card);
        } else if (card == null){
            return giveUp(player, null);
        } else return Stage.Continue;
    }

    public Stage attack(Player player, Card card) {
        if (player.activityStatus == true) {
            if (table.size() == 0) {
                card.inPlay = true;
                table.add(card);
                player.playerHand.remove(card);
                switchPlayers();
            } else {
                for (Card tableCard : table) {
                    if (tableCard.inPlay) {
                        return Stage.Continue;
                    }
                }
                for (int i = 0; i < table.size(); i++) {
                    if (card.rank == table.get(i).rank) {
                        card.inPlay = true;
                        table.add(card);
                        switchPlayers();
                    }
                }
            }
        }
        return Stage.Continue;
    }

    public Stage defend(Player player, Card card) {
        if (player.activityStatus == true && table.size() > 0) {
            Card cardAttacking = new Card(-1, Suit.Spades);
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
        }
        return Stage.Continue;
    }

    public Stage retreat(Player player, Card card) {
        if (player.activityStatus == true &&
                findActiveCard() == false &&
                card == null) {
            table.clear();
            switchPlayers();
            return replenish();
        }
        return Stage.Continue;
    }

    public Stage giveUp(Player player, Card card) {
        if (player.activityStatus == true &&
                findActiveCard() == true &&
                card == null) {
            player.playerHand.addAll(table);
            table.clear();
            return replenish();
        }
        return Stage.Continue;
    }

    public Stage replenish() {
        for (int i = 0; i < (6 - firstPlayer.playerHand.size()); i++) {
            if (deckOfCards.size() > 0) {
                firstPlayer.playerHand.add(deckOfCards.remove(0));
            }
        }
        for (int i = 0; i < (6 - secondPlayer.playerHand.size()); i++) {
            if (deckOfCards.size() > 0) {
                secondPlayer.playerHand.add(deckOfCards.remove(0));
            }
        }
        if (deckOfCards.size() == 0) {
            if (firstPlayer.playerHand.size() == 0 && secondPlayer.playerHand.size() == 0) {
                return Stage.Draw;
            }
            if (firstPlayer.playerHand.size() != 0 && secondPlayer.playerHand.size() == 0) {
                return Stage.Victory;
            } else {
                return Stage.Loss;
            }
        }
        return Stage.Continue;
    }

    public void switchPlayers() {
        firstPlayer.activityStatus = !(firstPlayer.activityStatus);
        secondPlayer.activityStatus = !(secondPlayer.activityStatus);
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