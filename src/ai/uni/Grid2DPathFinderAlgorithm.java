package ai.uni;


import algo.Algorithm;
import jmath.datatypes.tuples.Point3D;
import org.jetbrains.annotations.NotNull;
import utils.SemaphoreBase;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

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
        addSemaphore("middle-dls");
        addSemaphore("begin-bfs");
    }

    private Point findRobot() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j].contains("r"))
                    return new Point(i, j);
        throw new RuntimeException("ERROR:: Robot lost");
    }

    public List<Point> ids(Point start) {
        List<Point> res;
        for (int i = 0; i < rows * cols; i++) {
            info = null;
            if ((res = dls(start, new int[rows][cols], i)) != null) {
                if (res.isEmpty())
                    break;
                Collections.reverse(res);
                acquire("begin-dls");
                info = null;
                return res;
            }
        }
        acquire("begin-dls");
        info = null;
        return null;
    }

    public List<Point> dls(Point start, int[][] explored, int limit) {
        explored[start.x][start.y] = 10;
        info = explored;

        acquire("begin-dls");

        if (isGoal(start))
            return new ArrayList<>(List.of(start));

        if (limit == 0)
            return null;

        List<Point> res;
        var neighbors = neighbors(start);
        if (neighbors.isEmpty())
            return List.of();
        neighbors.stream().filter(e -> explored[e.x][e.y] <= 0).forEach(e -> info[e.x][e.y] = -1);
        boolean flag = false;
        for (var neighbor : neighbors)
            if (explored[neighbor.x][neighbor.y] <= 0) {
                info[start.x][start.y] = 10;
                acquire("begin-dls");
                info[start.x][start.y] = 1;
                explored[neighbor.x][neighbor.y] = 1;
                res = dls(neighbor, clone(explored), limit - 1);
                info[neighbor.x][neighbor.y] = 1;
                explored[neighbor.x][neighbor.y] = 1;
                if (res == null) {
                    flag = true;
                    continue;
                }
                if (res.isEmpty())
                    continue;
                res.add(start);
                return res;
            }

        return flag ? null : List.of();
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

    private List<Point> neighborsDest(Point point) {
        return neighbors(point, -2, true);
    }

    private List<Point> neighbors(Point point) {
        return neighbors(point, 1, true);
    }

    private List<Point> neighbors(Point point, int factor, boolean forButter) {
        var res = new ArrayList<Point>();
        var clone = new Point(point);
        clone.translate(0, -1);
        if (!forButter) {
            if (isNeighbor(clone))
                res.add(new Point(clone));
            clone.translate(1, 1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
            clone.translate(-1, 1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
            clone.translate(-1, -1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
        return res;
        }
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        clone.translate(1, 1);
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        clone.translate(-1, 1);
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        clone.translate(-1, -1);
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        return res;
    }

    private boolean isNeighbor(Point point) {
        return point.x > -1 && point.x < rows &&
                point.y > -1 && point.y < cols &&
                !cells[point.x][point.y].equalsIgnoreCase("x") &&
                !cells[point.x][point.y].contains("b") && !cells[point.x][point.y].contains("r");
    }

    private boolean validNeighbor(Point from, Point to, int factor) {
        from = new Point(from);
        from.translate(factor * (from.x - to.x), factor * (from.y - to.y));
        return isNeighbor(to) && isNeighbor(from)/* && isNeighborForRobot(from, to, factor)*/;
    }

    private boolean isNeighborForRobot(Point butter, Point butterDest, int factor) {
        return bbfs(findRobot(), new Point(factor * (butter.x - butterDest.x), factor * (butter.y - butterDest.y))) != null;
    }

    public List<Point> robotPath(List<Point> butterPath) {
        var res = new ArrayList<Point>();
        Point p1 = null;
        var robot = findRobot();
        getInfo()[robot.x][robot.y] = 15;
        for (int i = 0; i < butterPath.size() - 1; i++) {
            p1 = butterPath.get(i);
            var p2 = butterPath.get(i + 1);
            var dest = new Point(2 * p1.x - p2.x, 2 * p1.y - p2.y);
            var path = bbfs(robot, dest);
            if (path == null) {
                acquire("begin-dls");
                info = null;
                return null;
            }
            res.addAll(path);
            cells[p2.x][p2.y] += "b";
            cells[p1.x][p1.y] = cells[p1.x][p1.y].charAt(0) + "r";
            getInfo()[p1.x][p1.y] = 15;
            cells[dest.x][dest.y] = String.valueOf(cells[dest.x][dest.y].charAt(0));
            robot = p1;
        }
        res.add(p1);
        res.forEach(e -> getInfo()[e.x][e.y] = 15);
        return res;
    }

    public List<Point> bbfs(Point start, Point dest) {
        if (start.equals(dest)) {
            acquire("begin-dls");
            getInfo()[start.x][start.y] = 15;
            return new ArrayList<>(List.of(start));
        }
        Optional<Point> intersect;
        var explored = new int[rows][cols];
        var queueOfStart = new ArrayDeque<>(List.of(new Node(start, null, 0)));
        var queueOfDest = new ArrayDeque<>(List.of(new Node(dest, null, 0)));
        Node nodeStart = null, nodeDest = null;
        var pointNodeMap = new HashMap<Point, Node>();
        boolean find = false;
        explored[start.x][start.y] = 1;
        explored[dest.x][dest.y] = 2;
        while (!queueOfStart.isEmpty() && !queueOfDest.isEmpty()) {
            // expand from start
//            acquire("begin-dls");
            nodeStart = queueOfStart.pop();
            start = nodeStart.point;
            var neighborsStart = neighbors(start, 0, false);
            if ((intersect = neighborsStart.stream().filter(e -> explored[e.x][e.y] == 2).findFirst()).isPresent()) {
                nodeDest = pointNodeMap.get(intersect.get());
                find = true;
                break;
            }
            Node finalNodeStart = nodeStart;
            neighborsStart.stream().filter(e -> explored[e.x][e.y] == 0)
                    .forEach(e -> {
                        explored[e.x][e.y] = 1;
                        var node = new Node(e, finalNodeStart, 0);
                        queueOfStart.add(node);
                        pointNodeMap.put(e, node);
                    });
            // expand from dest
//            acquire("begin-dls");
            nodeDest = queueOfDest.pop();
            dest = nodeDest.point;
            var neighborsDest = neighbors(dest, 0, false);
            if ((intersect = neighborsDest.stream().filter(e -> explored[e.x][e.y] == 1).findFirst()).isPresent()) {
                nodeStart = pointNodeMap.get(intersect.get());
                find = true;
                break;
            }
            Node finalNodeDest = nodeDest;
            neighborsDest.stream().filter(e -> explored[e.x][e.y] == 0)
                    .forEach(e -> {
                        explored[e.x][e.y] = 2;
                        var node = new Node(e, finalNodeDest, 0);
                        queueOfDest.add(node);
                        pointNodeMap.put(e, node);
                    });
        }
        if (!find)
            return null;
        var path = new LinkedList<Point>();
        while (nodeStart != null) {
            path.addFirst(nodeStart.point);
            nodeStart = nodeStart.parent;
        }
        while (nodeDest != null) {
            path.add(nodeDest.point);
            nodeDest = nodeDest.parent;
        }
        for (int i = 1; i < path.size(); i++) {
            acquire("begin-dls");
            var p = path.get(i - 1);
            cells[p.x][p.y] = String.valueOf(cells[p.x][p.y].charAt(0));
            var pp = path.get(i);
            cells[pp.x][pp.y] += 'r';
            getInfo()[pp.x][pp.y] = 15;
        }
        acquire("begin-dls");
        return path;
    }

    ////////////////
    public List<Point> bbfs(Point start) {
        Optional<Point> intersect;
        var dest = nearestManhattanDest(start);
        var queueOfStart = new ArrayDeque<>(List.of(new Node(start, null, 0)));
        var mapOfQueueDestinations = new HashMap<Point, ArrayDeque<Node>>();
        getDestinations().forEach(e -> mapOfQueueDestinations.put(e, new ArrayDeque<>(List.of(new Node(e, null, 0)))));
        Node nodeStart = null, nodeDest = null;
        var pointNodeMap = new HashMap<Point, Node>();
        boolean find = false;
        getInfo()[start.x][start.y] = 1;
        for (var d : mapOfQueueDestinations.keySet())
            getInfo()[d.x][d.y] = 2;
        while (!queueOfStart.isEmpty() && !mapOfQueueDestinations.isEmpty()) {
            // expand from start
            acquire("begin-dls");
            nodeStart = queueOfStart.pop();
            start = nodeStart.point;
            var neighborsStart = neighbors(start);
            if ((intersect = neighborsStart.stream().filter(e -> getInfo()[e.x][e.y] == 2).findFirst()).isPresent()) {
                nodeDest = pointNodeMap.get(intersect.get());
                find = true;
                break;
            }
            Node finalNodeStart = nodeStart;
            neighborsStart.stream().filter(e -> getInfo()[e.x][e.y] == 0)
                    .forEach(e -> {
                        getInfo()[e.x][e.y] = 10;
                        acquire("begin-dls");
                        getInfo()[e.x][e.y] = 1;
                        var node = new Node(e, finalNodeStart, 0);
                        queueOfStart.add(node);
                        pointNodeMap.put(e, node);
                    });
            // expand from dest

            var shouldRemove = new ArrayList<>();
            for (var kv : mapOfQueueDestinations.entrySet()) {
                if (kv.getValue().isEmpty()) {
                    shouldRemove.add(kv.getKey());
                    continue;
                }
                acquire("begin-dls");
                nodeDest = kv.getValue().pop();
                dest = nodeDest.point;
                var neighborsDest = neighborsDest(dest);
                if ((intersect = neighborsDest.stream().filter(e -> getInfo()[e.x][e.y] == 1).findFirst()).isPresent()) {
                    nodeStart = pointNodeMap.get(intersect.get());
                    find = true;
                    break;
                }
                Node finalNodeDest = nodeDest;
                neighborsDest.stream().filter(e -> getInfo()[e.x][e.y] == 0)
                        .forEach(e -> {
                            getInfo()[e.x][e.y] = 10;
                            acquire("begin-dls");
                            getInfo()[e.x][e.y] = 2;
                            var node = new Node(e, finalNodeDest, 0);
                            kv.getValue().add(node);
                            pointNodeMap.put(e, node);
                        });
            }
            //noinspection SuspiciousMethodCalls
            shouldRemove.forEach(mapOfQueueDestinations::remove);
        }
        if (!find) {
            acquire("begin-dls");
            info = null;
            return null;
        }
        var path = new LinkedList<Point>();
        while (nodeStart != null) {
            path.addFirst(nodeStart.point);
            nodeStart = nodeStart.parent;
        }
        while (nodeDest != null) {
            path.add(nodeDest.point);
            nodeDest = nodeDest.parent;
        }
        path.forEach(e -> getInfo()[e.x][e.y] = 16);
        acquire("begin-dls");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                info[i][j] = info[i][j] == 16 ? 16 : 0;
        return path;
    }
    ////////////////

    public List<Point> aStar(Point start) {
        var fringe = new PriorityQueue<Node>();
        var dest = nearestManhattanDest(start);
        fringe.add(new Node(start, null, manhattan(start, dest)));
        Node node = null;
        while (!fringe.isEmpty()) {
//            acquire("begin-dls");
            node = fringe.poll();
            assert node != null;
            start = node.point;
            getInfo()[start.x][start.y] = 10;
//            acquire("begin-dls");
            getInfo()[start.x][start.y] = 1;
            if (isGoal(start))
                break;
            var neighbors = neighbors(start);
            Node finalNode = node;
            neighbors.stream().
                    filter(e -> getInfo()[e.x][e.y] == 0).
                    forEach(e -> {
                        fringe.add(new Node(e, finalNode, manhattan(e, dest)));
                        getInfo()[e.x][e.y] = -1;
                    });
        }
        var path = new LinkedList<Point>();
        while (node != null) {
            path.addFirst(node.point);
            node = node.parent;
        }
        return path;
    }

    private List<Point> getDestinations() {
        var res = new ArrayList<Point>();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j].contains("p"))
                    res.add(new Point(i, j));
        return res;
    }

    private Point nearestManhattanDest(Point start) {
        Point tempPoint, dest = null;
        int temp;
        int minManhattan = Integer.MAX_VALUE;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j].contains("p") && minManhattan > (temp = manhattan(start, tempPoint = new Point(i, j)))) {
                    minManhattan = temp;
                    dest = tempPoint;
                }
        return dest;
    }

    private static int manhattan(Point start, Point dest) {
        return Math.abs(start.x - dest.x) + Math.abs(start.y - dest.y);
    }

    public String[][] getCells() {
        return cells;
    }

    public int[][] getInfo() {
        if (info == null) {
            info = new int[rows][cols];
            var r = findRobot();
            info[r.x][r.y] = 15;
        }
        return info;
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

    private class Node implements Comparable<Node> {
        private final Point point;
        private final int g, h;
        private final Node parent;
        public Node(Point point, Node parent, int h) {
            this.point = point; this.g = parent == null ? 0 : parent.g + (cells[point.x][point.y].charAt(0) - '0');
            this.h = h; this.parent = parent;
        }
        @Override public int compareTo(@NotNull Node o) {return Integer.compare(g + h, o.g + o.h);}
    }
}
