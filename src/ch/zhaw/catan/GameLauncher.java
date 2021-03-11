package ch.zhaw.catan;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import static ch.zhaw.catan.Config.Faction;
import static ch.zhaw.catan.Config.X_MAX;
import static ch.zhaw.catan.Config.X_MIN;
import static ch.zhaw.catan.Config.Y_MAX;
import static ch.zhaw.catan.Config.Y_MIN;

/**
 * This class is responsible for launching the Siedler Game and managing the correct sequence.
 *
 * @author Ala Hadi und Silvan Lüthy
 * @version 2019-11-28
 */
public class GameLauncher {

    /**
     * The enum class Action holds all the commands to play the game.
     */
    public enum Action {
        HELP, SHOW, TRADE, ROAD, SETTLEMENT, CITY, DONE, QUIT
    }

    private final static String SEP = System.lineSeparator();

    /**
     * This is the games main application loop. It retrieves the number of players from the user and starts a new
     * Siedler game. The user is presented with a UI and can choose from multiple commands to interact with the game.
     * The game will run until a player has won or the 'QUIT' command has been chosen.
     */
    private void run() {
        TextIO textIO = TextIoFactory.getTextIO();
        TextTerminal<?> terminal = textIO.getTextTerminal();

        terminal.println("Welcome to the SiedlerGame." + SEP + SEP);

        // phase 1 - create default game layout
        int playerCount = getNumberOfPlayers(textIO);
        SiedlerGame game = new SiedlerGame(Config.WIN_POINTS, playerCount);
        SiedlerBoardTextView view = game.getView();
        terminal.println(view.toStringWithCoordinates());


        // phase 2 - let players place their initial settlements and roads
        createInitialPlacements(playerCount, textIO, game);

        // phase 3 - regular gameplay until the end
        nextTurn(terminal, game);
        runGame(textIO, terminal, game);
    }

