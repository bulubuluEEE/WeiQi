package common.utils;

import java.awt.*;
import java.util.*;
import java.util.List;

import static common.constants.Constants.*;
import static common.utils.ChessBoardUtil.*;

public class StrategyUtil {
    //几步以下 放置在内圈
    static int innerHand = 3;
    //往后预判几手
    static int deepMind = 1;
    public static Point analysis() {
        int preHand = 0;
        ArrayList<Point> rooms = new ArrayList<>();
        //獲取可下的棋盤空間
        for (int i = 0; i < CHESSBOARD_SIZE; i++) {
            for (int j = 0; j < CHESSBOARD_SIZE; j++) {
                if (CHESS_STATUS[i][j] == 0) {
                    rooms.add(new Point(i, j));
                }
            }

        }

        ArrayList<Point> suggest = new ArrayList<>();
        suggest.addAll(rooms);
        if (HAND < 30) {
            //遍历去除外面两圈的点
            Iterator<Point> s = suggest.iterator();
            while (s.hasNext()) {
                Point p = s.next();
                //每人的前面两步 建议走内圈
                if (HAND < 2*innerHand + 1) {
                    if (p.getX() < 2 || p.getY() < 2 || p.getX() >= CHESSBOARD_SIZE - 2 || p.getY() >= CHESSBOARD_SIZE - 2) {
                        s.remove();
                    }
                }
                //建议与上次对手下的棋距离4以内
                else if (Math.abs(p.getX() - LATEST_PUT.getX()) > 4 || Math.abs(p.getY() - LATEST_PUT.getY()) > 4) {
                    s.remove();
                }
            }
        }
        //每个点位落子后的成绩单。
        Map<Point, Integer> report = new HashMap<>();
        //挑選的棋子座標
        Point pick = null;
        Point bestValue = myCalculate(CHESS_STATUS, STATUS, rooms);
        for (Point tryChess : suggest) {
            if (pick == null) {
                pick = tryChess;
            }
            Point tempValue = tryPut(tryChess, CHESS_STATUS, rooms, STATUS, DAJIE, DAJIE_POINT, LATEST_PUT, preHand);
            if (tempValue == null)
                continue;
            if (bestValue == null) {
                bestValue = tempValue;
                continue;
            }
            if ((tempValue.getX() - tempValue.getY()) >= bestValue.getX() - bestValue.getY()) {
                bestValue = tempValue;
                pick = tryChess;
            }

        }

        return pick;
    }


