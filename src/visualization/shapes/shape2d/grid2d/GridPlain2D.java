package visualization.shapes.shape2d.grid2d;

import jmath.datatypes.tuples.Point2D;
import utils.Utils;
import visualization.canvas.CoordinatedScreen;
import visualization.canvas.Render;

import java.awt.*;
import java.util.Arrays;

public class GridPlain2D implements Render {
    private boolean isVisible;
    private boolean drawGrid;
    private float gridThickness;
    private Color gridColor;
    private double tilesWidth;
    private double tilesHeight;
    private final int numOfRows;
    private final int numOfCols;
    private final Tile2D[][] girdTiles;
    private final Point2D pos;
    private CoordinatedScreen cs;

    public GridPlain2D(CoordinatedScreen cs, int numOfRows, int numOfCols, Point2D pos) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
        tilesWidth = 0.5;
        tilesHeight = 0.5;
        isVisible = true;
        drawGrid = true;
        gridThickness = 1f;
        gridColor = Color.GRAY;
        this.cs = cs;
        this.pos = pos;
        girdTiles = new Tile2D[numOfRows][numOfCols];
        for (int i = numOfRows-1; i >= 0; i--)
            for (int j = 0; j < numOfCols; j++) {
                int finalI = i;
                int finalJ = j+1;
                girdTiles[numOfRows-i-1][j] = new Tile2D(cs, new Point2D(j * tilesWidth + pos.x, i * tilesHeight + pos.y),
                        tilesWidth, tilesHeight, Utils::randomColor, true, () -> String.valueOf(finalI*numOfCols+finalJ));
            }
    }

    public GridPlain2D(CoordinatedScreen cs, int numOfRows, int numOfCols) {
        this(cs, numOfRows, numOfCols, new Point2D());
    }

    public Point2D getPos() {
        return pos.getCopy();
    }

    public void setPos(double x, double y) {
        pos.set(x, y);
        for (int i = numOfRows-1; i >= 0; i--)
            for (int j = 0; j < numOfCols; j++)
                girdTiles[numOfRows-i-1][j].setPos(new Point2D(j * tilesWidth + pos.x, i * tilesHeight + pos.y));
    }

    public void move(double dx, double dy) {
        setPos(pos.x + dx, pos.y + dy);
    }

    public double getTilesWidth() {
        return tilesWidth;
    }

    public void setTilesWidth(double tilesWidth) {
        this.tilesWidth = tilesWidth;
    }

    public double getTilesHeight() {
        return tilesHeight;
    }

    public void setTilesHeight(double tilesHeight) {
        this.tilesHeight = tilesHeight;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }

    public float getGridThickness() {
        return gridThickness;
    }

    public void setGridThickness(float gridThickness) {
        this.gridThickness = gridThickness;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public CoordinatedScreen getCs() {
        return cs;
    }

    public void setCs(CoordinatedScreen cs) {
        this.cs = cs;
    }

    public Tile2D getTile(int i, int j) {
        return girdTiles[i][j];
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void actOnAllTiles(ActOnTile actOnTile) {
        for (int i = 0; i < numOfRows; i++)
            for (int j = 0; j < numOfCols; j++)
                actOnTile.act(girdTiles[i][j]);
    }

    private void drawGrid(Graphics2D g2d) {
        if (!drawGrid)
            return;
        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(gridThickness));
        for (int i = -1; i < numOfRows; i++)
            g2d.drawLine(cs.screenX(pos.x),
                    cs.screenY(i * tilesHeight + pos.y), cs.screenX(tilesWidth * numOfCols + pos.x), cs.screenY(i * tilesHeight + pos.y));
        for (int i = 0; i < numOfCols + 1; i++)
            g2d.drawLine(cs.screenX(i * tilesWidth + pos.x),
                    cs.screenY(-tilesHeight + pos.y), cs.screenX(tilesWidth * i + pos.x), cs.screenY((numOfRows-1) * tilesHeight + pos.y));
    }

    @Override
    public void render(Graphics2D g2d) {
        for (var row : girdTiles)
            Arrays.stream(row).forEach(tile -> tile.renderIfInView(g2d));
        drawGrid(g2d);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @FunctionalInterface
    public interface ActOnTile {
        void act(Tile2D tile);
    }
}