    /**
     * Each case of the enum class Action is a separate case and responsible for executing the methods from the
     * SiedlerGame class.
     *
     * @param textIO   An Object of the TextIO class which enables a more beautiful UI
     * @param terminal An object of the TextTerminal class to hold user inputs
     * @param game     An object of the SiedlerGame class to execute all methods
     */
    void runGame(TextIO textIO, TextTerminal<?> terminal, SiedlerGame game) {
        boolean running = true;
        while (game.getWinner() == null && running) {
            terminal.println("-------------------------------------------------------------");
            terminal.println("Current player " + game.getCurrentPlayer());
            if(game.getPlayerWithlongestRoad()==null){
                terminal.println();
            }else if (game.getPlayerWithlongestRoad().equals(game.getCurrentPlayer())) {
                terminal.println();
                terminal.println("You own the longest road currently.");
                terminal.println();
            }
            terminal.println(getResourceStock(game));
            switch (getEnumValue(textIO, Action.class)) {
                case HELP:
                    terminal.println(getHelperMessage());
                    break;
                case SHOW:
                    terminal.println(game.getView().toStringWithCoordinates());
                    break;
                case TRADE:
                    String resourceOffer = textIO.newStringInputReader()
                            .read("Which of your resources you want wo use for the trade? Choose between" +
                                    SEP + "GRAIN: GR, WOOL: WL, WOOD: WD, STONE: ST, CLAY: CL");
                    String resourceWant = textIO.newStringInputReader()
                            .read("Which resource you want to get in exchange 4:1? Choose between" +
                                    SEP + "GRAIN: GR, WOOL: WL, WOOD: WD, STONE: ST, CLAY: CL");
                    Config.Resource tradeOffer = Config.Resource.findByName(resourceOffer.toUpperCase());
                    Config.Resource tradeWant = Config.Resource.findByName(resourceWant.toUpperCase());
                    if (!game.checkTradeWithBankFourToOne(tradeOffer, tradeWant)) {
                        terminal.println("Trade is not possible, inconsistent value.");
                    } else {
                        game.tradeWithBankFourToOne(tradeOffer, tradeWant);
                    }
                    break;
                case ROAD:
                    int xRoadStartCoordinate = textIO.newIntInputReader()
                            .withMinVal(X_MIN)
                            .withMaxVal(X_MAX)
                            .read("Please input x start coordinate of road: ");
                    int yRoadStartCoordinate = textIO.newIntInputReader()
                            .withMinVal(Y_MIN)
                            .withMaxVal(Y_MAX)
                            .read("Please input y start coordinate of road: ");
                    int xRoadEndCoordinate = textIO.newIntInputReader()
                            .withMinVal(X_MIN)
                            .withMaxVal(X_MAX)
                            .read("Please input x end coordinate of road: ");
                    int yRoadEndCoordinate = textIO.newIntInputReader()
                            .withMinVal(Y_MIN)
                            .withMaxVal(Y_MAX)
                            .read("Please input y end coordinate of road: ");
                    Point startRoad = new Point(xRoadStartCoordinate, yRoadStartCoordinate);
                    Point endRoad = new Point(xRoadEndCoordinate, yRoadEndCoordinate);
                    if (!game.buildRoad(startRoad, endRoad)) {
                        terminal.println("Road can not be built.");
                    }else{
                        terminal.println("Road has been built.");
                    }
                    game.updateLongestRoad();
                    if(game.getPlayerWithlongestRoad()==null){
                        terminal.println("");
                    }else if (game.getPlayerWithlongestRoad().equals(game.getCurrentPlayer())) {
                        terminal.println("You own the longest road now!");
                    }
                    break;
                case SETTLEMENT:
                    int xSettlementCoordinate = textIO.newIntInputReader()
                            .withMinVal(X_MIN)
                            .withMaxVal(X_MAX)
                            .read("Please input x coordinate of settlement: ");
                    int ySettlementCoordinate = textIO.newIntInputReader()
                            .withMinVal(Y_MIN)
                            .withMaxVal(Y_MAX)
                            .read("Please input y coordinate of settlement: ");
                    Point setPointSettlement = new Point(xSettlementCoordinate, ySettlementCoordinate);
                    if (!game.buildSettlement(setPointSettlement)) {
                        terminal.println("Settlement can not be built at this position.");
                    } else {
                        terminal.println("Settlement has been built.");
                        game.buildSettlement(setPointSettlement);}
                    break;
                case CITY:
                    int xCityCoordinate = textIO.newIntInputReader()
                            .withMinVal(X_MIN)
                            .withMaxVal(X_MAX)
                            .read("Please input x coordinate of city: ");
                    int yCityCoordinate = textIO.newIntInputReader()
                            .withMinVal(Y_MIN)
                            .withMaxVal(Y_MAX)
                            .read("Please input y coordinate of city: ");
                    Point setPointCity = new Point(xCityCoordinate, yCityCoordinate);
                    if (!game.buildCity(setPointCity)) {
                        terminal.println("City can not be built at that position.");
                    }else{
                        terminal.println("City has been built.");
                    }
                    break;
                case DONE:
                    game.switchToNextPlayer();
                    nextTurn(terminal, game);
                    break;
                case QUIT:
                    running = false;
                    break;
                default:
                    throw new IllegalStateException("Internal error found - Command not implemented.");
            }
        }
        terminal.println("Congrats, player " + game.getCurrentPlayer().toString() + " - you won \\o/");
    }

    private void nextTurn(TextTerminal<?> terminal, SiedlerGame game) {
        Map<Faction, List<Config.Resource>> resources = game.throwDice(2);
        game.distributeResources(resources);
        terminal.println("-------------------------------------------------------------");
        terminal.println("\nTurn: " + game.getCurrentPlayer());
        terminal.println("2 Dices were rolled and you have thrown " + game.getLastDiceSum());
        terminal.println(checkHasLongestRoad(game));
    }

    //TODO Requirements
    private String getHelperMessage() {
        String helperMessage =
                "\nTRADE      -- Trade 4 random resources to 1 other with the bank\n" +
                        "ROAD       -- Build a road\n" +
                        "              You need at least 1 CLAY and 1 WOOD \n" +
                        "SETTLEMENT -- Build a settlement\n" +
                        "              You need at least 1 CLAY, 1 GRAIN, 1 WOOL, 1 WOOD \n" +
                        "CITY       -- Build a city\n" +
                        "              You need at least 3 STONE and 2 GRAIN \n" +
                        "DONE       -- Finish a turn and switch to the next player\n\n";
        return helperMessage;
    }


