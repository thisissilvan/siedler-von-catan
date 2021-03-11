package ch.zhaw.catan;

import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import ch.zhaw.catan.Config.Faction;
import ch.zhaw.catan.Config.Resource;
import ch.zhaw.catan.Config.Structure;


public class PlayerTest {

    Player player = new Player(Faction.RED);

    @Test
    void testSaveRoad() {
        player.saveRoad(new Point(6, 4),new Point (7,3));
        assertEquals(1, player.getRoads().size());
        assertEquals(14, player.getStructureAmount(Structure.ROAD));
    }

    @Test
    void testSaveSettlement() {
        player.saveSettlement(new Point(6,4));
        assertEquals(1,player.getSettlements().size());
        assertEquals(4,player.getStructureAmount(Config.Structure.SETTLEMENT));
        assertEquals(1,player.getPoints());
    }

    @Test
    void testSaveCity() {
        player.saveSettlement(new Point(6,4));
        player.deleteSettlementAndPutBackToStock(new Point(6,4));
        player.saveCity(new Point(6,4));
        assertNotEquals(1,player.getSettlements().size());
        assertEquals(1,player.getCities().size());
        assertNotEquals(4,player.getStructureAmount(Config.Structure.SETTLEMENT));
        assertEquals(3,player.getStructureAmount(Config.Structure.CITY));
        assertEquals(2, player.getPoints());

    }

    @Test
    void testDeleteSettlementAndPutBackToStock(){
        Point point  = new Point(7,7);
        player.saveSettlement(point);
        player.deleteSettlementAndPutBackToStock(point);
        assertEquals(0, player.getSettlements().size());
        assertEquals(5, player.getStructureAmount(Structure.SETTLEMENT));
    }

    @Test
    void testGetSizeOfResourceMap(){
        assertEquals(5, player.getSizeOfResourceMap());
    }

    @Test
    void testSetHasLongestRoad(){
        player.setHasLongestRoad();
        assertEquals(2, player.getPoints());
    }
    @Test
    void testRemoveHasLongestRoad() {
        player.setHasLongestRoad();
        player.removeHasLongestRoad();
        assertEquals(0, player.getPoints());
    }

    @Test
    void testSetResourceAmount() {
        int oldAmount = player.getResourceAmount(Resource.GRAIN);
        player.setResourceAmount(Resource.GRAIN,oldAmount+1);
        assertNotEquals(oldAmount, player.getResourceAmount(Resource.GRAIN));
    }

    @Test
    void testGetResourceAmount(){
        player.setResourceAmount(Resource.GRAIN, 5);
        assertEquals(5, player.getResourceAmount(Resource.GRAIN));
    }

    @Test
    void testGetTotalResourcesAmount(){
        player.setResourceAmount(Resource.GRAIN, 5);
        player.setResourceAmount(Resource.GRAIN, 1);
        player.setResourceAmount(Resource.WOOL, 1);
        assertEquals(2, player.getTotalResourcesAmount());
    }

    @Test
    void testSetStructureAmount() {
        int oldAmount = player.getStructureAmount(Structure.ROAD);
        player.setStructureAmount(Structure.ROAD);
        assertNotEquals(oldAmount, player.getResourceAmount(Resource.GRAIN));
    }
}

