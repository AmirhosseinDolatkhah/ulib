package ai.uni;


import algo.Algorithm;
import utils.SemaphoreBase;
import utils.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

@Algorithm(type = "Search")
public class Grid2DPathFinderAlgorithm implements SemaphoreBase<String> {
    private final String[][] cells;
    private int[][] info;
    private final int rows;
    private final int cols;
    private final HashMap<String, Semaphore> semaphoreMap;

    public Grid2DPathFinderAlgorithm(String[][] cells) {
        this.cells = cells;
        if (cells == null || cells.length == 0 || cells[0].length == 0)
            throw new RuntimeException("AHD:: cells must have at least one row and one column");
        rows = cells.length;
        cols = cells[0].length;
        semaphoreMap = new HashMap<>();

        //////
        addSemaphore("begin-dls");
        addSemaphore("begin-bfs");
    }

    public List<Point> ids(Point start) {
        List<Point> res;
        for (int i = 0; i < rows * cols; i++) {
            info = null;
            if ((res = dls(start, new int[rows][cols], i)) != null) {
                Collections.reverse(res);
                return res;
            }
        }
        return null;
    }

    public List<Point> dls(Point start, int[][] explored, int limit) {
        explored[start.x][start.y] = 1;
        info = explored;

        acquire("begin-dls");

        if (isGoal(start))
            return new ArrayList<>(List.of(start));

        if (limit == 0)
            return null;

        List<Point> res;
        var neighbors = neighbors(start);
        neighbors.stream().filter(e -> explored[e.x][e.y] <= 0).forEach(e -> info[e.x][e.y] = -1);
        for (var neighbor : neighbors)
            if (explored[neighbor.x][neighbor.y] <= 0 && (res = dls(neighbor, clone(explored), limit - 1)) != null) {
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

    private List<Point> neighbors(Point point) {
        var res = new ArrayList<Point>();
        point.translate(0, 1);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(1, -1);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(-1, -1);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(-1, 1);
        if (isNeighbor(point))
            res.add(new Point(point));
        point.translate(1, 0);
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

    public int[][] getInfo() {
        return info == null ? info = new int[rows][cols] : info;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    @Override
    public Map<String, Semaphore> getSemaphoreMap() {
        return semaphoreMap;
    }
}
