const baseURL = "http://localhost:8080/";
/*const baseURL = "https://foolkyivbased.herokuapp.com/";*/
var deckID = 0;
function render(gameState) {
    deckID = gameState.deckID;

    var trumpLayer = $('#trump-layer');
    trumpLayer.empty();
    var trumpDeck = "";
    if(gameState.remainingCards > 0){
        var trump = gameState.trumpCard;
        trumpDeck += `<div id='trump-card' class='trump-card ${trump}'></div>`;
    }
    if (gameState.remainingCards > 1){
        trumpDeck += `<div class='deck-card'></div>`;
    }
    trumpLayer.append(trumpDeck);

    var firstPlayerHand = $("#firstPlayerHand");
    firstPlayerHand.empty();
    var endButton1 = $("#end-button1");
    endButton1.empty();
    if(gameState.firstPlayerActive){
        text = "<div class='button' onclick='makeMove(0, -1)'></div>";
        endButton1.append(text);
    }
    var PlayerOneRow = gameState.firstPlayerCards.length;
    for (var column = 0; column < PlayerOneRow; column++) {
        var cardClass = gameState.firstPlayerCards[column];
        var playerNumber = gameState.firstPlayerNumber;
        var playability = "";
        var clickable = "";
        if (gameState.firstPlayerActive){
            playability += "playable";
            clickable += `onclick='makeMove(${playerNumber}, ${column})'`
        }
        text = `<div class='card visible ${cardClass} ${playability}' ${clickable}></div>`;
        firstPlayerHand.append(text);
    }

    var firstPlayerTable = $('#firstPlayerTable');
    var secondPlayerTable = $('#secondPlayerTable');
    firstPlayerTable.empty();
    secondPlayerTable.empty();
    var currentTable;
    var spareTable;
    if (gameState.firstPlayerOffender){
        currentTable = firstPlayerTable;
        spareTable = secondPlayerTable;
    } else {
        currentTable = secondPlayerTable;
        spareTable = firstPlayerTable;
    }
    var tableLength = gameState.table.length;
    for (var i = 0; i < tableLength; i++){
        var cardClass = gameState.table[i];
        text = `<div class='card visible ${cardClass}'></div>`;
        if (i % 2 === 0){
            currentTable.append(text);
        } else {
            spareTable.append(text);
        }
    }

    var secondPlayerHand = $("#secondPlayerHand");
    secondPlayerHand.empty();
    var endButton2 = $("#end-button2");
    endButton2.empty();
    if(gameState.secondPlayerActive){
        text = "<div class='button' onclick='makeMove(1, -1)'></div>";
        endButton2.append(text);
    }
    var PlayerTwoRow = gameState.secondPlayerCards.length;
    for (var columnTwo = 0; columnTwo < PlayerTwoRow; columnTwo++) {
        var cardClass = gameState.secondPlayerCards[columnTwo];
        var playerTwoNumber = gameState.secondPlayerNumber;
        var playability = "";
        var clickable = "";
        if (gameState.secondPlayerActive){
           playability += "playable";
           clickable += `onclick='makeMove(${playerTwoNumber}, ${columnTwo})'`;
        }
        text = `<div class='card invisible' ${clickable}></div>`;
        secondPlayerHand.append(text);
    }

    if (gameState.stage === "Victory") {
        alert("You won!");

    } else if (gameState.stage === "Loss") {
        alert("Human player lost!");
    } else if (gameState.stage === "Draw"){
        alert("Draw!");
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

function makeMove(playerNumber, cardNumber) {
    var playerNumber = playerNumber;
    var cardNumber = cardNumber;

    $.ajax({
        url: baseURL + "deck",
        crossDomain: true,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({
            deckID: deckID,
            playerNumber: playerNumber,
            cardNumber: cardNumber,
        }),
        type: "POST",
        success: render
    })
}