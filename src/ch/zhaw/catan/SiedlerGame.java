package ch.zhaw.catan;

import ch.zhaw.catan.Config.Faction;
import ch.zhaw.catan.Config.Resource;
import ch.zhaw.catan.Config.Structure;
import ch.zhaw.hexboard.Label;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import static ch.zhaw.catan.SiedlerBoard.NO_SUCH_EDGE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;

/**
 * The SiedlerGame class holds the game's board and enables the players to interact with the game's state.
 * After creating and running a new SiedlerGame a new SiedlerBoard will be created and the game starts in Phase 1,
 * which initializes the board according to the default layout.
 *
 * @author Ala Hadi und Silvan Lüthy
 * @version 2019/11/27
 */
public class SiedlerGame {
    private Dice dice = new Dice();
    private int winPoints;
    private int currentPlayer = 0;
    private SiedlerBoard board = new SiedlerBoard();
    private List<Faction> factions = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private SiedlerBoardTextView view = new SiedlerBoardTextView(board);
    private Random ran = new Random();
    private Map<Resource, Integer> bank = new HashMap<>();
    private Player playerWithlongestRoad;
    private int lastDiceSum;
    private static final int AMOUNTRESOURCE = Resource.values().length;
    private static final int STOCKRESOURCE = 19;
    private static final int MIN_AMOUNT_FOR_LONGESTSTREET = 5;

    /**
     * Constructor of the class SiedlerGame.
     * <p>
     * It requires the amount of points for winning the game and the number of players
     *
     * @param winPoints The amount of points needed for winning the game
     * @param players   The amount of players.
     *                  The minimum amount is defined in Config.MIN_NUMBER_OF_PLAYERS
     *                  The maximum amount is defined by the amount of Factions in the Faction enum
     */
    public SiedlerGame(int winPoints, int players) {
        this.winPoints = winPoints;

        // create factions
        for (int i = 0; i < players; i++) {
            this.factions.add(Faction.values()[i]);
            this.players.add(new Player(Faction.values()[i]));
        }

        //set default resources from bank
        for (int i = 0; i < AMOUNTRESOURCE; i++) {
            bank.put(Resource.values()[i],STOCKRESOURCE); //TODO: remove this magic number
        }

        // set default game layout
        for (Map.Entry<Point, Config.Land> entry : Config.getStandardLandPlacement().entrySet()) {
            board.addField(entry.getKey(), entry.getValue());
        }

        // add labels to view
        for (Map.Entry<Point, Integer> entry : Config.getStandardDiceNumberPlacement().entrySet()) {
            String label = String.format("%02d", entry.getValue());
            view.setLowerFieldLabel(entry.getKey(), new Label(label.charAt(0), label.charAt(1)));
        }
    }

    /**
     * Switches to the next player according to the Faction enum
     */
    public void switchToNextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    /**
     * Switches to the previous player according to the Faction enum
     */
    public void switchToPreviousPlayer() {
        currentPlayer = (currentPlayer - 1 + players.size()) % players.size();
    }

    public SiedlerBoard getBoard() {
        return this.board;
    }

    public Faction getCurrentPlayer() {
        return factions.get(currentPlayer);
    }

    public int getCurrentPlayerResourceStock(Resource resource) {
        return players.get(currentPlayer).getResourceAmount(resource);
    }

    /**
     * This method adds a settlement at a given location on the board. If the location is already taken, or
     * not a valid corner, or doesn't adhere to the distance rule no settlement will be created. Otherwise a
     * settlement for the currently active player will be placed on the board
     *
     * @param position The point on the SiedlerBoard on which to place the settlement
     * @param payout   Boolean which defines if a payout should follow after placing the settlement
     * @return boolean True if the settlement could be placed successfully, false otherwise.
     */
    public boolean placeInitialSettlement(Point position, boolean payout) {
        // check if settlement can be created at position
        if (!board.hasCorner(position) || !checkCornerFree(position) || !checkDistanceRule(position)) {
            return false;
        }

        // create settlement
        board.putSettlement(position, getCurrentPlayer().toString());
        players.get(currentPlayer).saveSettlement(position);
        players.get(currentPlayer).setStructureAmount(Structure.SETTLEMENT);

        // do payout
        if (payout) {
            List<Config.Land> fields = board.getFields(position);
            for (Config.Land land : fields) {
                if (!land.equals(Config.Land.WATER)) {
                    increaseResourcesAndUpdateBankResources(land.getResource(), 1);
                }
            }
        }

        return true;
    }

