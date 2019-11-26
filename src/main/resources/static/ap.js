const baseURL = "http://localhost:8080/";
/*const baseURL = "https://minesweeperkyivbased.herokuapp.com/";*/
var deckID = 0;
function render(gameState) {
    deckID = gameState.deckID;

    var trump-layer = $("#trump-layer");
    trump-layer.empty();
    var trump-deck = "";
    if(gameState.remainingCards >= 1){
        var trump = convertCard(gameState.trumpCard);
        trump-deck += `<div id='trump-layer' class='trump-layer ${trump}'></div>";`
    }
    if (gameState.remainingCards > 1){
        trump-deck += `<div class='deck-card'></div>;`
    }
    trump-layer.append(trump-deck);

    var firstPlayerHand = $("#firstPlayerHand");
    firstPlayerHand.empty();
    var PlayerOneRow = gameState.player1.playerHand.length;
    for (var column = 0; column < PlayerOneRow; column++) {
        var card = gameState.player1.playerHand.get(column);
        var cardClass = convertCard(card);
        text = `<div class='card visible playable ${cardClass}' onclick='makeMove(this, player)'></div>`;
        firstPlayerHand.append(text);
    }

    var firstPlayerTable = $('#firstPlayerTable');
    var firstTableLength = gameState.table.length / 2;
    if (gameState.player1.isActive){
        column = 0;
            if (gameState.table.length % 2 = 1){
                firstTableLength = (gameState.table.length / 2) + 1;
            }
    } else {
        column = 1;
    }
    for (column; column < firstTableLength; column += 2) {
        var card = gameState.table.get(column);
        var cardClass = convertCard(card);
        text = `<div class='card visible ${cardClass}'></div>;`
        firstPlayerHand.append(text);
    }

    var secondPlayerTable = $('#secondPlayerTable');
    var secondTableLength = gameState.table.length / 2;
    if (gameState.player2.isActive){
        column = 0;
            if (gameState.table.length % 2 = 1){
                secondTableLength = (gameState.table.length / 2) + 1;
            }
    } else {
        column = 1;
    }
    for (column; column < secondTableLength; column += 2) {
        var card = gameState.table.get(column);
        var cardClass = convertCard(card);
        text = `<div class='card visible ${cardClass} onclick='makeMove(this, player)'></div>`;
        secondPlayerHand.append(text);
    }

    var secondPlayerHand = $("#secondPlayerHand");
    secondPlayerHand.empty();
    var PlayerTwoRow = gameState.player2.playerHand.length;
    for (var column = 0; column < PlayerTwoRow; column++) {
        var card = gameState.player2.playerHand.get(column);
        var cardClass = convertCard(card);
        text = `<div class='card visible ${cardClass}'></div>`;
        secondPlayerHand.append(text);
        }

    if (gameState.stage === "Victory") {
        alert("You won!");

    } else if (gameState.stage === "Loss") {
        alert("You lost!");
    }

    return false;
}

function newGame(){
    $.ajax({
        url: baseURL + "deck",
        crossDomain: true,
        type: "GET",
        success: render
    })
}

function convertCard(card){
    var cardClass = "";
    switch (card.suit) {
    case "Spades":
        cardClass = "S";
        break;
    case "Diamonds":
        cardClass = "D";
        break;
    case "Hearts":
        cardClass = "H";
        break;
    case "Clubs":
        cardClass = "C";
        break;
    }
    var cardRank = "";
    if (card.rank < 5){
    cardRank += (card.rank + 6);
    } else {
        switch (card.rank){
        case 5:
            cardRank = "J";
            break;
        case 6:
            cardRank = "Q";
            break;
        case 7:
            cardRank = "K";
            break;
        case 8:
            cardRank = "A";
            break;
        }
    }
    cardClass.append(cardRank);
    return cardClass;
}

function makeMove(element, player) {
    var card = $(element).attr("card");

    $.ajax({
        url: baseURL + "deck",
        crossDomain: true,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({
            deckID: deckID,
            player: player,
            card: card;
        }),
        type: "POST",
        success: render
    })
}