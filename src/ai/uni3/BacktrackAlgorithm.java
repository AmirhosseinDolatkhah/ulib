package ai.uni3;

import utils.annotation.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Algorithm(type = Algorithm.SEARCH)
public class BacktrackAlgorithm {
    private final int[][] cells;
    private final int rows;
    private final int cols;

    public BacktrackAlgorithm(int[][] cells) {
        this.cells = cells;
        rows = cells.length;
        cols = cells[0].length;
    }

    private boolean solve() {

        return false;
    }

    private List<Integer> domain(int i, int j) {
        var res = new ArrayList<>(List.of(0, 1));
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
        return Arrays.stream(row).anyMatch(e -> e == -1);
    }
}

