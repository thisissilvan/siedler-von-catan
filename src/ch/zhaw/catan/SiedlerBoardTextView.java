package ch.zhaw.catan;

import ch.zhaw.catan.Config.Land;
import ch.zhaw.hexboard.HexBoardTextView;

/**
 * The class SiedlerBoardTextView extends the class HexBoardTextView.
 * It adds coordinates to the HexBoard.
 *
 * @author Kunsang Kündetsang
 * @version 12-05-2019
 */
public class SiedlerBoardTextView extends HexBoardTextView<Land, String, String, String> {

    public SiedlerBoardTextView(SiedlerBoard board) {
        super(board);
    }

    private String boardWithXCoordinates() {
        StringBuilder coordinates= new StringBuilder();
        coordinates.append("\t");
        for (int i=0; i<15;i++){
            if(i<10){
                coordinates.append(" ").append(i).append("       ");
            }else{
                coordinates.append(" ").append(i).append("      ");
            }
        }
    return coordinates+System.lineSeparator();
    }

    private String boardWithYCoordinates(String oldBoard) {
        String[] lines = oldBoard.split(System.lineSeparator());
        StringBuilder board= new StringBuilder();
        int coordinate=0;
        int counter=0;
        for (String line : lines) {
            if(counter%6==0){
                board.append(coordinate).append("\t").append(line).append(System.lineSeparator());
                coordinate++;
                counter++;

            }else if(counter%6==1||counter%6==2||counter%6==5){
                board.append("\t").append(line).append(System.lineSeparator());
                counter++;
            }else{
                board.append(coordinate).append("\t").append(line).append(System.lineSeparator());
                coordinate++;
                counter++;
            }
        }
        return board.toString();
    }

    /**
     * Creates a String with the given coordinates
     * @return  the String who has been created
     */
    public String toStringWithCoordinates(){
        String newBoard;
        newBoard= boardWithXCoordinates();
        newBoard+=boardWithYCoordinates(toString());
        return newBoard;
    }
}