    /**
     * This method adds a road at a given location on the board. If the location is already taken by another player,
     * or not a valid edge, or doesn't start or end in any of the player's settlements no road will be created.
     * Otherwise a road for the currently active player will be placed on the board
     *
     * @param roadStart The starting point on the SiedlerBoard on which to place the road
     * @param roadEnd   The end point on the SiedlerBoard on which to place the road
     * @return boolean  True if the road could be placed successfully, false otherwise.
     */
    public boolean placeInitialRoad(Point roadStart, Point roadEnd) {
        String player = getCurrentPlayer().toString();
        String edgeData = board.getEdge(roadStart, roadEnd);
        if (NO_SUCH_EDGE.equals(edgeData) || null != edgeData) {
            return false;
        }

        // you can only place a road adjacent to a settlement of the player
        if (!(board.getCorner(roadStart) != null && board.getCorner(roadStart).equals(player))
                && !(board.getCorner(roadEnd) != null && board.getCorner(roadEnd).equals(player))) {
            return false;
        }

        // write player edge data to the board
        board.putRoad(roadStart, roadEnd, player.substring(1));
        players.get(currentPlayer).saveRoad(roadEnd, roadStart);
        players.get(currentPlayer).setStructureAmount(Structure.ROAD);

        //return currentPlayer.addEdge(roadStart, roadEnd);
        return true;
    }

    private int getRandomDiceRoll(int dicethrows) {
        return dice.rollTheDice(dicethrows);
    }

    /**
     * Returns the last thrown diceSum
     *
     * @return The diceSum that was thrown last
     */
    public int getLastDiceSum() {
        return lastDiceSum;
    }

    /**
     * The method throwDice makes a dice roll with a given amount of dices. In the Siedler-Game, the amount of dices
     * must be two. After throwing two dices, the method checks, if the diceSum is equal or not eqal to seven.
     * <p>
     * The method makes a list of all Players(Faction) and their resources.
     * <p>
     * If the diceSum is equal to seven, the method stealResources gets executed and the players with more than
     * seven cards of a random resource must give away half. If the diceSum is not equal to seven, the bank
     * pays resources to the players according to their possessions.
     *
     * @param dicethrow the amount of dices to play with, should be two for the Siedler-Game
     * @return payout, the Map RFesources per Faction
     */
    public Map<Faction, List<Resource>> throwDice(int dicethrow) {
        Map<Faction, List<Resource>> result = new HashMap<>();

        // throw the dice
        int diceSum = getRandomDiceRoll(dicethrow);
        this.lastDiceSum = diceSum;

        // if dice is 7
        if (diceSum == 7) {
            stealResources();
        } else {
            result = getResourceDistributionForThrow(diceSum);
        }

        return result;
    }

    /**
     * This implements the Thanos Snap of resources. On a dice roll of 7 all players with more than
     * 7 resources will lose half of their stock in a randomly chosen fashion.
     */
    private void stealResources() {
        // find all players with more than 7 resources
        for (Player player : players) {
            int sum = player.getTotalResourcesAmount();
            if (sum <= 7) {
                continue;
            }

            // amount N to delete is half of what the player has
            int toDelete = Math.floorDiv(sum, 2);

            // delete N random resources and return to bank
            for (int i = 0; i < toDelete; i++) {
                Resource resource = player.getRandomResource();
                player.setResourceAmount(resource, player.getResourceAmount(resource) - 1);
                bank.merge(resource, 1, Integer::sum);
            }
        }
    }

    /**
     * Each player gets 1 Resource for each settlement and 2 Resources for each city from an adjacent field of the
     * thrown number.
     *
     * @param diceSum the thrown diceSum
     * @return Map<Faction, List < Resource>> Returns a Map of resources each player should be payed out with
     */
    Map<Faction, List<Resource>> getResourceDistributionForThrow(int diceSum) {
        // find all fields with annotation diceSum
        Map<Point, Integer> fields = findFieldByAnnotation(diceSum);
        Map<Faction, List<Resource>> result = new HashMap<>();

        // for each field annotated with diceSum do
        for (Point point : fields.keySet()) {
            List<String> corners = board.getCornersOfField(point);
            Resource resource = board.getField(point).getResource();

            for (String cornerData : corners) {
                if (cornerData.equals(cornerData.toUpperCase())) {
                    getResourcesForCities(result, resource, cornerData);
                } else {
                    getResourcesForSettlements(result, resource, cornerData);
                }
            }
        }
        return result;
    }

