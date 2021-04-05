package ai.uni;


import utils.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Algorithm {
    private final String[][] cells;
    private int[][] info;
    private final int rows;
    private final int cols;

    public Algorithm(String[][] cells) {
        this.cells = cells;
        if (cells == null || cells.length == 0)
            throw new RuntimeException("AHD:: cells must have at least one row and column");
        rows = cells.length;
        cols = cells[0].length;
    }

    public List<Point> ids(Point start) {
        List<Point> res;
        for (int i = 0; i < rows + cols - 1; i++)
            if ((res = dfs(start, new int[rows][cols], i)) != null) {
                Collections.reverse(res);
                return res;
            }
        return null;
    }

    public List<Point> dfs(Point start, int[][] explored, int limit) {
        Utils.sleep(100);

        if (isGoal(start))
            return new ArrayList<>(List.of(start));

        if (limit == 0)
            return null;

        explored[start.x][start.y] = 1;
        info = explored;


        List<Point> res;
        for (var neighbor : neighbors(start))
            if (explored[neighbor.x][neighbor.y] == 0 && (res = dfs(neighbor, clone(explored), limit - 1)) != null) {
                res.add(start);
                return res;
            }

        return null;
    }

    private int[][] clone(int[][] arr) {
        var res = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++)
            System.arraycopy(arr[i], 0, res[i], 0, arr[0].length);
        return res;
    }

    private boolean isGoal(Point p) {
        return cells[p.x][p.y].toLowerCase().contains("p");
    }

    private boolean goalTest(String[][] cells, Point point) {
        return cells[point.x][point.y].equalsIgnoreCase("p");
    }

    private List<Point> neighbors(Point point) {
        var res = new ArrayList<Point>();
        point.translate(1, 0);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(-2, 0);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(1, 1);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(0, -2);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(0, 1);
        return res;
    }

    private boolean isNeighbor(Point point) {
        return point.x > -1 && point.x < cells.length &&
                point.y > -1 && point.y < cells[0].length &&
                !cells[point.x][point.y].equalsIgnoreCase("x");
    }

    public List<Point> bbfs() {

        return List.of();
    }

    public List<Point> aStar() {

        return List.of();
    }

    public String[][] getCells() {
        return cells;
    }

    public int[][] getVisited() {
        return info;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
