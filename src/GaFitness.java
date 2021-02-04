import java.util.List;

import models.Board;
import models.Game;
import models.PlayerType;
import models.Action;
import models.Player;
// import java.lang.*;
// import java.util.Arrays; 
// import java.util.List; 

public class GaFitness {

    private Board start_state = null;
    private Board end_state = null;
    private String player_action = null;
    private List<Integer> vars;

    private WarriorZ whitePlayer;
    private AiDeep blackPlayer;
    private Game game;

    public GaFitness(List<Integer> vars) {
        this.vars = vars;
        set_states();
        this.whitePlayer = new WarriorZ(PlayerType.white, vars);
        this.blackPlayer = new AiDeep(PlayerType.black);
        this.game = new Game(whitePlayer, blackPlayer, start_state);
    }

    private void set_states() {
        // take one random state as start_state and its next state as end_state
    }

    private Board  get_next_state_from_game() {
        // create a game with start_state as it is 
        // and vars as eval variables 
        // with a not important oponnent

        Action action = null;
        boolean attack = false;

        if (player_action == "force") {
            action = whitePlayer.forceAttack(game.copy());
            attack = true;
        } else if (player_action == "second") {
            action = whitePlayer.secondAction(game.copy());
            attack = false;
        }

        // action is applied
        game.applyAction(whitePlayer, action, attack);

        return game.getBoard();
    }

    private int eval_beads(Board current_board, Player player) {
        int A = 0, B = 0, C = 0;
        for (int row_c = 0; row_c < current_board.getRows().length; row_c ++) {
            Board.BoardRow row = current_board.getRows()[row_c];
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

    private int check_board_diff(Board one, Board two) {
        return Math.abs(eval_beads(one, whitePlayer) + eval_beads(one, blackPlayer) - (eval_beads(two, whitePlayer) + eval_beads(two, blackPlayer)));
    }

    public int fitness() {
        // random states has been initialized
        Board next_guess_state = get_next_state_from_game();
        return check_board_diff(next_guess_state, end_state);
    }
}
