import models.Board;
import models.Action;
import models.Game;
import models.Player;
import models.PlayerType;

import java.util.ArrayList;
// import java.util.Arrays; 
import java.util.List; 

public class WarriorZ extends Player {

    private int doneActions = 0;
    private final int maxDepth = 3;
    private List<Integer> vars;

    public WarriorZ(PlayerType type) {
        super(type);
    }
    public WarriorZ(PlayerType type, List<Integer> vars) {
        super(type);
        this.vars = vars;
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

    private int eval_beads(Game game, Player player, String bead_type) {
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
        if (bead_type == "A")
            return A;
        else if (bead_type == "B")
            return B;
        else if (bead_type == "C") 
            return C;
        else
            return 0;
    }

    private int get_action_count(Game game, String turn) {
        int min = 0;
        int max = 0;
        ArrayList<Action> actions = getAllActions(game.getBoard());

        for (Action action : actions) {
            if (action.getStart().bead.getPlayer().getType() != getType()) 
                max = max + 1;
            else
                min = min + 1;
        }

        if (turn == "min")
            return min;
        else
            return max;
    }


    private int eval(Game game, int depth) {
        int E_val = 0;
        E_val = E_val + vars.get(0) * eval_beads(game, this, "A");
        E_val = E_val + vars.get(1) * eval_beads(game, this, "B");
        E_val = E_val + vars.get(2) * eval_beads(game, this, "C");
        E_val = E_val + vars.get(3) * eval_beads(game, getOpp(game), "A");
        E_val = E_val + vars.get(4) * eval_beads(game, getOpp(game), "B");
        E_val = E_val + vars.get(5) * eval_beads(game, getOpp(game), "C");

        E_val = E_val + vars.get(6) * depth;
        E_val = E_val + vars.get(7) * get_action_count(game, "max");
        E_val = E_val + vars.get(8) * get_action_count(game, "min");

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
            return eval(game, depth);
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
            return eval(game, depth);
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
            return eval(game, depth);
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
            return eval(game, depth);
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
