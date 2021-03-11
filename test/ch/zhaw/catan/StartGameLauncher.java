package ch.zhaw.catan;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.awt.Point;


/**
 * This class is used for test purpose only. A Dummy game is being inizialised with 2 players
 * and some valid coordinates.
 *
 * @author Kunsang Kündetsang
 * @version 12-06-2019
 */
public class StartGameLauncher {
    private final static String SEP = System.lineSeparator();

    private void run() {
        GameLauncher launcher = new GameLauncher();
        TextIO textIO = TextIoFactory.getTextIO();
        TextTerminal<?> terminal = textIO.getTextTerminal();

        terminal.println("Welcome to the SiedlerGame." + SEP + SEP);

        // phase 1 - create default game layout
        int playerCount = 2;
        SiedlerGame game = new SiedlerGame(Config.WIN_POINTS, playerCount);
        SiedlerBoardTextView view = game.getView();
        terminal.println(view.toStringWithCoordinates());


        // phase 2 - let players place their initial settlements and roads
        createInitialPlacements(playerCount, textIO, game);

        // phase 3 - regular gameplay until the end
        launcher.runGame(textIO, terminal, game);
    }

    private void createInitialPlacements(int playerCount, TextIO textIO, SiedlerGame game) {
        game.placeInitialSettlement(new Point(6,4),false);
        game.placeInitialRoad(new Point(6,4),new Point (7,3));
        game.placeInitialSettlement(new Point(10,4),true);
        game.placeInitialRoad(new Point(10,4),new Point (11,3));

        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 10);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.STONE, 10);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 10);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 10);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 10);

        game.switchToNextPlayer();



        game.placeInitialSettlement(new Point(3,9),false);
        game.placeInitialRoad(new Point(3,9),new Point (2,10));
        game.placeInitialSettlement(new Point(9,9),true);
        game.placeInitialRoad(new Point(9,9),new Point (10,10));
        textIO.getTextTerminal().println(game.getView().toStringWithCoordinates());
        game.switchToNextPlayer();
    }

    public static void main(String[] args) {
        StartGameLauncher launcher = new StartGameLauncher();
        launcher.run();
    }
}
