package common;


import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.awt.*;
import java.util.*;
import java.util.List;

import static common.constants.Constants.*;
import static common.utils.ChessBoardUtil.*;
import static common.utils.StrategyUtil.analysis;

/**
 * 递归实现围棋规则
 */
public class Recursion {

    static List<Point> link = new ArrayList<>();


    public static void main(String[] args) {
        int blackForbid = 0;
        int whiteForbid = 0;
        Random ran = new Random();

        while (true) {
            Point point = analysis();
            if (point == null){
                System.out.println((STATUS == BLACK ? "黑" : "白") + "方投降，结束！！");
            }
            int x = 0;
            int y=0;
            try {
                 x = (int)point.getX();
                 y = (int)point.getY();

            }catch (Exception e){
                e.printStackTrace();
                break;
            }

            if (putChess(x, y, STATUS)) {
                if (STATUS == BLACK){
                    blackForbid =0;
                }else {
                    whiteForbid =0;
                }
                LATEST_PUT = new Point(x,y);
                STATUS = -STATUS;
                HAND++;
                drawChessBord(x,y);
            }else {
                if (STATUS == BLACK){
                    blackForbid ++;
                }else {
                    whiteForbid ++;
                }
            }
            if (blackForbid > 20  || whiteForbid > 20){
                clearDeath(STATUS);
                drawChessBord(-1,-1);
                break;
            }


        }


    }

    /**
     * 落子，并分析该子是否有效
     *
     * @param x
     * @param y
     * @param STATUS
     * @return
     */
    static boolean putChess(int x, int y, int STATUS) {
        //我方连接体
        ArrayList<Point> myLink = new ArrayList<Point>();
        //对方连接体
        ArrayList<Point> opponentLink = new ArrayList<Point>();


        //先判断是否能吃对手
        if (opDead(x, y, STATUS, opponentLink)) {
            //考虑打劫的情况
            if (opponentLink.size() == 1) {
                if (DAJIE && DAJIE_POINT.equals(opponentLink.get(0))) {
                    System.out.print("第" + HAND + "手 " + (STATUS == BLACK ? "黑" : "白") + "方落子 想打劫失败，坐标 [" + x + "] [" + y + "]     要被吃的坐标 [" + x + "] [" + y + "] ");
                    return false;
                } else {
                    DAJIE_POINT = new Point(x, y);
                    DAJIE = true;
                }
            }else {
                DAJIE = false;
            }
            System.out.print("第" + HAND + "手 " + (STATUS == BLACK ? "黑" : "白") + "方落子，坐标 [" + x + "] [" + y + "]   ");
            //清除死子，腾出空间
            makeRoom(opponentLink,new StringBuilder((-STATUS == BLACK ? "黑" : "白") + "棋被吃，坐标 "));
            CHESS_STATUS[x][y] = STATUS;
            return true;
        }
        //判断落子后自己是否有气
        if (hasRoom(x, y, STATUS, myLink)) {
            CHESS_STATUS[x][y] = STATUS;
            DAJIE = false;
            System.out.println("第" + HAND + "手 " + (STATUS == BLACK ? "黑" : "白") + "方落子，坐标 [" + x + "] [" + y + "]");
            return true;
        }

        return false;
    }



}
