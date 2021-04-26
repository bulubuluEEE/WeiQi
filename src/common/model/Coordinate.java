package common.model;

import static common.constants.Constants.CHESSBOARD_SIZE;
import static common.constants.Constants.CHESS_STATUS;

/**
 * 棋子坐标
 */
public class Coordinate {
    private  int x;
    private  int y;


    public Coordinate xAdd(int i){
        return new Coordinate(this.x + i,this.y);
    }
    public Coordinate yAdd(int i){
        return new Coordinate(this.x ,this.y + i);
    }
    //坐标偏移
    public void xPlus(int i){
        int r = this.x + i;
        if (r>=0 && r < CHESSBOARD_SIZE - 1){
            this.x = r;
        }
    }
    public void yPlus(int i){
        int r = this.y + i;
        if (r>=0 && r < CHESSBOARD_SIZE - 1){
            this.y = r;
        }
    }

    public Coordinate(int x,int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
