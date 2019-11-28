package fool.gameLogic;

import java.util.ArrayList;

public class Player {
    ArrayList<Card> playerHand;
    boolean activityStatus;
    boolean offence;
/*
    public String playerName;
*/
/*
    public boolean isHuman;
*/

    public Player() {
        this.activityStatus = false;
        this.playerHand = new ArrayList<Card>();
        this.offence = false;
/*
        playerName = "NPC";
*/
/*
        this.isHuman = false;
*/
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public boolean isActivityStatus() {
        return activityStatus;
    }

    public boolean isOffence() {
        return offence;
    }

    public void setActivityStatus(boolean activityStatus) {
        this.activityStatus = activityStatus;
    }

/*    public boolean isHuman(){
        return this.isHuman;
    }
*/

}