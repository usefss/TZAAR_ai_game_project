import models.Game;
import models.Player;
import models.PlayerType;

public class Main {

    public static void main(String[] args) {
        // RandomPlayer whitePlayer = new RandomPlayer(PlayerType.white);
        WarriorZ whitePlayer = new WarriorZ(PlayerType.white);
        // Ai whitePlayer = new Ai(PlayerType.white);
        // OptimizedAI blackPlayer = new OptimizedAI(PlayerType.black);
        AiDeep blackPlayer = new AiDeep(PlayerType.black);
        Game game = new Game(whitePlayer, blackPlayer);
        Player player = game.play();
        System.out.println(player.getType());
    }

}
