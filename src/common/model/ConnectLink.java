package common.model;

import common.BeginGo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static common.constants.Constants.CHESSBOARD_SIZE;
import static common.constants.Constants.CHESS_STATUS;

/**
 * 棋子连接
 */
public class ConnectLink {


    private String id;
    private List<Coordinate> connect;

    private List<Coordinate> westBorder;
    private List<Coordinate> eastBorder;
    private List<Coordinate> northBorder;
    private List<Coordinate> southBorder;


    public int connectExists(Coordinate coordinate) {
        for (int i = 0; i < this.connect.size(); i++) {
            Coordinate cn = this.connect.get(i);
            if (cn.getX() == coordinate.getX() && cn.getY() == coordinate.getY()) {
                return i;
            }
        }
        return -1;
    }

    //判断上下左右的边界是否存在coordinate这个坐标
    public int westBorderExists(Coordinate coordinate) {
        for (int i = 0; i < this.westBorder.size(); i++) {
            Coordinate west = this.westBorder.get(i);
            if (west.getX() == coordinate.getX() && west.getY() == coordinate.getY()) {
                return i;
            }
        }
        return -1;
    }

    public int eastBorderExists(Coordinate coordinate) {
        for (int i = 0; i < this.eastBorder.size(); i++) {
            Coordinate east = this.eastBorder.get(i);
            if (east.getX() == coordinate.getX() && east.getY() == coordinate.getY()) {
                return i;
            }
        }
        return -1;
    }

    public int northBorderExists(Coordinate coordinate) {
        for (int i = 0; i < this.northBorder.size(); i++) {
            Coordinate north = this.northBorder.get(i);
            if (north.getX() == coordinate.getX() && north.getY() == coordinate.getY()) {
                return i;
            }
        }
        return -1;
    }

    public int southBorderExists(Coordinate coordinate) {
        for (int i = 0; i < this.southBorder.size(); i++) {
            Coordinate south = this.southBorder.get(i);
            if (south.getX() == coordinate.getX() && south.getY() == coordinate.getY()) {
                return i;
            }
        }
        return -1;
    }

    //腾出棋盘空间
    public void removeSelf() {
        StringBuilder stringBuilder = new StringBuilder((!BeginGo.black ? "黑" : "白") + "棋被吃，坐标 X[");
        for (int r = 0; r < this.connect.size(); r++) {
            Coordinate coordinate = this.connect.get(r);
            CHESS_STATUS[coordinate.getX()][coordinate.getY()] = 0;
            stringBuilder.append(coordinate.getX() + "] Y[" + coordinate.getY() + "]——");
        }
        System.out.println(stringBuilder.toString());
    }

    public ConnectLink(Coordinate coordinate) {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
        this.connect = new ArrayList<>();
        this.westBorder = new ArrayList<>();
        this.eastBorder = new ArrayList<>();
        this.northBorder = new ArrayList<>();
        this.southBorder = new ArrayList<>();
        if (coordinate != null) {
            this.connect.add(coordinate);
            Atom atom = (Atom) coordinate;
            if (atom.getWest() != null)
                this.getWestBorder().add(atom.getWest());
            if (atom.getEast() != null)
                this.getEastBorder().add(atom.getEast());
            if (atom.getNorth() != null)
                this.getNorthBorder().add(atom.getNorth());
            if (atom.getSouth() != null)
                this.getSouthBorder().add(atom.getSouth());
        }
    }

    //按照规则，落子后，添加上下左右的边界。判断该子是否与我连接上
    public boolean increaseBorder(Atom atom) {
        Coordinate coordinate = atom;
        int westIndex, eastIndex, northIndex, southIndex;
        westIndex = this.westBorderExists(coordinate);
        if (westIndex > -1) {
            this.getWestBorder().remove(westIndex);
            if (atom.getWest() != null) {
                this.getWestBorder().add(atom.getWest());
                return true;
            }
            if (atom.getNorth() != null)
                this.getNorthBorder().add(atom.getNorth());
            if (atom.getSouth() != null)
                this.getSouthBorder().add(atom.getSouth());
        }
        eastIndex = this.eastBorderExists(coordinate);
        if (eastIndex > -1) {
            this.getEastBorder().remove(eastIndex);
            if (atom.getEast() != null) {
                this.getEastBorder().add(atom.getEast());
                return true;
            }
            if (atom.getNorth() != null)
                this.getNorthBorder().add(atom.getNorth());
            if (atom.getSouth() != null)
                this.getSouthBorder().add(atom.getSouth());
        }
        northIndex = this.northBorderExists(coordinate);
        if (northIndex > -1) {
            this.getNorthBorder().remove(northIndex);
            if (atom.getNorth() != null) {
                this.getNorthBorder().add(atom.getNorth());
                return true;
            }
            if (atom.getWest() != null)
                this.getWestBorder().add(atom.getWest());
            if (atom.getEast() != null)
                this.getEastBorder().add(atom.getEast());
        }
        southIndex = this.southBorderExists(coordinate);
        if (southIndex > -1) {
            this.getSouthBorder().remove(southIndex);
            if (atom.getSouth() != null) {
                this.getSouthBorder().add(atom.getSouth());
                return true;
            }
            if (atom.getWest() != null)
                this.getWestBorder().add(atom.getWest());
            if (atom.getEast() != null)
                this.getEastBorder().add(atom.getEast());
        }

        return false;
    }

    //另一个连接要与我组合，更新属性。
    public void unit(ConnectLink follower) {
        this.connect.addAll(follower.getConnect());
        this.westBorder.addAll(follower.getWestBorder());
        this.eastBorder.addAll(follower.getEastBorder());
        this.northBorder.addAll(follower.getNorthBorder());
        this.southBorder.addAll(follower.getSouthBorder());
    }

    //判断本连接体是否有气
    public boolean isAlive() {
        return notEmpty(this.getWestBorder()) || notEmpty(this.getEastBorder()) || notEmpty(this.getNorthBorder()) || notEmpty(this.getSouthBorder());
    }

    static boolean notEmpty(List<Coordinate> list) {
        return list != null && list.size() > 0;
    }

    public List<Coordinate> getConnect() {
        return connect;
    }

    public void setConnect(List<Coordinate> connect) {
        this.connect = connect;
    }

    public List<Coordinate> getWestBorder() {
        return westBorder;
    }

    public void setWestBorder(List<Coordinate> westBorder) {
        this.westBorder = westBorder;
    }

    public List<Coordinate> getEastBorder() {
        return eastBorder;
    }

    public void setEastBorder(List<Coordinate> eastBorder) {
        this.eastBorder = eastBorder;
    }

    public List<Coordinate> getNorthBorder() {
        return northBorder;
    }

    public void setNorthBorder(List<Coordinate> northBorder) {
        this.northBorder = northBorder;
    }

    public List<Coordinate> getSouthBorder() {
        return southBorder;
    }

    public void setSouthBorder(List<Coordinate> southBorder) {
        this.southBorder = southBorder;
    }

}
