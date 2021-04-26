package common.model;

import static common.constants.Constants.CHESSBOARD_SIZE;
import static common.constants.Constants.CHESS_STATUS;

/**
 * 棋子坐标
 */
public class Atom extends Coordinate{
    private  Coordinate west;
    private  Coordinate east;
    private  Coordinate north;
    private  Coordinate south;

    public Atom(int x, int y) {
        super(x, y);
        boolean increaseWest = (x >= 1 ) && (CHESS_STATUS[x - 1][y] == 0)
                ,increaseEast = (x < CHESSBOARD_SIZE - 1 ) && (CHESS_STATUS[x + 1][y] == 0)
                ,increaseNorth = (y < CHESSBOARD_SIZE - 1 ) && (CHESS_STATUS[x][y + 1] == 0)
                ,increaseSouth = (y >= 1 ) && (CHESS_STATUS[x][y - 1] == 0);
        if (increaseEast)
            east = super.xAdd(1);
        if (increaseWest)
            west = super.xAdd(-1);
        if (increaseNorth)
            north = super.yAdd(1);
        if (increaseSouth)
            south = super.yAdd(-1);
    }

    public Coordinate getWest() {
        return west;
    }

    public Coordinate getEast() {
        return east;
    }

    public Coordinate getNorth() {
        return north;
    }

    public Coordinate getSouth() {
        return south;
    }
}
