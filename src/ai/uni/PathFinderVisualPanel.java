package ai.uni;

import utils.Utils;
import utils.supplier.StringSupplier;
import visualization.canvas.CoordinatedScreen;
import visualization.shapes.shape2d.grid2d.GridPlain2D;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class PathFinderVisualPanel extends GridPlain2D {

    private final String[][] cells;
    private final Algorithm algorithm;

    public PathFinderVisualPanel(CoordinatedScreen cs, String[][] cells) {
        super(cs, cells.length, cells[0].length);
        this.cells = cells;
        var rows = cells.length;
        var cols = cells[0].length;
        algorithm = new Algorithm(cells);
        Utils.checkTimePerform(e -> algorithm.ids(new Point()), false, "ids");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                var tile = getTile(i, j);
                int finalI = i;
                int finalJ = j;
                tile.setColorFunc(() -> algorithm.getVisited()[finalI][finalJ] == 0 ? Color.BLUE : colorOf(cells[finalI][finalJ]));
                tile.setTextFunction(new StringSupplier() {
                    @Override
                    public String getText() {
                        return textOf(cells[finalI][finalJ]);
                    }

                    @Override
                    public Color getColor() {
                        return Color.YELLOW.darker();
                    }

                    @Override
                    public Font getFont() {
                        return new Font(Font.SANS_SERIF, Font.BOLD, 20);
                    }
                });
                tile.setVisible(() -> visibilityOf(cells[finalI][finalJ]));
            }
    }

    public PathFinderVisualPanel(CoordinatedScreen cs, String path) throws FileNotFoundException {
        this(cs, loadFromFile(path));
    }

    private Color colorOf(String cell) {
        final var brown = new Color(50, 15, 5);
        final var pink = new Color(255, 0, 80).darker();
        final var darkBrown = new Color(130, 25, 0);
        return switch (cell.toLowerCase().charAt(0)) {
            case '1' -> darkBrown;
            case '2' -> pink;
            case 'x' -> brown;
            default -> Color.WHITE;
        };
    }

    private String textOf(String cell) {
//        return switch (cell.toLowerCase()) {
//            case "1b", "2b" -> "B";
//            case "1r", "2r" -> "R";
//            case "x" -> "X";
//            case "1p", "2p" -> "P";
//            case "f" -> "F";
//            default -> cell;
//        };
        return cell.toLowerCase().startsWith("d") ? cell.substring(1) : "";
    }

    private boolean visibilityOf(String cell) {
        return true;
    }

    private static boolean goalTest(String[][] cells, Point point) {
        return cells[point.x][point.y].equalsIgnoreCase("p");
    }

    private static List<Point> neighbors(String[][] cells, Point point) {
        var res = new ArrayList<Point>();
        point.translate(1, 0);
        if (isNeighbor(cells, point))
            res.add(new Point(point));
        point.translate(-2, 0);
        if (isNeighbor(cells, point))
            res.add(new Point(point));
        point.translate(1, 1);
        if (isNeighbor(cells, point))
            res.add(new Point(point));
        point.translate(0, -2);
        if (isNeighbor(cells, point))
            res.add(new Point(point));
        point.translate(0, 1);
        return res;
    }

    private static boolean isNeighbor(String[][] cells, Point point) {
        return point.x > -1 && point.x < cells.length &&
                point.y > -1 && point.y < cells[0].length &&
                !cells[point.x][point.y].equalsIgnoreCase("x");
    }

    public static String[][] loadFromFile(String path) throws FileNotFoundException {
        var scanner = new Scanner(new File(path));
        var dim = Arrays.stream(scanner.nextLine().trim().split("\t")).mapToInt(Integer::parseInt).toArray();
        var res = new String[dim[0]][dim[1]];
        int counter = 0;
        while (scanner.hasNextLine())
            res[counter++] = scanner.nextLine().trim().split("\t");
        return res;
    }
}
