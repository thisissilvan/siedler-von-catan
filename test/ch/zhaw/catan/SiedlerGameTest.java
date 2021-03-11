package ch.zhaw.catan;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiedlerGameTest {

    private final static String SEP = System.lineSeparator();
    private final static String BOARD_LAYOUT =
            "                                    (  )              (  )              (  )              (  )                                                  " + SEP +
                    "                                  //    \\\\          //    \\\\          //    \\\\          //    \\\\                                          " + SEP +
                    "                               //          \\\\    //          \\\\    //          \\\\    //          \\\\                                       " + SEP +
                    "                           (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                            ||       ~~       ||       ~~       ||       ~~       ||       ~~       ||                                 " + SEP +
                    "                            ||                ||                ||                ||                ||                                 " + SEP +
                    "                           (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                         //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\                                 " + SEP +
                    "                      //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\                              " + SEP +
                    "                  (  )              (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                   ||       ~~       ||       WD       ||       WL       ||       WL       ||       ~~       ||                                 " + SEP +
                    "                   ||                ||       06       ||       03       ||       08       ||                ||                                 " + SEP +
                    "                  (  )              (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\                        " + SEP +
                    "             //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\                     " + SEP +
                    "         (  )              (  )              (  )              (  )              (  )              (  )              (  )              " + SEP +
                    "          ||       ~~       ||       GR       ||       ST       ||       GR       ||       WD       ||       ~~       ||               " + SEP +
                    "          ||                ||       02       ||       04       ||       05       ||       10       ||                ||               " + SEP +
                    "         (  )              (  )              (  )              (  )              (  )              (  )              (  )              " + SEP +
                    "       //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\               " + SEP +
                    "    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\            " + SEP +
                    "(  )              (  )              (  )              (  )              (  )              (  )              (  )              (  )              " + SEP +
                    " ||       ~~       ||       WD       ||       CL       ||       --       ||       ST       ||       GR       ||       ~~       ||               " + SEP +
                    " ||                ||       05       ||       09       ||       07       ||       06       ||       09       ||                ||               " + SEP +
                    "(  )              (  )              (  )              (  )              (  )              (  )              (  )              (  )              " + SEP +
                    "    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //            " + SEP +
                    "       \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //               " + SEP +
                    "         (  )              (  )              (  )              (  )              (  )              (  )              (  )              " + SEP +
                    "          ||       ~~       ||       GR       ||       ST       ||       WD       ||       WL       ||       ~~       ||               " + SEP +
                    "          ||                ||       10       ||       11       ||       03       ||       12       ||                ||               " + SEP +
                    "         (  )              (  )              (  )              (  )              (  )              (  )              (  )              " + SEP +
                    "             \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //                     " + SEP +
                    "                \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //                        " + SEP +
                    "                  (  )              (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                   ||       ~~       ||       WL       ||       CL       ||       CL       ||       ~~       ||                                 " + SEP +
                    "                   ||                ||       08       ||       04       ||       11       ||                ||                                 " + SEP +
                    "                  (  )              (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                      \\\\          //    \\\\          //    \\\\          //    \\\\          //    \\\\          //                              " + SEP +
                    "                         \\\\    //          \\\\    //          \\\\    //          \\\\    //          \\\\    //                                 " + SEP +
                    "                           (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                            ||       ~~       ||       ~~       ||       ~~       ||       ~~       ||                                 " + SEP +
                    "                            ||                ||                ||                ||                ||                                 " + SEP +
                    "                           (  )              (  )              (  )              (  )              (  )                                " + SEP +
                    "                               \\\\          //    \\\\          //    \\\\          //    \\\\          //                                       " + SEP +
                    "                                  \\\\    //          \\\\    //          \\\\    //          \\\\    //                                          " + SEP +
                    "                                    (  )              (  )              (  )              (  )                                                  " + SEP +
                    "                                                                                                                                                " + SEP +
                    "                                                                                                                                                " + SEP;

    @Test
    void testGamePhaseOneLayout() {
        SiedlerGame game = new SiedlerGame(-1, -1);
        SiedlerBoardTextView view = game.getView();

        assertEquals(BOARD_LAYOUT, view.toString(), "board layout does not match");
    }

    @Test
    void testPlaceInitialSettlement() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point location = new Point(7, 3);
        boolean success = game.placeInitialSettlement(location, false);

        assertTrue(success, "Expected the settlement placement to be successful but it wasn't");
        assertEquals(player.toString(), game.getBoard().getCorner(location), "Expected the settlement to belong to the player, but it didn't.");
    }

    @Test
    void testPlaceInitialSettlementWrongLocation() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point location = new Point(-1, -3);
        boolean success = game.placeInitialSettlement(location, false);

        assertFalse(success, "Expected the settlement placement to be unsuccessful but it wasn't");
    }

    @Test
    void testPlaceInitialSettlementLocationTaken() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player1 = game.getCurrentPlayer();
        Point location = new Point(7, 3);
        boolean success1 = game.placeInitialSettlement(location, false);

        game.switchToNextPlayer();
        Config.Faction player2 = game.getCurrentPlayer();
        boolean success2 = game.placeInitialSettlement(location, false);

        assertTrue(success1, "Expected the settlement placement to be successful but it wasn't");
        assertFalse(success2, "Expected the settlement placement to be unsuccessful but it wasn't");
        assertEquals(player1.toString(), game.getBoard().getCorner(location), "Expected the settlement to belong to the player1, but it didn't.");
    }

    @Test
    void testPlaceInitialRoad() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point loc1 = new Point(7, 3);
        Point loc2 = new Point(8, 4);
        boolean successSettlement = game.placeInitialSettlement(loc1, false);
        boolean successRoad = game.placeInitialRoad(loc1, loc2);

        assertTrue(successRoad, "Expected the road placement to be successful but it wasn't");
        assertEquals(player.toString().substring(1), game.getBoard().getEdge(loc1, loc2), "Expected the settlement to belong to the player, but it didn't.");
    }

    @Test
    void testPlaceInitialRoadOutOfBounds() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point loc1 = new Point(7, 3);
        Point loc2 = new Point(8, -4);
        boolean successSettlement = game.placeInitialSettlement(loc1, false);
        boolean successRoad = game.placeInitialRoad(loc1, loc2);

        assertTrue(successSettlement, "Expected the settlement placement to be successful but it wasn't");
        assertFalse(successRoad, "Expected the road placement to be unsuccessful but it wasn't");
    }

    @Test
    void testPlaceInitialRoadNoSettlement() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point loc1 = new Point(7, 3);
        Point loc2 = new Point(8, 4);
        boolean successRoad = game.placeInitialRoad(loc1, loc2);

        assertFalse(successRoad, "Expected the road placement to be unsuccessful but it wasn't");
    }

    @Test
    void testPlaceInitialRoadRoadTaken() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point loc1 = new Point(7, 3);
        Point loc2 = new Point(8, 4);
        boolean successSettlement = game.placeInitialSettlement(loc1, false);
        boolean successRoad = game.placeInitialRoad(loc1, loc2);

        game.switchToNextPlayer();
        Config.Faction player2 = game.getCurrentPlayer();
        boolean successRoad2 = game.placeInitialRoad(loc1, loc2);

        assertTrue(successRoad, "Expected the road placement to be successful but it wasn't");
        assertFalse(successRoad2, "Expected the road placement to be unsuccessful but it wasn't");
    }

    @Test
    void testPlaceInitialRoadNoValidEdge() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Config.Faction player = game.getCurrentPlayer();
        Point loc1 = new Point(7, 3);
        Point loc2 = new Point(4, 16);
        boolean successSettlement = game.placeInitialSettlement(loc1, false);
        boolean successRoad = game.placeInitialRoad(loc1, loc2);

        assertFalse(successRoad, "Expected the road placement to be unsuccessful but it wasn't");
    }

    @Test
    void testPayoutAfterInitialSettlement() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Point location = new Point(7, 3);
        int before = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);
        game.placeInitialSettlement(location, true);
        int after = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);

        assertEquals(before + 1, after, "Wool resource should have increased by one, but it wasn't.");
    }

    @Test
    void testPayoutAfterInitialSettlement2() {
        SiedlerGame game = new SiedlerGame(-1, 2);
        Point location = new Point(8, 6);
        int beforeWool = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);
        int beforeGrain = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);
        game.placeInitialSettlement(location, true);
        int afterWool = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);
        int afterGrain = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);

        assertEquals(beforeWool + 2, afterWool, "Wool resource should have increased by two, but it wasn't.");
        assertEquals(beforeGrain + 1, afterGrain, "Grain resource should have increased by one, but it wasn't.");
    }

    @Test
    void positiveTestTradeWithBankFourToOne() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 4);
        assertTrue(game.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.WOOD));
        assertEquals(1, game.getCurrentPlayerResourceStock(Config.Resource.WOOD));

    }

    @Test
    void negativeTestTradeWithBankFourToOne() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 2);
        assertFalse(game.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.WOOD));
        assertEquals(2, game.getCurrentPlayerResourceStock(Config.Resource.WOOL));
    }

    @Test
    void tradeWithBankFourToOneTest() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 4);
        game.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.WOOD);
        assertEquals(1, game.getCurrentPlayerResourceStock(Config.Resource.WOOD));
    }

    @Test
    void decreaseResourcesAndUpdateBankResourcesTest(){
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 4);
        game.decreaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 1);
        assertEquals(3, game.getCurrentPlayerResourceStock(Config.Resource.WOOL));
    }

    @Test
    void increaseResourcesAndUpdateBankResourcesTest(){
        SiedlerGame game = new SiedlerGame(1, 2);
        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 1);
        assertEquals(1, game.getCurrentPlayerResourceStock(Config.Resource.WOOD));
    }

    @Test
    void nullObjectTestTradeWithBankFourToOne() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 0);
        assertFalse(game.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.WOOD));
        assertEquals(0, game.getCurrentPlayerResourceStock(Config.Resource.WOOL));
    }

    @Test
    void playerHasEnoughResourcesForRoad() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 1);

        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        int clayStock = game.getCurrentPlayerResourceStock(Config.Resource.CLAY);

        assertTrue(game.playerHasEnoughResourcesForRoad());
        assertEquals(1, woodStock);
        assertEquals(1, clayStock);
    }

    @Test
    void playerHasNullResourcesForRoad() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 0);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 0);

        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        int clayStock = game.getCurrentPlayerResourceStock(Config.Resource.CLAY);

        assertFalse(game.playerHasEnoughResourcesForRoad());
        assertEquals(0, woodStock);
        assertEquals(0, clayStock);
    }

    @Test
    void playerHasNoWoodForRoad() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 0);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 1);

        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        int clayStock = game.getCurrentPlayerResourceStock(Config.Resource.CLAY);

        assertFalse(game.playerHasEnoughResourcesForRoad());
        assertEquals(0, woodStock);
        assertEquals(1, clayStock);
    }

    @Test
    void playerHasEnoughResourcesForSettlement() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 1);

        int clayStock = game.getCurrentPlayerResourceStock(Config.Resource.CLAY);
        int grainStock = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);
        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        int woolStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);

        assertTrue(game.playerHasEnoughResourcesForSettlement());
        assertEquals(1, clayStock);
        assertEquals(1, grainStock);
        assertEquals(1, woodStock);
        assertEquals(1, woolStock);
    }

    @Test
    void playerHasNullResourcesForSettlement() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 0);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 0);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 0);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 0);

        int clayStock = game.getCurrentPlayerResourceStock(Config.Resource.CLAY);
        int grainStock = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);
        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        int woolStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);

        assertFalse(game.playerHasEnoughResourcesForSettlement());
        assertEquals(0, clayStock);
        assertEquals(0, grainStock);
        assertEquals(0, woodStock);
        assertEquals(0, woolStock);
    }

    @Test
    void playerHasNoWoolForSettlement() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 1);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOL, 0);

        int clayStock = game.getCurrentPlayerResourceStock(Config.Resource.CLAY);
        int grainStock = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);
        int woodStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOD);
        int woolStock = game.getCurrentPlayerResourceStock(Config.Resource.WOOL);

        assertFalse(game.playerHasEnoughResourcesForSettlement());
        assertEquals(1, clayStock);
        assertEquals(1, grainStock);
        assertEquals(1, woodStock);
        assertEquals(0, woolStock);
    }

    @Test
    void playerHasEnoughResourcesForCity() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.STONE, 3);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 2);

        int stoneStock = game.getCurrentPlayerResourceStock(Config.Resource.STONE);
        int grainStock = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);

        assertTrue(game.playerHasEnoughResourcesForCity());
        assertEquals(3, stoneStock);
        assertEquals(2, grainStock);
    }

    @Test
    void playerHasNullResourcesForCity() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.STONE, 0);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 0);

        int stoneStock = game.getCurrentPlayerResourceStock(Config.Resource.STONE);
        int grainStock = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);

        assertFalse(game.playerHasEnoughResourcesForCity());
        assertEquals(0, stoneStock);
        assertEquals(0, grainStock);
    }


    @Test
    void playerHasNoGrainForCity() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.STONE, 3);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.GRAIN, 0);

        int stoneStock = game.getCurrentPlayerResourceStock(Config.Resource.STONE);
        int grainStock = game.getCurrentPlayerResourceStock(Config.Resource.GRAIN);

        assertFalse(game.playerHasEnoughResourcesForCity());
        assertEquals(3, stoneStock);
        assertEquals(0, grainStock);
    }

    @Test
    void bankHasEnoughResourceWood() {
        SiedlerGame game = new SiedlerGame(1, 2);
        // Give 19 Resources to bank
        game.decreaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 20);
        // decrease Resource from player and increase Resource from Bank
        assertTrue(game.bankHasEnoughResource(Config.Resource.WOOD));
    }

    @Test
    void bankHasNotEnoughResources() {
        SiedlerGame game = new SiedlerGame(1, 2);
        // Give 19 Resources to bank
        game.increaseResourcesAndUpdateBankResources(Config.Resource.STONE, 19);
        // increase Resource from player and decrease Resource from Bank
        assertFalse(game.bankHasEnoughResource(Config.Resource.STONE));
    }

    @Test
    void bankBankrupt() {
        SiedlerGame game = new SiedlerGame(1, 2);
        // Give 19 Resources to bank
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 20);
        // increase Resource from player and decrease Resource from Bank
        assertFalse(game.bankHasEnoughResource(Config.Resource.CLAY));
    }

    @Test
    void testDistributeResources() {
        SiedlerGame game = new SiedlerGame(1, 2);
        Map<Config.Faction, List<Config.Resource>> resources = new HashMap<>();
        resources.put(Config.Faction.RED, Arrays.asList(Config.Resource.WOOD, Config.Resource.WOOD, Config.Resource.WOOD));
        resources.put(Config.Faction.BLUE, Arrays.asList(Config.Resource.GRAIN, Config.Resource.WOOL, Config.Resource.STONE));
        game.distributeResources(resources);

        assertTrue(game.findByFaction(Config.Faction.RED).getResourceAmount(Config.Resource.WOOD) == 3);
        assertTrue(game.findByFaction(Config.Faction.BLUE).getResourceAmount(Config.Resource.WOOL) == 1);
        assertTrue(game.findByFaction(Config.Faction.BLUE).getResourceAmount(Config.Resource.STONE) == 1);
        assertTrue(game.findByFaction(Config.Faction.BLUE).getResourceAmount(Config.Resource.GRAIN) == 1);
    }

    @Test
    void testBuildRoadHappyPath() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.placeInitialSettlement(new Point(6, 4), false);
        game.placeInitialRoad(new Point(6, 4), new Point(6, 6));
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 2);
        boolean result = game.buildRoad(new Point(6, 6), new Point(7, 7));

        assertTrue(result, "The road extension should have worked");
    }
    @Test
    void testBuildRoadATakenRoad() {
        SiedlerGame game = new SiedlerGame(1, 2);
        game.placeInitialSettlement(new Point(6, 4), false);
        game.placeInitialRoad(new Point(6, 4), new Point(6, 6));
        game.increaseResourcesAndUpdateBankResources(Config.Resource.CLAY, 2);
        game.increaseResourcesAndUpdateBankResources(Config.Resource.WOOD, 2);
        boolean result = game.buildRoad(new Point(6, 6), new Point(6, 4));

        assertFalse(result, "The road extension shouldn't have worked");
    }
}