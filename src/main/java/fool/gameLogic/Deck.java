package fool.gameLogic;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public ArrayList<Card> deckOfCards;
    public ArrayList<Card> table = new ArrayList<Card>();
    public Player firstPlayer = new Player();
    public Player secondPlayer = new Player();
    public String trump;

    public Deck() {
        deckOfCards = createDeck();
        switch (deckOfCards.get(35).suit) {
            case Spades:
                trump = "Spades";
                break;
            case Diamonds:
                trump = "Diamonds";
                break;
            case Hearts:
                trump = "Hearts";
                break;
            default:
                trump = "Clubs";
                break;
        }

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
        if (secondPlayer.isActivityStatus()) {
            int iniCardIndex = pickAttackCard(secondPlayer.playerHand);
            attack(secondPlayer, secondPlayer.playerHand.get(iniCardIndex));
        }
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
                throw new Exception("Someone has tried to feed false data instead of a player number");
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
                throw new Exception("Someone has tried to feed false data instead of a player number");
        }
        if (player.offence) {
            return retreat(player);
        } else {
            return giveUp(player);
        }
    }

    public Stage attack(Player player, Card card) {
        boolean aiResponse = false;
        boolean attackSuccess = false;
        if (player == firstPlayer) {
            aiResponse = true;
        }

        ArrayList<Card> hand1 = firstPlayer.playerHand;
        ArrayList<Card> hand2 = secondPlayer.playerHand;

        if (table.size() == 0) {
            table.add(card);
            player.playerHand.remove(card);
            switchPlayers();
            attackSuccess = true;
            if (endGame() != Stage.Continue) {
                return endGame();
            }
        } else {
            if ((table.size() % 2 == 0)) {
                for (int i = 0; i < table.size(); i++) {
                    if (card.rank == table.get(i).rank) {
                        table.add(card);
                        player.playerHand.remove(card);
                        switchPlayers();
                        attackSuccess = true;
                        if (endGame() != Stage.Continue) {
                            return endGame();
                        }
                        break;
                    }
                }
            } else {
                return Stage.Continue;
            }
        }

        if (aiResponse && attackSuccess) {
            boolean effectiveDefence = false;
            if (secondPlayer.playerHand.size() > 0) {
                if (pickDefenceCard(secondPlayer.playerHand, card) != -1) {
                    effectiveDefence = true;
                    int defCardIndex = pickDefenceCard(secondPlayer.playerHand, card);
                    defend(secondPlayer, secondPlayer.playerHand.get(defCardIndex));
                }
            }

            if (!effectiveDefence) {
                giveUp(secondPlayer);
            }
        }

        return Stage.Continue;
    }

    public Stage defend(Player player, Card card) {
        boolean aiResponse = false;
        if (player == firstPlayer) {
            aiResponse = true;
        }

        Card cardAttacking = new Card();
        if (table.size() % 2 == 1) {
            cardAttacking = table.get(table.size() - 1);
        }

        if ((cardAttacking.suit == card.suit && cardAttacking.rank < card.rank) ||
                (cardAttacking.suit != card.suit && card.isTrump)) {
            table.add(card);
            player.playerHand.remove(card);
            boolean emptyHand = (player.playerHand.size() == 0);
            switchPlayers();
            if (table.size() == 12 || emptyHand){
                retreat(player);
            }
            if (endGame() != Stage.Continue) {
                return endGame();
            }
        }

        if (aiResponse) {
            boolean effectiveAttack = false;
            if (secondPlayer.playerHand.size() > 0 && table.size() < 11) {
                if (pickAttackCard(secondPlayer.playerHand) != -1) {
                    effectiveAttack = true;
                    int attCardIndex = pickAttackCard(secondPlayer.playerHand);
                    attack(secondPlayer, secondPlayer.playerHand.get(attCardIndex));
                }
            }

            if (!effectiveAttack) {
                retreat(secondPlayer);
            }
        }

        return Stage.Continue;
    }


    public Stage retreat(Player player) {
        boolean aiResponse = false;
        if (player == firstPlayer) {
            aiResponse = true;
        }

        if (endGame() != Stage.Continue) {
            return endGame();
        }

        if (table.size() % 2 == 0) {
            table.clear();
            switchOffender();
            switchPlayers();
            replenish();
        }

        if (aiResponse) {
            if (secondPlayer.playerHand.size() > 0) {
                int attCardIndex = pickAttackCard(secondPlayer.playerHand);
                attack(secondPlayer, secondPlayer.playerHand.get(attCardIndex));
            }
        }

        return endGame();
    }

    public Stage giveUp(Player player) {
        boolean aiResponse = false;
        if (player == firstPlayer) {
            aiResponse = true;
        }

        if (endGame() != Stage.Continue) {
            return endGame();
        }

        if (table.size() % 2 == 1) {
            player.playerHand.addAll(table);
            table.clear();
            switchPlayers();
            replenish();
        }

        if (aiResponse) {
            if (secondPlayer.playerHand.size() > 0) {
                int attCardIndex = pickAttackCard(secondPlayer.playerHand);
                attack(secondPlayer, secondPlayer.playerHand.get(attCardIndex));
            }
        }

        return endGame();
    }

    public Stage replenish() {
        Player offender = firstPlayer;
        Player defender = secondPlayer;
        if (firstPlayer.isOffence()) {
            offender = firstPlayer;
            defender = secondPlayer;
        } else if (secondPlayer.isOffence()) {
            offender = secondPlayer;
            defender = firstPlayer;
        }

        int missingCards = 0;
        if (6 - firstPlayer.playerHand.size() > 0) {
            missingCards += 6 - firstPlayer.playerHand.size();
        }
        if (6 - secondPlayer.playerHand.size() > 0) {
            missingCards += 6 - secondPlayer.playerHand.size();
        }

        for (int i = 0; i < missingCards; i++) {
            if (deckOfCards.size() > 0) {
                if (offender.playerHand.size() < 6) {
                    offender.playerHand.add(deckOfCards.remove(0));
                } else if (defender.playerHand.size() < 6) {
                    defender.playerHand.add(deckOfCards.remove(0));
                }
                Player interLude;
                interLude = defender;
                defender = offender;
                offender = interLude;
            }
        }

        return Stage.Continue;
    }

    public void switchPlayers() {
        firstPlayer.activityStatus = !(firstPlayer.activityStatus);
        secondPlayer.activityStatus = !(secondPlayer.activityStatus);
    }

    public Stage endGame() {
        if (deckOfCards.size() == 0) {
            if (firstPlayer.playerHand.size() == 0 && secondPlayer.playerHand.size() == 0) {
                return Stage.Draw;
            }

            if (firstPlayer.playerHand.size() == 0 && secondPlayer.playerHand.size() > 1) {
                return Stage.Victory;
            }

            if (firstPlayer.playerHand.size() > 1 && secondPlayer.playerHand.size() == 0) {
                return Stage.Loss;
            }
        }
        return Stage.Continue;
    }


    public void switchOffender() {
        firstPlayer.offence = !(firstPlayer.offence);
        secondPlayer.offence = !(secondPlayer.offence);

    }

    public void sortHand(Player player) {
        Collections.sort(player.playerHand, new CardComparator());
    }

    public int pickDefenceCard(ArrayList<Card> hand, Card actionCard) {
        int lowestDefenceCard = -1;
        CardComparator cardComparator = new CardComparator();
        for (int i = 0; i < hand.size(); i++) {
            if ((cardComparator.compare(hand.get(i), actionCard) == 1)) {
                lowestDefenceCard = i;
                break;
            }
        }

        if (lowestDefenceCard != -1) {
            for (int i = 0; i < hand.size(); i++) {
                if ((cardComparator.compare(hand.get(i), actionCard) == 1)) {
                    if (cardComparator.compare(hand.get(i), hand.get(lowestDefenceCard)) == -1) {
                        lowestDefenceCard = i;
                    }
                }
            }
        }

        return lowestDefenceCard;
    }

    public int pickAttackCard(ArrayList<Card> hand) {
        int lowestAttackCard = -1;
        CardComparator cardComparator = new CardComparator();

        if (table.size() == 0) {
            lowestAttackCard = 0;
            for (int i = 0; i < hand.size(); i++) {
                if (cardComparator.compare(hand.get(i), hand.get(lowestAttackCard)) == -1) {
                    lowestAttackCard = i;
                }
            }
        } else {
            for (int i = 0; i < hand.size(); i++) {
                for (int j = 0; j < table.size(); j++) {
                    if (hand.get(i).rank == table.get(j).rank) {
                        lowestAttackCard = i;
                        break;
                    }
                }
            }

            if (lowestAttackCard != -1) {
                for (int i = 0; i < hand.size(); i++) {
                    for (int j = 0; j < table.size(); j++) {
                        if (hand.get(i).rank == table.get(j).rank) {
                            if (cardComparator.compare(hand.get(i), hand.get(lowestAttackCard)) == -1) {
                                lowestAttackCard = i;
                            }
                        }
                    }
                }
            }
        }

        return lowestAttackCard;
    }

}