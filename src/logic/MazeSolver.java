package logic;

import java.awt.Point;
import java.util.*;

public class MazeSolver {

    private static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // ימינה, שמאלה, למטה, למעלה

    // מחלקה פנימית לניהול צמתים בתור העדיפויות עבור אלגוריתם A*
    private static class Node implements Comparable<Node> {
        int x, y, f;
        
        Node(int x, int y, int f) {
            this.x = x;
            this.y = y;
            this.f = f;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    // הפונקציה הראשית שתחזיר לנו את המסלול מההתחלה (0,0) לסוף
    public List<Point> solve(boolean[][] maze) {
        int width = maze.length;
        if (width == 0) return null;
        int height = maze[0].length;

        // אם ההתחלה או הסוף הם קירות, אין פתרון
        if (!maze[0][0] || !maze[width - 1][height - 1]) return null;

        // שימוש במערכים במקום ב-HashMap לייעול משמעותי של מהירות הגישה והזיכרון
        int[][] parentX = new int[width][height];
        int[][] parentY = new int[width][height];
        boolean[][] closedSet = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(parentX[i], -1);
            Arrays.fill(parentY[i], -1);
        }

        int[][] gScore = new int[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(gScore[i], Integer.MAX_VALUE);
        }
        gScore[0][0] = 0;

        // תור עדיפויות שימשוך קודם את הדרך שנראית קרובה יותר ליעד (A*)
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        openSet.add(new Node(0, 0, heuristic(0, 0, width - 1, height - 1)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            int cx = current.x;
            int cy = current.y;

            if (closedSet[cx][cy]) {
                continue; // כבר מצאנו למקום הזה מסלול קצר יותר בעבר
            }
            closedSet[cx][cy] = true;

            if (cx == width - 1 && cy == height - 1) {
                return reconstructPath(parentX, parentY, cx, cy);
            }

            for (int[] dir : DIRECTIONS) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];

                if (nx >= 0 && ny >= 0 && nx < width && ny < height && maze[nx][ny]) {
                    int tentativeG = gScore[cx][cy] + 1;
                    if (tentativeG < gScore[nx][ny]) {
                        parentX[nx][ny] = cx;
                        parentY[nx][ny] = cy;
                        gScore[nx][ny] = tentativeG;
                        
                        int fScore = tentativeG + heuristic(nx, ny, width - 1, height - 1);
                        openSet.add(new Node(nx, ny, fScore));
                    }
                }
            }
        }
        return null; // לא נמצא פתרון
    }

    private int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2); // מרחק מנהטן
    }

    private List<Point> reconstructPath(int[][] parentX, int[][] parentY, int endX, int endY) {
        List<Point> path = new ArrayList<>();
        int cx = endX;
        int cy = endY;
        
        while (cx != -1 && cy != -1) {
            path.add(new Point(cx, cy));
            int nextX = parentX[cx][cy];
            int nextY = parentY[cx][cy];
            cx = nextX;
            cy = nextY;
        }
        Collections.reverse(path);
        return path;
    }
}