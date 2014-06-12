/**
 * Grupos
 */
if(game.scoreA && game.scoreB) {
    if(bet.scoreA==game.scoreA && bet.scoreB==game.scoreB) {
        result.winLevel=2;
        result.points = 5;
    } else {
        if(bet.scoreA>bet.scoreB && game.scoreA>game.scoreB) {
            result.winLevel=1;
            result.points = 2;
        } else {
            if(bet.scoreA<bet.scoreB && game.scoreA<game.scoreB) {
                result.winLevel=1;
                result.points = 2;
            } else {
                if(bet.scoreA==bet.scoreB && game.scoreA==game.scoreB) {
                    result.winLevel=1;
                    result.points = 2;
                } else {
                    result.winLevel=0;
                    result.points = 0;    
                }
            }
        }
    }   
}

/**
 * Octavos
 */
var totalPoints=0;
var foundA = false;
var foundB = false;
var existsGamesInRound = false;
for (var i=0;i<allGameSidesInRound.size();i++) {
    var gameSide = allGameSidesInRound.get(i);
    if (gameSide) {
        existsGamesInRound = true;
        if (bet.gameSideA.name == gameSide.name) {
            foundA=true;
            totalPoints += 5;
        }
        if (bet.gameSideB.name == gameSide.name) {
            foundB = true;
            totalPoints += 5;
        }
    }
}
if (existsGamesInRound) {
    result.points = totalPoints;
    if(foundA) {
        result.sideAWinLevel = 2;
    } else {
        result.sideAWinLevel = 0;
    }    
    if(foundB) {
        result.sideBWinLevel = 2;
    } else {
        result.sideBWinLevel = 0;
    }
}

/**
 * Cuartos
 */
var totalPoints=0;
var foundA = false;
var foundB = false;
var existsGamesInRound = false;
for (var i=0;i<allGameSidesInRound.size();i++) {
    var gameSide = allGameSidesInRound.get(i);
    if (gameSide) {
        existsGamesInRound = true;
        if (bet.gameSideA.name == gameSide.name) {
            foundA=true;
            totalPoints += 15;
        }
        if (bet.gameSideB.name == gameSide.name) {
            foundB = true;
            totalPoints += 15;
        }
    }
}
if (existsGamesInRound) {
    result.points = totalPoints;
    if(foundA) {
        result.sideAWinLevel = 2;
    } else {
        result.sideAWinLevel = 0;
    }    
    if(foundB) {
        result.sideBWinLevel = 2;
    } else {
        result.sideBWinLevel = 0;
    }
}

/**
 * Semifinales
 */
var totalPoints=0;
var foundA = false;
var foundB = false;
var existsGamesInRound = false;
for (var i=0;i<allGameSidesInRound.size();i++) {
    var gameSide = allGameSidesInRound.get(i);
    if (gameSide) {
        existsGamesInRound = true;
        if (bet.gameSideA.name == gameSide.name) {
            foundA=true;
            totalPoints += 25;
        }
        if (bet.gameSideB.name == gameSide.name) {
            foundB = true;
            totalPoints += 25;
        }
    }
}
if (existsGamesInRound) {
    result.points = totalPoints;
    if(foundA) {
        result.sideAWinLevel = 2;
    } else {
        result.sideAWinLevel = 0;
    }    
    if(foundB) {
        result.sideBWinLevel = 2;
    } else {
        result.sideBWinLevel = 0;
    }
}



/**
 * Final
 */
var totalPoints=0;
var foundA = false;
var foundB = false;
var existsGamesInRound = false;
for (var i=0;i<allGameSidesInRound.size();i++) {
    var gameSide = allGameSidesInRound.get(i);
    if (gameSide) {
        existsGamesInRound = true;
        if (bet.gameSideA.name == gameSide.name) {
            foundA=true;
            totalPoints += 35;
        }
        if (bet.gameSideB.name == gameSide.name) {
            foundB = true;
            totalPoints += 35;
        }
    }
}
if ((game.scoreA > game.scoreB) && (game.gameSideA.name == result.betWinner.name)) {
    totalPoints += 50;
} 
if ((game.scoreB > game.scoreA) && (game.gameSideB.name == result.betWinner.name)) {
    totalPoints += 50;
} 
if (existsGamesInRound) {
    result.points = totalPoints;
    if(foundA) {
        result.sideAWinLevel = 2;
    } else {
        result.sideAWinLevel = 0;
    }    
    if(foundB) {
        result.sideBWinLevel = 2;
    } else {
        result.sideBWinLevel = 0;
    }
}