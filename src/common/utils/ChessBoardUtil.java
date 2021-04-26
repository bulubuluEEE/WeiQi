package common.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static common.constants.Constants.*;

public class ChessBoardUtil {

    /**
     * 判断是否有气，递归判断周围是否有空间
     *
     * @param x
     * @param y
     * @param status
     * @param link
     * @param tempBoard
     * @return
     */
    public static boolean hasRoom(int x, int y, int status, ArrayList<Point> link,int[][] tempBoard) {
        if (tempBoard == null){
            tempBoard = CHESS_STATUS;
        }
        Point point = new Point(x, y);
        link.add(point);
        //先判断自己的周围有没有气
        if (x + 1 < CHESSBOARD_SIZE && tempBoard[x + 1][y] == 0) {
            if (!linkContains(link, x + 1, y))
                return true;
        }
        if (y + 1 < CHESSBOARD_SIZE && tempBoard[x][y + 1] == 0) {
            if (!linkContains(link, x, y + 1))
                return true;
        }
        if (x - 1 >= 0 && tempBoard[x - 1][y] == 0) {
            if (!linkContains(link, x - 1, y))
                return true;
        }
        if (y - 1 >= 0 && tempBoard[x][y - 1] == 0) {
            if (!linkContains(link, x, y - 1))
                return true;
        }
        //如果自己的周围没有气，则递归问周围的同方棋子有没有气
        boolean left, right, top, bottom;
        left = right = top = bottom = false;
        if (x + 1 < CHESSBOARD_SIZE && tempBoard[x + 1][y] == status) {
            //判断过的子不再判断
            if (!linkContains(link, x + 1, y)) {
                if (hasRoom(x + 1, y, status, link))
                    return true;
            }
        }
        if (y + 1 < CHESSBOARD_SIZE && tempBoard[x][y + 1] == status) {
            if (!linkContains(link, x, y + 1)) {
                if (hasRoom(x, y + 1, status, link))
                    return true;
            }
        }
        if (x - 1 >= 0 && tempBoard[x - 1][y] == status) {
            if (!linkContains(link, x - 1, y)) {
                if (hasRoom(x - 1, y, status, link))
                    return true;
            }
        }
        if (y - 1 >= 0 && tempBoard[x][y - 1] == status) {
            if (!linkContains(link, x, y - 1)) {
                if (hasRoom(x, y - 1, status, link))
                    return true;
            }
        }
        return false;

    }
    public static boolean hasRoom(int x, int y, int status, ArrayList<Point> link) {
        return  hasRoom( x,  y, status, link,null);
    }

    /**
     * 判断对手是否有死亡的连接体
     *
     * @param x
     * @param y
     * @param status（我方）
     * @param link
     * @param tempBoard
     * @return
     */
    public static boolean opDead(int x, int y, int status, ArrayList<Point> link,int[][] tempBoard) {
        if (tempBoard == null){
            tempBoard = CHESS_STATUS;
        }
        ArrayList<Point> linkLeft = new ArrayList<>();
        ArrayList<Point> linkRight = new ArrayList<>();
        ArrayList<Point> linkTop = new ArrayList<>();
        ArrayList<Point> linkBottom = new ArrayList<>();
        //预先落一手虚拟棋，后面再恢复
        tempBoard[x][y] = status;
        //递归判断四个方向的对手是否有气
        boolean left, right, top, bottom;
        left = right = top = bottom = false;
        if (x + 1 < CHESSBOARD_SIZE && tempBoard[x + 1][y] == -status) {
            //将死亡的连接体记录在link里
            right = !hasRoom(x + 1, y, -status, linkRight,tempBoard);
            if (right) {
                link.removeAll(linkRight);
                link.addAll(linkRight);
            }
        }
        if (y + 1 < CHESSBOARD_SIZE && tempBoard[x][y + 1] == -status) {
            top = !hasRoom(x, y + 1, -status, linkTop,tempBoard);
            if (top) {
                link.removeAll(linkTop);
                link.addAll(linkTop);
            }
        }
        if (x - 1 >= 0 && tempBoard[x - 1][y] == -status) {
            left = !hasRoom(x - 1, y, -status, linkLeft,tempBoard);
            if (left) {
                link.removeAll(linkLeft);
                link.addAll(linkLeft);
            }
        }
        if (y - 1 >= 0 && tempBoard[x][y - 1] == -status) {
            bottom = !hasRoom(x, y - 1, -status, linkBottom,tempBoard);
            if (bottom) {
                link.removeAll(linkBottom);
                link.addAll(linkBottom);
            }
        }
        //还原棋盘
        tempBoard[x][y] = 0;
        return left || right || top || bottom;
    }


    /**
     * 判断对手是否有死亡的连接体
     *
     * @param x
     * @param y
     * @param status（我方）
     * @param link
     * @return
     */
    public static boolean opDead(int x, int y, int status, ArrayList<Point> link) {
        return opDead(x,y,status,link,null);
    }


    /**
     * 判断连接体中是否存在x,y坐标
     *
     * @param link
     * @param x
     * @param y
     * @return
     */
    static boolean linkContains(ArrayList<Point> link, int x, int y) {
        for (Point p : link) {
            if (p.getX() == x && p.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 绘制棋棋谱
     * @param newOneX
     * @param newOneY
     */
    public static void drawChessBord(int newOneX, int newOneY) {
        for (int i = 0; i < CHESSBOARD_SIZE; i++) {
            for (int j = 0; j < CHESSBOARD_SIZE; j++) {
                String s = CHESS_STATUS[i][j] == BLACK ? "⚫ "
                        : CHESS_STATUS[i][j] == WHITE ? "⚪ "  :"✛ ";
                if (newOneX == i && newOneY == j) {
                    s = CHESS_STATUS[i][j] == BLACK ? "◕ " : CHESS_STATUS[i][j] == WHITE ? "◔ " : "✛ ";
                }
                System.out.print(s);
            }
            System.out.println();
        }
        System.out.println("\n");
    }

    /**
     * 盘中腾出死子棋盘空间
     * @param link
     * @param stringBuilder
     */
    public static void makeRoom(ArrayList<Point> link, StringBuilder stringBuilder) {
        //腾出棋盘空间
        for (Point death : link) {
            CHESS_STATUS[(int) death.getX()][(int) death.getY()] = 0;
            stringBuilder.append("[" + (int) death.getX() + "] [" + (int) death.getY() + "]  ——  ");
        }
        System.out.println(stringBuilder.toString());
    }
    /**
     * 终局清除死子
     * @param status（被清除的一方）
     */
    public static void clearDeath(int status) {
        ArrayList<Point> link = new ArrayList<>();
        for (int i = 0; i < CHESSBOARD_SIZE; i++) {
            for (int j = 0; j < CHESSBOARD_SIZE; j++) {
                if (CHESS_STATUS[i][j] == 0){
                    opDead( i, j, -status, link);
                }

            }
        }
        makeRoom(link,new StringBuilder("开始清除死子: 坐标 "));
    }

    /**
     * 从link连接体中移除坐标
     *
     * @param link
     * @param x
     * @param y
     */
    static void linkRemove(List<Point> link, int x, int y) {
        int i = 0;
        for (int j = 0; j < link.size(); j++)
            if (link.get(i).getX() == x && link.get(i).getY() == y) {
                i = j;
            }
        link.remove(i);
    }
}
