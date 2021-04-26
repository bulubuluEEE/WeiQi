package common.constants;

import java.awt.*;

public class Constants {
    //9路棋盘
    public static final int CHESSBOARD_SIZE = 9;
    public static int [][] CHESS_STATUS=new int[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    public static final int BLACK = 1;
    public static final int WHITE = -1;
    //打劫
    public static boolean DAJIE = false;
    public static Point DAJIE_POINT = new Point(-1,-1);
    public static int STATUS = BLACK;
    //手数
    public static int HAND = 1;
    public static Point CURRENT_VALUE = new Point(0,0);
    public static Point LATEST_PUT = new Point(CHESSBOARD_SIZE/2,CHESSBOARD_SIZE/2);
    static {

        //绘制棋盘 坐标
        for (int i=0;i< CHESSBOARD_SIZE; i++){
            for (int j = 0; j < CHESSBOARD_SIZE; j++) {
                CHESS_STATUS[i][j]  =  0;
            }

        }
    }
}