    // check for city
    private void getResourcesForCities(Map<Faction, List<Resource>> result, Resource resource, String data) {
        try {
            Faction fac = Faction.findByName(data.toLowerCase());
            result.merge(fac, new ArrayList<>(asList(resource, resource)), (a, b) -> {
                a.addAll(b);
                return a;
            });
        } catch (IllegalArgumentException | NoSuchElementException e) {
            // not a city of any faction
        }
    }

    // check for settlement
    private void getResourcesForSettlements(Map<Faction, List<Resource>> result, Resource resource, String data) {
        try {
            Faction fac = Faction.findByName(data);
            result.merge(fac, new ArrayList<>(singletonList(resource)), (a, b) -> {
                a.addAll(b);
                return a;
            });

        } catch (IllegalArgumentException e) {
            // not a settlement of any faction
        }
    }

    private Map<Point, Integer> findFieldByAnnotation(int diceSum) {
        Map<Point, Integer> fields = Config.getStandardDiceNumberPlacement();
        return fields.entrySet().stream()
                .filter(entry -> entry.getValue() == diceSum)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    /**
     * Player with longest road will get assigned the title of having the longest road
     *
     * @return true if a player has a longest street
     */
    public boolean updateLongestRoad() {
        if (checkHasLongestRoad() == (null)) {
            return false;
        }
        if (playerWithlongestRoad == null) {
            playerWithlongestRoad = checkHasLongestRoad();
            playerWithlongestRoad.setHasLongestRoad();
            return true;
        } else if (!playerWithlongestRoad.equals(checkHasLongestRoad())) {
            playerWithlongestRoad.removeHasLongestRoad();
            playerWithlongestRoad = checkHasLongestRoad();
            playerWithlongestRoad.setHasLongestRoad();
            return true;
        }
        return false;
    }

    /**
     * Checks which player has the longest road. All points that appear more than once will be counted.
     * The player who has the biggest count will receive the title of having the longest road.
     * @return player with the longest road
     */
    //p.getRoads() returns List<List<Point>> streets
    //so many loops as i need to save all points per player in a set and list.
    public Player checkHasLongestRoad(){
        int maxLength = 0;
        Player playerLongestRoad = null;
        for(Player p : players) {
            Set<Point> set = new HashSet<>();
            int count = 0;
            int roadLength = 0;
            for (int i = 0; i < p.getRoads().size(); i++) {
                for (int j = 0; j < 2; j++) {
                    if(set.contains(p.getRoads().get(i).get(j)))
                        count++;
                    else
                        set.add(p.getRoads().get(i).get(j));
                }
            }
            roadLength = count+1;
            if(roadLength > maxLength && roadLength >= MIN_AMOUNT_FOR_LONGESTSTREET) {
                maxLength = roadLength;
                playerLongestRoad = p;
            }
        }
        return playerLongestRoad;
    }

    /**
     * A road will be built for certain player.
     * Different conditions will be checked.
     * Board and resource/structure/bank stocks will be updated
     *
     * @param roadStart
     * @param roadEnd
     * @return true if road is successfully built
     */
    public boolean buildRoad(Point roadStart, Point roadEnd) {
        if (!(checkEdgeFree(roadStart, roadEnd) && playerHasEnoughResourcesForRoad() && playerHasEnoughStructure(Config.Structure.ROAD)
                && (isAdjacentToOwnRoad(roadStart) || isAdjacentToOwnRoad(roadEnd)
                || isAdjacentToOwnStructure(roadStart, getCurrentPlayer().toString()) ||
                isAdjacentToOwnStructure(roadEnd, getCurrentPlayer().toString())))) {
            return false;
        } else {
            board.putRoad(roadStart, roadEnd, getCurrentPlayer().toString().substring(1));
            players.get(currentPlayer).saveRoad(roadStart, roadEnd);
            decreaseResourcesAndUpdateBankResources(Resource.CLAY, 1);
            decreaseResourcesAndUpdateBankResources(Resource.WOOD, 1);
            players.get(currentPlayer).setStructureAmount(Structure.ROAD);
            return true;
        }
    }

    private boolean isAdjacentToOwnStructure(Point point, String player) {
        return board.getCorner(point) != null && board.getCorner(point).toLowerCase().equals(player);
    }

    /**
     * A settlement will be built for certain player.
     * Different conditions will be checked.
     * Board and resource/structure/bank stocks will be updated
     *
     * @param position
     * @return true if settlement is successfully built
     */
    public boolean buildSettlement(Point position) {
            if (!(checkCornerFree(position) && checkDistanceRule(position)
                    && playerHasEnoughResourcesForSettlement()
                    && playerHasEnoughStructure(Config.Structure.SETTLEMENT)
                    && isAdjacentToOwnRoad(position))) {
                return false;
            } else {
                board.putSettlement(position, getCurrentPlayer().toString());
                players.get(currentPlayer).saveSettlement(position);
                decreaseResourcesAndUpdateBankResources(Resource.CLAY, 1);
                decreaseResourcesAndUpdateBankResources(Resource.WOOD, 1);
                decreaseResourcesAndUpdateBankResources(Resource.WOOL, 1);
                decreaseResourcesAndUpdateBankResources(Resource.GRAIN, 1);
                players.get(currentPlayer).setStructureAmount(Structure.SETTLEMENT);
                return true;
            }
        }


    /**
     * A city will be built for certain player.
     * Different conditions will be checked.
     * Board and resource/structure/bank stocks will be updated
     *
     * @param position
     * @return true if city is successfully built
     */
    public boolean buildCity(Point position) {
        //make sure this point has players own settlement
        if (!(board.getCorner(position).equals(players.get(currentPlayer).getFaction().toString())
                && playerHasEnoughResourcesForCity() &&
                playerHasEnoughStructure(Config.Structure.CITY))) {
            return false;
        } else {
            board.putCity(position, players.get(currentPlayer).getFaction().toString().toUpperCase());
            players.get(currentPlayer).deleteSettlementAndPutBackToStock(position);
            players.get(currentPlayer).saveCity(position);
            decreaseResourcesAndUpdateBankResources(Resource.STONE, 3);
            decreaseResourcesAndUpdateBankResources(Resource.GRAIN, 2);
            players.get(currentPlayer).setStructureAmount(Structure.CITY);
            return true;
        }
    }

    /**
     * Trade with bank via offering 4 of a kind of a resource against another kind of a resource.
     * Certain resource/bank stocks will be updated
     *
     * @param offer
     * @param want
     * @return true if trade is successful
     */
    //edge case: what if player wants to trade several things at once?
    public boolean tradeWithBankFourToOne(Resource offer, Resource want) {
            if (!checkTradeWithBankFourToOne(offer, want)) {
                return false;
            } else {
                decreaseResourcesAndUpdateBankResources(offer, 4);
                increaseResourcesAndUpdateBankResources(want, 1);
                return true;
            }
        }


    /**
     * Calculates which player has the biggest amount of points
     *
     * @return the winner
     */
    public Faction getWinner() {
        int playerPoints = 0;
        Player winner = null;
        for (Player player : players) {
            if (playerPoints < player.getPoints()) {
                playerPoints = player.getPoints();
                winner = player;
            }
        }
        if (playerPoints >= winPoints) {
            return winner.getFaction();
        }
        return null;
    }

    public SiedlerBoardTextView getView() {
        return view;
    }

    private boolean checkDistanceRule(Point position) {
        List<Point> cornerPoints = SiedlerBoard.getAdjacentCorners(position);
        boolean canPlaceHere = true;
        for (Point p : cornerPoints) {
            canPlaceHere &= board.getCorner(p) == null;
        }
        return canPlaceHere;
    }

    private boolean checkCornerFree(Point position) {
        return board.getCorner(position) == null;
    }

    private boolean checkEdgeFree(Point position1, Point position2) {
        return board.getEdge(position1, position2) == null;
    }

    public boolean checkTradeWithBankFourToOne(Resource offer, Resource want){
        if (!(bankHasEnoughResource(want) && (players.get(currentPlayer).getResourceAmount(offer) >= 4))) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a certain point is adjacent to a player's road by iterationg through their adjacent edge list
     *
     * @param position
     * @return true if the point is adjacent to a player's road
     */
    public boolean isAdjacentToOwnRoad(Point position) {
        List<String> adjacentEdges = board.getAdjacentEdges(position);
        for (String edge : adjacentEdges) {
            if (edge.equals(players.get(currentPlayer).getFaction().toString().substring(1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a certain player has enough resources to build a road by comparing the number of specific resources with the required amount
     *
     * @return true if it is possible to built a road
     */
    public boolean playerHasEnoughResourcesForRoad() {
        boolean hasEnoughResources = false;
        if (players.get(currentPlayer).getResourceAmount(Resource.CLAY) >= 1 &&
                players.get(currentPlayer).getResourceAmount(Resource.WOOD) >= 1) {
            hasEnoughResources = true;
        }
        return hasEnoughResources;
    }

    /**
     * Checks if a certain player has enough resources to build a settlement by comparing the number of specific resources with the required amount
     *
     * @return true if it is possible to built a settlement
     */
    public boolean playerHasEnoughResourcesForSettlement() {
        boolean hasEnoughResources = false;
        if (players.get(currentPlayer).getResourceAmount(Resource.CLAY) >= 1 &&
                players.get(currentPlayer).getResourceAmount(Resource.GRAIN) >= 1 &&
                players.get(currentPlayer).getResourceAmount(Resource.WOOD) >= 1 &&
                players.get(currentPlayer).getResourceAmount(Resource.WOOL) >= 1) {
            hasEnoughResources = true;
        }
        return hasEnoughResources;
    }

    /**
     * Checks if a certain player has enough resources to build a city by comparing the number of specific resources with the required amount
     *
     * @return true if it is possible to built a city
     */
    public boolean playerHasEnoughResourcesForCity() {
        boolean hasEnoughResources = false;
        if (players.get(currentPlayer).getResourceAmount(Resource.STONE) >= 3 &&
                players.get(currentPlayer).getResourceAmount(Resource.GRAIN) >= 2) {
            hasEnoughResources = true;
        }
        return hasEnoughResources;
    }

    /**
     * Checks if bank has enough of a certain resource in stock
     *
     * @param resource
     * @return true if bank has at least one of this resource
     */
    public boolean bankHasEnoughResource(Resource resource) {
        boolean hasEnoughResources = false;
        if (bank.get(resource) >= 1) {
            hasEnoughResources = true;
        }
        return hasEnoughResources;
    }

    /**
     * Increases a players chosen resource amount and decreases the bank's equivalent resource stock
     *
     * @param resource
     * @param amount
     */
    public void increaseResourcesAndUpdateBankResources(Resource resource, int amount) {
        players.get(currentPlayer).setResourceAmount(resource, players.get(currentPlayer).getResourceAmount(resource) + amount);
        bank.replace(resource, bank.get(resource) - amount);
    }

    /**
     *  Decreases a players chosen resource amount and increases the bank's equivalent resource stock
     *
     * @param resource
     * @param amount
     */
    public void decreaseResourcesAndUpdateBankResources(Resource resource, int amount) {
        players.get(currentPlayer).setResourceAmount(resource, players.get(currentPlayer).getResourceAmount(resource) - amount);
        bank.replace(resource, bank.get(resource) + amount);
    }

    /**
     * Checks if player has at least one of certain resource
     *
     * @param structure
     * @return true if player has enough of certain resource
     */
    public boolean playerHasEnoughStructure(Structure structure) {
        boolean hasEnoughResources = false;
        if (players.get(currentPlayer).getStructureAmount(structure) >= 1) {
            hasEnoughResources = true;
        }
        return hasEnoughResources;
    }

    public Faction getPlayerWithlongestRoad() {
        if(!(playerWithlongestRoad==null))
        return playerWithlongestRoad.getFaction();
        return null;
    }

    /**
     * This method distributes resources to players according to the provided map. For each resource that
     * gets added to the player's stock the respective amount gets reduced at the bank.
     *
     * @param resources A map containing players and a list of resources.
     */
    public void distributeResources(Map<Faction, List<Resource>> resources) {
        for (Map.Entry<Faction, List<Resource>> entry : resources.entrySet()) {
            Player player = findByFaction(entry.getKey());
            for (Resource resource : entry.getValue()) {
                player.setResourceAmount(resource, player.getResourceAmount(resource) + 1);
                bank.put(resource, bank.get(resource) - 1);
            }
        }
    }

    Player findByFaction(Faction faction) {
        return players.stream()
                .filter(player -> player.getFaction().equals(faction))
                .findFirst()
                .orElseThrow();
    }
}