    private void createInitialPlacements(int playerCount, TextIO textIO, SiedlerGame game) {
        for (int i = 0; i < playerCount; i++) {
            Faction player = game.getCurrentPlayer();
            createInitialSettlement(textIO, game, player, false);
            createInitialRoad(textIO, game, player);
            textIO.getTextTerminal().println(game.getView().toStringWithCoordinates());
            game.switchToNextPlayer();
        }
        game.switchToPreviousPlayer();

        for (int i = 0; i < playerCount; i++) {
            Faction player = game.getCurrentPlayer();
            createInitialSettlement(textIO, game, player, true);
            createInitialRoad(textIO, game, player);
            textIO.getTextTerminal().println(game.getView().toStringWithCoordinates());
            game.switchToPreviousPlayer();
        }
        game.switchToNextPlayer();
    }

    private void createInitialRoad(TextIO textIO, SiedlerGame game, Faction player) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        terminal.println(String.format(SEP + "Player %s: You have to place your road", player));
        boolean validChoice = false;

        while (!validChoice) {
            int xStartCoordinate = textIO.newIntInputReader()
                    .withMinVal(X_MIN)
                    .withMaxVal(X_MAX)
                    .read("Please input x start coordinate of road: ");
            int yStartCoordinate = textIO.newIntInputReader()
                    .withMinVal(Y_MIN)
                    .withMaxVal(Y_MAX)
                    .read("Please input y start coordinate of road: ");
            int xEndCoordinate = textIO.newIntInputReader()
                    .withMinVal(X_MIN)
                    .withMaxVal(X_MAX)
                    .read("Please input x end coordinate of road: ");
            int yEndCoordinate = textIO.newIntInputReader()
                    .withMinVal(Y_MIN)
                    .withMaxVal(Y_MAX)
                    .read("Please input y end coordinate of road: ");

            Point start = new Point(xStartCoordinate, yStartCoordinate);
            Point end = new Point(xEndCoordinate, yEndCoordinate);
            validChoice = game.placeInitialRoad(start, end);
            if (!validChoice) {
                terminal.println("Cannot place road here. Please choose a valid location." + SEP);
            }
        }
    }


    private void createInitialSettlement(TextIO textIO, SiedlerGame game, Faction player, boolean payout) {
        TextTerminal<?> terminal = textIO.getTextTerminal();
        terminal.println(String.format(SEP + "Player %s: You have to place your settlement", player));
        boolean validChoice = false;

        while (!validChoice) {
            int xCoordinate = textIO.newIntInputReader()
                    .withMinVal(X_MIN)
                    .withMaxVal(X_MAX)
                    .read("Please input x coordinate of settlement: ");
            int yCoordinate = textIO.newIntInputReader()
                    .withMinVal(Y_MIN)
                    .withMaxVal(Y_MAX)
                    .read("Please input y coordinate of settlement: ");

            Point choice = new Point(xCoordinate, yCoordinate);
            validChoice = game.placeInitialSettlement(choice, payout);
            if (!validChoice) {
                terminal.println("Cannot place settlement here. Please choose a valid corner." + SEP);
            }
        }
    }

    private static int getNumberOfPlayers(TextIO textIO) {
        int min = Config.MIN_NUMBER_OF_PLAYERS;
        int max = Config.Faction.values().length;
        return textIO.newIntInputReader()
                .withMinVal(min)
                .withMaxVal(max)
                .read(String.format(SEP + "Input number of players (%s-%s):", min, max));
    }

    /**
     * The main method creates a new GameLauncher and runs it.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        GameLauncher launcher = new GameLauncher();
        launcher.run();
    }

    /**
     * TODO
     *
     * @param textIO
     * @param commands
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T getEnumValue(TextIO textIO, Class<T> commands) {
        return textIO.newEnumInputReader(commands).read("What would you like to do?");
    }

    private String getResourceStock(SiedlerGame game) {
        return "You have the following resources in your stock" + SEP +
                "Clay: " + game.getCurrentPlayerResourceStock(Config.Resource.CLAY) + SEP
                + "Grain: " + game.getCurrentPlayerResourceStock(Config.Resource.GRAIN) + SEP
                + "Stone: " + game.getCurrentPlayerResourceStock(Config.Resource.STONE) + SEP
                + "Wood: " + game.getCurrentPlayerResourceStock(Config.Resource.WOOD) + SEP
                + "Wool: " + game.getCurrentPlayerResourceStock(Config.Resource.WOOL) + SEP;

    }

    private String checkHasLongestRoad(SiedlerGame game) {
        if (!game.updateLongestRoad()) {
            return "";
        } else if (game.getCurrentPlayer().equals(game.checkHasLongestRoad().getFaction())) {
            return "You own the longest road." + SEP;
        }
        return "";
    }
}
