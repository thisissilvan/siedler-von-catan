package ch.zhaw.catan;

import ch.zhaw.catan.Config.Faction;
import ch.zhaw.catan.Config.Resource;
import ch.zhaw.catan.Config.Structure;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is responsible for saving the stats of a player.
 *
 * @author Sydney Nguyen und Kunsang Kündetsang
 * @version 2019-11-28
 */

public class Player {
    private Faction faction;
    private List<List<Point>> roads;
    private List<Point> settlements;
    private List<Point> cities;
    private Map<Resource, Integer> resources;
    private Map<Structure, Integer> structureStockPerPlayer; //this is the stock of properties per player as they are limited
    private int points;
    private boolean hasLongestRoad;
    private static final int AMOUNTRESOURCE = 5;
    private static final int NUMBER_OF_ROADS_PER_PLAYER = 15;
    private static final int NUMBER_OF_SETTLEMENTS_PER_PLAYER = 5;
    private static final int NUMBER_OF_CITIES_PER_PLAYER = 4;

    public Player(Faction faction) {
        roads = new ArrayList<>();
        settlements = new ArrayList<>();
        cities = new ArrayList<>();
        this.faction = faction;
        resources = new HashMap<>();
        for (int i = 0; i < AMOUNTRESOURCE; i++) {
            resources.put(Resource.values()[i], 0);
        }
        structureStockPerPlayer = new HashMap<>();
        structureStockPerPlayer.put(Structure.ROAD,NUMBER_OF_ROADS_PER_PLAYER);
        structureStockPerPlayer.put(Structure.SETTLEMENT,NUMBER_OF_SETTLEMENTS_PER_PLAYER);
        structureStockPerPlayer.put(Structure.CITY,NUMBER_OF_CITIES_PER_PLAYER);
    }

    /**
     * The player can build a road. The method saveRoad needs a valid startPoint and a valid endPoint to
     * save the road to the List roadLocation
     * @param spot1 startPoint of the road
     * @param spot2 endPoint of the road
     */
    public void saveRoad(Point spot1, Point spot2) {
        List<Point> roadLocation = new ArrayList<>();
        roadLocation.add(spot1);
        roadLocation.add(spot2);
        roads.add(roadLocation);
        structureStockPerPlayer.put(Structure.ROAD, structureStockPerPlayer.get(Structure.ROAD) - 1);
    }

    /**
     * Save a settlement and give the player a point. When a settlement is saved, the structure Stock is decremented
     * by 1
     * @param spot a valid Point where to save the new settlement
     */
    public void saveSettlement(Point spot) {
        settlements.add(spot);
        structureStockPerPlayer.put(Structure.SETTLEMENT, structureStockPerPlayer.get(Structure.SETTLEMENT) - 1);
        points += 1;
    }

    /**
     * Delete a settlement and put it back to the bank stock, the stock amount is then incremented by 1 and the
     * player who is on the row looses 1 point
     * @param spot the Point where the settlement is
     */
    public void deleteSettlementAndPutBackToStock(Point spot) {
        settlements.remove(spot);
        structureStockPerPlayer.put(Structure.SETTLEMENT, structureStockPerPlayer.get(Structure.SETTLEMENT) + 1);
        points -= 1;
    }

    /**
     * Method to save a city. A valid point must be given as parameter. Winner points are incremented by 2
     * when a city is buildt
     * @param spot a valid Point
     */
    public void saveCity(Point spot) {
        settlements.remove(spot);
        cities.add(spot);
        structureStockPerPlayer.put(Structure.CITY, structureStockPerPlayer.get(Structure.CITY) - 1);
        points += 2;
    }


    /**
     * Check the enum and look for a random resource
     *
     * @return a random resource from the Resource Enum
     */
    public Resource getRandomResource() {
        Random random = new Random();

        // filter all resources which are zero
        List<Resource> greaterZero = new ArrayList<>();
        for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
            if( entry.getValue() > 0) {
                greaterZero.add(entry.getKey());
            }
        }

        // pick random resource which is greater than zero
        int position = random.nextInt(greaterZero.size());

        // return resource
        return greaterZero.get(position);
    }

    /**
     * Gets the size of the Resource Map
     * @return The size of Resources
     */
    public int getSizeOfResourceMap() {
        return resources.size();
    }

    /**
     * Method to set longest road to a player and give this player 2 winnerpoints
     */
    public void setHasLongestRoad() {
        hasLongestRoad = true;
        points += 2;
    }

    /**
     * Method to remove longest road card from a player and take 2 winnerpoints from him
     */
    public void removeHasLongestRoad() {
        hasLongestRoad = false;
        points -= 2;
    }

    public List<List<Point>> getRoads() {
        return roads;
    }

    public List<Point> getSettlements() {
        return settlements;
    }

    public List<Point> getCities() {
        return cities;
    }

    public int getPoints() {
        return points;
    }

    public Faction getFaction() {
        return faction;
    }

    /**
     * Sets the amount of Resources, a newAmount an be given as parameter
     * @param resource  The Resource given
     * @param newAmount The newAmount which has to be set
     */
    public void setResourceAmount(Resource resource, int newAmount) {
        resources.replace(resource, newAmount);
    }

    /**
     * Gets the List of resources of for a given Resoure
     * @param resource  the Resource given
     * @return          the List with the amount of Resources
     */
    public int getResourceAmount(Resource resource) {
        return resources.getOrDefault(resource, 0);
    }

    /**
     * Gets the total amount of resources per Resource
     * @return the sum of resources from the chosen Resource
     */
    public int getTotalResourcesAmount() {
        int sum = 0;
        for (int i : resources.values()) {
            sum += i;
        }
        return sum;
    }

    public int getStructureAmount(Structure structure) {
        return structureStockPerPlayer.get(structure);
    }

    public void setStructureAmount(Structure structure) {
        structureStockPerPlayer.replace(structure,structureStockPerPlayer.get(structure)-1);
    }
}
