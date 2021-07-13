package ai.uni3;

import utils.SemaphoreBase;
import utils.annotation.Algorithm;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

@Algorithm(type = Algorithm.SEARCH)
public class BacktrackAlgorithm implements SemaphoreBase<String> {
    private final Map<String, Semaphore> semaphoreMap;
    private final int[][] cells;
    private final int rows;
    private final int cols;
    private final Map<Point, List<Integer>> domainMap;

    public BacktrackAlgorithm(int[][] cells) {
        this.cells = cells;
        domainMap = new HashMap<>();
        rows = cells.length;
        cols = cells[0].length;
        semaphoreMap = new HashMap<>();
        addSemaphore("solve");
    }

    public boolean solve(boolean mrv) {
        Point empty;
        while ((empty = emptyCell(mrv)) != null) {
            acquire("solve");
            if (empty.x == -1)
                return false;
            var domain = domain(empty.x, empty.y);
            if (domain.isEmpty()) {
                domainMap.put(empty, new ArrayList<>(List.of(0, 1)));
                return false;
            }
            for (var e : domain) {
                cells[empty.x][empty.y] = e;
//                forwardChecking(empty);
                if (solve(mrv))
                    return true;
                domainMap.get(empty).remove(e);
            }
            cells[empty.x][empty.y] = -1;
        }
        return true;
    }

    private boolean forwardChecking(Point point) {
        return false;
    }

    public Map<Point, List<Integer>> getDomainMap() {
        return domainMap;
    }

    private Point emptyCell(boolean mrv) {
        int size = mrv ? 3 : -1;
        Point res = null;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j] == -1) {
                    var tmp = domain(i, j).size();
                    if (tmp == 0) {
                        domainMap.put(new Point(i, j), new ArrayList<>(List.of(0, 1)));
                        return new Point(-1, -1);
                    }
                    if (mrv && size > tmp || !mrv && size < tmp) {
                        size = tmp;
                        res = new Point(i, j);
                    }
                }
        return res;
    }

    private List<Integer> domain(int i, int j) {
        var p = new Point(i, j);
        if (!domainMap.containsKey(p))
            domainMap.put(p, new ArrayList<>(List.of(0, 1)));
        var res = new ArrayList<>(domainMap.get(p));
        res.removeIf(e -> !isPossible(e, i, j));
        return Collections.unmodifiableList(res);
    }

    private boolean isPossible(int value, int row, int col) {
        final var old = cells[row][col];
        try {
            if (isValid(row - 1, col) && value == cells[row - 1][col] && isValid(row + 1, col) && value == cells[row + 1][col]) return false;
            if (isValid(row, col - 1) && value == cells[row][col - 1] && isValid(row, col + 1) && value == cells[row][col + 1]) return false;
            if (isValid(row - 2, col) && value == cells[row - 2][col] && isValid(row - 1, col) && value == cells[row - 1][col]) return false;
            if (isValid(row, col - 1) && value == cells[row][col - 1] && isValid(row, col - 2) && value == cells[row][col - 2]) return false;
            if (isValid(row + 2, col) && value == cells[row + 2][col] && isValid(row + 1, col) && value == cells[row + 1][col]) return false;
            if (isValid(row, col + 2) && value == cells[row][col + 2] && isValid(row, col + 1) && value == cells[row][col + 1]) return false;
            cells[row][col] = value;
            if (isFilled(cells[row]))
                for (int i = 0; i < rows; i++)
                    if (i != row && isFilled(cells[i]) && Arrays.equals(cells[i], cells[row]))
                        return false;
            var column = col(col);
            if (isFilled(column))
                for (int i = 0; i < cols; i++) {
                    if (i == col)
                        continue;
                    var aCol = col(i);
                    if (isFilled(aCol) && Arrays.equals(aCol, column))
                        return false;
                }
            for (var r : cells)
                if (Arrays.stream(r).filter(e -> e == 1).count() > cols / 2
                        || Arrays.stream(r).filter(e -> e == 0).count() > cols / 2)
                    return false;
            for (int i = 0; i < cols; i++) {
                column = col(i);
                if (Arrays.stream(column).filter(e -> e == 1).count() > rows / 2
                        || Arrays.stream(column).filter(e -> e == 0).count() > rows / 2)
                    return false;
            }
            return true;
        } finally {
            cells[row][col] = old;
        }
    }

    private int[] col(int col) {
        var res = new int[rows];
        for (int i = 0; i < rows; i++)
            res[i] = cells[i][col];
        return res;
    }

    private boolean isValid(int i, int j) {
        return i < rows && i >= 0 && j < cols && j >= 0;
    }

    private static boolean isFilled(int[] row) {
        return Arrays.stream(row).noneMatch(e -> e == -1);
    }

    @Override
    public Map<String, Semaphore> getSemaphoreMap() {
        return semaphoreMap;
    }
}
