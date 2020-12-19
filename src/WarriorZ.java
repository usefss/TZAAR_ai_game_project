import models.Board;
import models.Action;
import models.Game;
import models.Player;
import models.PlayerType;

import java.util.ArrayList;

public class WarriorZ extends Player {

    private int doneActions = 0;
    private final int maxDepth = 3;

    public WarriorZ(PlayerType type) {
        super(type);
    }

    @Override
    public Action forceAttack(Game game) {
        int maxValue = Integer.MIN_VALUE;
        Action bestAction = null;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        if (doneActions == 0 && getType() == PlayerType.white) {
            for (Action action : actions) {
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, true)) {
                        continue;
                    }
                    Player winner = copyGame.getWinner();
                    if (winner != null) {
                        if (winner.getType() == getType()) {
                            return action;
                        }
                    } else {
                        int temp = Math.max(maxValue, minForceAttack(copyGame, 0, Integer.MIN_VALUE, Integer.MAX_VALUE));
                        if (temp > maxValue) {
                            maxValue = temp;
                            bestAction = action;
                        }
                    }
                }
            }
        } else {
            for (Action action : actions) {
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, true)) {
                        continue;
                    }
                    Player winner = copyGame.getWinner();
                    if (winner != null) {
                        if (winner.getType() == getType()) {
                            return action;
                        }
                    } else {
                        int temp = Math.max(maxValue, maxSecondMove(copyGame, 0, Integer.MIN_VALUE, Integer.MAX_VALUE));
                        if (temp > maxValue) {
                            maxValue = temp;
                            bestAction = action;
                        }
                    }
                }
            }
        }
        doneActions++;
        return bestAction;
    }

    private int eval_beads(Game game, Player player) {
        int A = 0, B = 0, C = 0;
        for (int row_c = 0; row_c < game.getBoard().getRows().length; row_c ++) {
            Board.BoardRow row = game.getBoard().getRows()[row_c];
            for (int cell_c = 0; cell_c < row.boardCells.length; cell_c ++) {
                Board.BoardCell cell = row.boardCells[cell_c];
                if (cell.bead != null) {
                    if (cell.bead.getPlayer().getType() == player.getType()) {
                        switch (cell.bead.getType()) {
                            case Tzaars -> A ++;
                            case Tzarras -> B ++;
                            case Totts -> C ++;
                        }
                    }
                }
            }
        }
        return A * B * C;
    }

    private int eval(Game game) {
        int E_val = 0;
        E_val = E_val + eval_beads(game, this);
        E_val = E_val - eval_beads(game, getOpp(game));
        return E_val;
    }

    @Override
    public Action secondAction(Game game) {
        int maxValue = Integer.MIN_VALUE;
        Action bestAction = null;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType()) {
                    return action;
                }
            } else {
                int temp = Math.max(maxValue, minForceAttack(copyGame, 0, Integer.MIN_VALUE, Integer.MAX_VALUE));
                if (temp > maxValue) {
                    maxValue = temp;
                    bestAction = action;
                }
            }
        }
        doneActions++;
        return bestAction;
    }

    private int maxForceAttack(Game game, int depth, int alpha, int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }
        int maxValue = Integer.MIN_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            if (action.getType() == Action.ActionType.attack) {
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(this, action, true)) {
                    continue;
                }
                Player winner = copyGame.getWinner();
                if (winner != null) {
                    if (winner.getType() == getType()) {
                        return Integer.MAX_VALUE;
                    }
                } else {
                    maxValue = Math.max(maxValue, maxSecondMove(copyGame, depth + 1, alpha, betha));
                    alpha = Math.max(maxValue, alpha);
                    if (betha <= alpha) {
                        // System.out.println("pruned");
                        break;
                    }
                }
            }
        }
        return maxValue;
    }

    private int maxSecondMove(Game game, int depth, int alpha, int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }
        int maxValue = Integer.MIN_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType()) {
                    return Integer.MAX_VALUE;
                } else {
                    return Integer.MIN_VALUE;
                }
            } else {
                maxValue = Math.max(maxValue, minForceAttack(copyGame, depth + 1, alpha, betha));
                alpha = Math.max(maxValue, alpha);
                if (betha <= alpha) {
                    // System.out.println("pruned");
                    break;
                }
            }
        }
        return maxValue;
    }

    private int minForceAttack(Game game, int depth, int alpha, int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }
        Player opp = getOpp(game);
        int minValue = Integer.MAX_VALUE;
        ArrayList<Action> actions = opp.getAllActions(game.getBoard());
        for (Action action : actions) {
            if (action.getType() == Action.ActionType.attack) {
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(opp, action, true)) {
                    continue;
                }
                Player winner = copyGame.getWinner();
                if (winner != null) {
                    if (winner.getType() == getType().reverse()) {
                        return Integer.MIN_VALUE;
                    }
                } else {
                    minValue = Math.min(minValue, minSecondMove(copyGame, depth + 1, alpha, betha));
                    betha = Math.min(minValue, betha);
                    if (betha <= alpha) {
                        // System.out.println("pruned");
                        break;
                    }
                }
            }
        }
        return minValue;

    }

    private int minSecondMove(Game game, int depth, int alpha, int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }

        Player opp = getOpp(game);
        int minValue = Integer.MAX_VALUE;
        ArrayList<Action> actions = opp.getAllActions(game.getBoard());
        for (Action action : actions) {
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(opp, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType().reverse()) {
                    return Integer.MIN_VALUE;
                } else {
                    return Integer.MAX_VALUE;
                }
            } else {
                minValue = Math.min(minValue, maxForceAttack(copyGame, depth + 1, alpha, betha));
                betha = Math.min(minValue, betha);
                if (betha <= alpha) {
                    // System.out.println("pruned");
                    break;
                }
            }
        }

        return minValue;
    }

    public Player getOpp(Game game) {
        if (getType() == PlayerType.white) {
            return game.getBlack();
        } else {
            return game.getWhite();
        }
    }

}
