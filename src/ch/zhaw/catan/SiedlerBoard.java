package ch.zhaw.catan;

import ch.zhaw.catan.Config.Land;
import ch.zhaw.hexboard.HexBoard;

import java.util.List;
import java.awt.Point;


/**
 * The class SiedlerBoard extends the HexBoard class and
 *
 *
 * @version 12-05-2019
 */
public class SiedlerBoard extends HexBoard<Land, String, String, String> {

    public static final String NO_SUCH_EDGE = "no_edge";

    public static List<Point> getAdjacentCorners(Point position) {
        return HexBoard.getAdjacentCorners(position);
    }

    /**
     * This method tries to retrieve the edge between point p1 and p2.
     * -    If there is an edge, its data will be returned (which can be null).
     * -    If there is no such edge, the constant NO_SUCH_EDGE will be returned instead.
     *
     * @param p1 starting point of the edge
     * @param p2 end point of the edge
     * @return String Data of the edge between p1 and p2, if there is an edge.
     * Constant NO_SUCH_EDGE otherwise
     */
    @Override
    public String getEdge(Point p1, Point p2) {
        if (super.hasEdge(p1, p2)) {
            return super.getEdge(p1, p2);
        }

        return NO_SUCH_EDGE;
    }

    /**
     * Put a road to the hex board and set the edges
     * @param roadStart start of the road
     * @param roadEnd   end of the road
     * @param data      the String which has to be given and set to the hex board
     */
    public void putRoad(Point roadStart, Point roadEnd, String data){
        setEdge(roadStart, roadEnd, data);
    }

    /**
     * Put a settlement to the hex board
     * @param position  the position where the settlement has to be put
     * @param data      the String which has to be given and set to the hex board
     */
    public void putSettlement(Point position, String data){
        setCorner(position, data);
    }

    /**
     * Put a city to the hex board
     * @param position  the position where the settlement has to be put
     * @param data      the String which has to be given and set to the hex board
     */
    public void putCity(Point position, String data) {
        setCorner(position, data);
    }


}