    /**
     * 递归预演轮流下子
     *
     * @param put
     * @param chessStatus
     * @param rooms
     * @param currentStat
     * @param dajie
     * @param dajiePoint
     * @param pre_Hand
     * @return
     */
    static Point tryPut(Point put, int[][] chessStatus, ArrayList<Point> rooms, int currentStat, Boolean dajie, Point dajiePoint, Point latestPut, int pre_Hand) {
        int[][] currentBoard = new int[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
        for (int i = 0; i < CHESSBOARD_SIZE; i++) {
            currentBoard[i] = chessStatus[i].clone();
        }
        Boolean currentDajie = new Boolean(dajie);
        Point currentDajiePoint = new Point(dajiePoint);
        Point currentLatestPut = latestPut;
        ArrayList<Point> currentRooms = new ArrayList<>();
        currentRooms.addAll(rooms);
        int currentPreHand = pre_Hand;
        int currentHand = HAND + pre_Hand;
        List<Point> suggest = new ArrayList<>();
        suggest.addAll(rooms);
        if (currentHand < 30) {
            //遍历去除外面两圈的点
            Iterator<Point> s = suggest.iterator();
            while (s.hasNext()) {
                Point p = s.next();
                //每人的开始两步 建议走内圈
                if (currentHand <  2*innerHand + 1) {
                    if (p.getX() < 2 || p.getY() < 2 || p.getX() >= CHESSBOARD_SIZE - 2 || p.getY() >= CHESSBOARD_SIZE - 2) {
                        s.remove();
                    }
                }
                //建议与上次对手下的棋距离四以内
                else if (Math.abs(p.getX() - currentLatestPut.getX()) > 4 || Math.abs(p.getY() - currentLatestPut.getY()) > 4) {
                    s.remove();
                }
            }
        }
        //我方成绩单
        Map<Point, Integer> myReport = new HashMap<>();
        //对方成绩单
        Map<Point, Integer> opReport = new HashMap<>();
        boolean enableMove = false;
        boolean enemyMoveable = false;
        int x = (int) put.getX();
        int y = (int) put.getY();
        Point bestValue = null;
        if (putChess(x, y, currentStat, currentBoard, currentDajie, currentDajiePoint)) {
            enableMove = true;
            //先把各方势力值计算出来，最好成绩缺省值为计算后的成绩
            bestValue = myCalculate(currentBoard, currentStat, currentRooms);

            //雙方各往下預判几手
            if (currentPreHand == deepMind * 2) {
                return bestValue;
            }
            currentPreHand++;
            currentRooms.remove(put);

            for (Point tryChess : suggest) {
                int[][] tempBoard = new int[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
                for (int i = 0; i < CHESSBOARD_SIZE; i++) {
                    tempBoard[i] = currentBoard[i].clone();
                }
                Boolean tempDajie = new Boolean(currentDajie);
                Point tempDajiePoint = new Point(currentDajiePoint);
                ArrayList<Point> tempRooms = new ArrayList<>();
                tempRooms.addAll(currentRooms);
                Point tempValue = tryPut(tryChess, tempBoard, tempRooms, -currentStat, tempDajie, tempDajiePoint, put, currentPreHand);
                if (tempValue == null) {
                    continue;
                }
                //由于下的是对手棋，X,Y反转
                if ((tempValue.getY() - tempValue.getX()) >= bestValue.getY() - bestValue.getX()) {
                    bestValue =  new Point((int)tempValue.getY(),(int)tempValue.getX());
                }

            }
        }

        return bestValue;

    }


    /**
     * 落子，并分析该子是否有效
     *
     * @param x
     * @param y
     * @param status
     * @param currentDajiePoint
     * @return
     */
    static boolean putChess(int x, int y, int status, int[][] currentBoard, Boolean dajie, Point currentDajiePoint) {
        //我方连接体
        ArrayList<Point> myLink = new ArrayList<Point>();
        //对方连接体
        ArrayList<Point> opponentLink = new ArrayList<Point>();


        //先判断是否能吃对手
        if (opDead(x, y, status, opponentLink, currentBoard)) {
            //考虑打劫的情况
            if (opponentLink.size() == 1) {
                if (dajie && currentDajiePoint.equals(opponentLink.get(0))) {
                    return false;
                } else {
                    currentDajiePoint = new Point(x, y);
                    dajie = true;
                }
            } else {
                dajie = false;
            }
            //腾出棋盘空间
            for (Point death : opponentLink) {
                currentBoard[(int) death.getX()][(int) death.getY()] = 0;
            }
            currentBoard[x][y] = status;
            return true;
        }
        //判断落子后自己是否有气
        if (hasRoom(x, y, status, myLink, currentBoard)) {
            currentBoard[x][y] = status;
            dajie = false;
            return true;
        }
        return false;

    }

    //局面 统计（有效边界值为1，私有空间值为4。允许重复统计）
    static Point myCalculate(int[][] tempStatus, int myStatus, ArrayList<Point> rooms) {
        //对手脏链
        ArrayList<Point> enemySmear = new ArrayList<>();
        //我方脏链
        ArrayList<Point> mySmear = new ArrayList<>();
        ArrayList<Point> currentRooms = rooms;
        ArrayList<Point> enemyLink = new ArrayList<>();
        ArrayList<Point> link = new ArrayList<>();
        int myBorderSize = 0;
        int enemyBorderSize = 0;
        for (Point poit : rooms) {
            int x = (int) poit.getX();
            int y = (int) poit.getY();
            Point left = new Point(x - 1, y);
            Point right = new Point(x + 1, y);
            Point bottom = new Point(x, y - 1);
            Point top = new Point(x, y + 1);
            if (x + 1 < CHESSBOARD_SIZE) {
                if (tempStatus[x + 1][y] == -myStatus) {
                    enemyBorderSize++;
                }
                if (tempStatus[x + 1][y] == myStatus) {
                    myBorderSize++;
                }
            }
            if (y + 1 < CHESSBOARD_SIZE) {
                if (tempStatus[x][y + 1] == -myStatus) {
                    enemyBorderSize++;
                }
                if (tempStatus[x][y + 1] == myStatus) {
                    myBorderSize++;
                }
            }
            if (x - 1 >= 0) {
                if (tempStatus[x - 1][y] == -myStatus) {
                    enemyBorderSize++;
                }
                if (tempStatus[x - 1][y] == myStatus) {
                    myBorderSize++;
                }
            }
            if (y - 1 >= 0) {
                if (tempStatus[x][y - 1] == -myStatus) {
                    enemyBorderSize++;
                }
                if (tempStatus[x][y - 1] == myStatus) {
                    myBorderSize++;
                }
            }

        }
        Point roomValue = new Point(0, 0);
        if (rooms.size() < 50) {
            roomValue = roomResult(tempStatus, myStatus, rooms);
        }


        int myRoom = (int) roomValue.getX();
        int enemyRoom = (int) roomValue.getY();
        //私有空间值为4 ，有效边界值为1
        int myValue = myRoom * 4 + myBorderSize;
        int enemyValue = enemyRoom * 4 + enemyBorderSize;
        //把我和对方的统计值放在Point类型的X，Y中
        Point value = new Point(myValue, enemyValue);
        return value;
    }


    static Point roomResult(int[][] tempStatus, int myStatus, ArrayList<Point> rooms) {

        ArrayList<Point> myRoom = new ArrayList<>();
        ArrayList<Point> enemyRoom = new ArrayList<>();

        int myBorderSize = 0;
        int enemyBorderSize = 0;
        for (Point poit : rooms) {
            if (!myRoom.contains(poit)) {
                ArrayList<Point> myTempRoom = new ArrayList<>();
                findRoomBorder(tempStatus, myStatus, poit, myTempRoom);
                myRoom.removeAll(myTempRoom);
                myRoom.addAll(myTempRoom);
            }
            if (!enemyRoom.contains(poit)) {
                ArrayList<Point> enemyTempRoom = new ArrayList<>();
                findRoomBorder(tempStatus, -myStatus, poit, enemyTempRoom);
                enemyRoom.removeAll(enemyTempRoom);
                enemyRoom.addAll(enemyRoom);
            }
        }
        Point roomValue = new Point(myRoom.size(), enemyRoom.size());
        return roomValue;
    }

    static boolean findRoomBorder(int[][] tempStatus, int myStatus, Point poit, ArrayList<Point> myRoom) {

        int x = (int) poit.getX();
        int y = (int) poit.getY();
        Point left = new Point(x - 1, y);
        Point right = new Point(x + 1, y);
        Point bottom = new Point(x, y - 1);
        Point top = new Point(x, y + 1);
        myRoom.add(poit);
        if (x - 1 >= 0) {
            if (tempStatus[x - 1][y] == 0) {
                if (!myRoom.contains(left) && !findRoomBorder(tempStatus, myStatus, left, myRoom)) {
                    myRoom.clear();
                    return false;
                }
            }
            if (tempStatus[x - 1][y] == -myStatus) {
                myRoom.clear();
                return false;
            }
        }
        if (y - 1 >= 0) {
            if (tempStatus[x][y - 1] == 0) {
                if (!myRoom.contains(bottom) && !findRoomBorder(tempStatus, myStatus, bottom, myRoom)) {
                    myRoom.clear();
                    return false;
                }
            }
            if (tempStatus[x][y - 1] == -myStatus) {
                myRoom.clear();
                return false;
            }
        }
        if (y + 1 < CHESSBOARD_SIZE) {
            if (tempStatus[x][y + 1] == 0) {
                if (!myRoom.contains(top) && !findRoomBorder(tempStatus, myStatus, top, myRoom)) {
                    myRoom.clear();
                    return false;
                }
            }
            if (tempStatus[x][y + 1] == -myStatus) {
                myRoom.clear();
                return false;
            }
        }
        if (x + 1 < CHESSBOARD_SIZE) {
            if (tempStatus[x + 1][y] == 0) {
                if (!myRoom.contains(right) && !findRoomBorder(tempStatus, myStatus, right, myRoom)) {
                    myRoom.clear();
                    return false;
                }
            }
            if (tempStatus[x + 1][y] == -myStatus) {
                myRoom.clear();
                return false;
            }
        }


        return true;
    }
}
