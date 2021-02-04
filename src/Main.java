import models.Game;
import models.Player;
import models.PlayerType;
import models.Board;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Main {

    public static void test_file() {
        WarriorZ whitePlayer = new WarriorZ(PlayerType.white);
        AiDeep blackPlayer = new AiDeep(PlayerType.black);
        Game game = new Game(whitePlayer, blackPlayer);

        Board board = game.getBoard();
        board.printComplete();
        
        try {
            // write object to file
            FileOutputStream fos = new FileOutputStream("start.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(board);
            oos.close();
    
            // read object from file
            FileInputStream fis = new FileInputStream("start.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            // MyBean result = (MyBean) ois.readObject();
            Board result = (Board) ois.readObject();
            ois.close();

            result.printComplete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }

    public static void start_game() {

        WarriorZ whitePlayer = new WarriorZ(PlayerType.white);
        AiDeep blackPlayer = new AiDeep(PlayerType.black);
        Game game = new Game(whitePlayer, blackPlayer);

        Player player = game.play();

        System.out.println(player.getType());

    }
    public static void main(String[] args) {
        // start_game();
        test_file();
    }

}
