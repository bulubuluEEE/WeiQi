package common;

import common.constants.Constants;
import common.model.Atom;
import common.model.ConnectLink;
import common.model.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static common.constants.Constants.CHESSBOARD_SIZE;
import static common.constants.Constants.CHESS_STATUS;

public class BeginGo  {
    static ArrayList<ConnectLink> blackConnects = new ArrayList<ConnectLink>();
    static ArrayList<ConnectLink> whiteConnects = new ArrayList<ConnectLink>();

    //是否处于打劫的情况
    static boolean dajie = false;
    //手数
    static int hand = 1;
    public static boolean black = true;

    public static void main(String[] args){
        Random ran = new Random();
        while (true) {
            int x = ran.nextInt(CHESSBOARD_SIZE);
            int y = ran.nextInt(CHESSBOARD_SIZE);
            if (putChess(x, y, black)) {
                black = !black;
                hand++;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    //落子，并判断改子是否有效
    public static boolean putChess(int x,int y,boolean black) {
        //判断该位置下是否已落过子
        if (CHESS_STATUS[x][y] == 0) {
            ArrayList<ConnectLink> myConnects = blackConnects;
            ArrayList<ConnectLink> enemyConnects = whiteConnects;
            if (!black) {
                myConnects = whiteConnects;
                enemyConnects = blackConnects;
            }
            Atom atom = new Atom(x, y);
            ArrayList<ConnectLink> enemyTemp = new ArrayList<ConnectLink>();
            enemyTemp.addAll(enemyConnects);
            //取出对手所有的连接体
            for (int i = 0; i < enemyTemp.size(); i++) {
                ConnectLink enemy = enemyTemp.get(i);
                //判断是否下到对手前后左右的边界
                int west, east, north, south;
                west = enemy.westBorderExists(atom);
                if (west > -1) {
                    enemy.getWestBorder().remove(west);
                }
                east = enemy.eastBorderExists(atom);
                if (east > -1) {
                    enemy.getEastBorder().remove(east);
                }
                north = enemy.northBorderExists(atom);
                if (north > -1) {
                    enemy.getNorthBorder().remove(north);
                }
                south = enemy.southBorderExists(atom);
                if (south > -1) {
                    enemy.getSouthBorder().remove(south);
                }

                //如果对手被吃了
                if (!enemy.isAlive()) {
                    if (dajie && enemy.getConnect().size() == 1) {
                        return false;
                    }
                    System.out.println("第"+hand+"手 " + (black ? "黑" : "白") + "方落子，坐标 X[" + x + "] Y[" + y + "]");
                    //腾出棋盘的空间
                    enemyConnects.get(i).removeSelf();
                    //吃完子之后更新我方边界
                    updateMyBorderAfterVict(enemy,myConnects);
                    //将这块连接体直接去除
                    enemyConnects.remove(i);
                    CHESS_STATUS[x][y] = 1;
                    dajie = false;
                    return true;
                }

            }

            ArrayList<ConnectLink> myTemp = new ArrayList<ConnectLink>();
            myTemp.addAll(myConnects);
            ConnectLink newUnit = new ConnectLink(null);
            boolean combine = false;
            //取出我方所有连接体
            for (int i = 0; i < myTemp.size(); i++) {
                ConnectLink my = myTemp.get(i);
                //将所有能链在一起的连接体结合起来
                if (my.increaseBorder(atom)){
                    newUnit.unit(my);
                    myTemp.remove(my);
                    combine = true;
                }
            }
            if (combine) {
                //去除旧的连接体，用新的结合体替换
                if (newUnit.isAlive()) {
                    newUnit.getConnect().add(atom);
                    myTemp.add(newUnit);
                    System.out.println("第" + hand + "手 " + (black ? "黑" : "白") + "方落子，坐标 X[" + x + "] Y[" + y + "]");
                    CHESS_STATUS[x][y] = 1;
                    //更新对手的边界（enemyConnects里包含边界）
                    enemyConnects = enemyTemp;
                    //更新自己的边界（myConnects里包含边界）
                    myConnects = myTemp;
                    dajie = false;
                    return true;

                }
            }

            //如果未和任何子有连接，是孤独的，则自己开创一个连接
            ConnectLink connectLink = new ConnectLink(atom);
            if (connectLink.isAlive()){
                myConnects.add(connectLink);
                System.out.println("第"+hand+"手 " + (black ? "黑" : "白") + "方落子，坐标 X[" + x + "] Y[" + y + "]");
                CHESS_STATUS[x][y] = 1;
                //更新对手的边界（enemyConnects里包含边界）
                enemyConnects = enemyTemp;
                //更新自己的边界（myConnects里包含边界）
                myConnects = myTemp;
                dajie = false;
                return true;
            }
        }
        return false;
    }

    /**
     * 吃完对方子之后更新自己的边界
     * @param enemyLink
     * @param myConnects
     */
    private static void updateMyBorderAfterVict(ConnectLink enemyLink, ArrayList<ConnectLink> myConnects) {
        for (int i = 0; i < myConnects.size(); i++) {
            ConnectLink myCK = myConnects.get(i);
            for (int j = 0; j < enemyLink.getConnect().size(); j++) {
                Coordinate enemyCd = enemyLink.getConnect().get(j);
                if (myCK.connectExists(enemyCd.xAdd(-1)) > -1){
                    myCK.getEastBorder().add(enemyCd);
                }
                if (myCK.connectExists(enemyCd.xAdd(1)) > -1){
                    myCK.getWestBorder().add(enemyCd);
                }
                if (myCK.connectExists(enemyCd.yAdd(-1)) > -1){
                    myCK.getNorthBorder().add(enemyCd);
                }
                if (myCK.connectExists(enemyCd.yAdd(1)) > -1){
                    myCK.getSouthBorder().add(enemyCd);
                }

            }
            
        }
    }

}
