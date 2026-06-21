package logic;

import java.awt.Point;
import java.util.*;

public class MazeSolver {
    private static final int[][] DIRS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // 4 כיווני תנועה חוקיים

    public List<Point> solve(boolean[][] maze) {
        int width = maze.length;
        if (width == 0) return null;
        int height = maze[0].length;

        // תנאי בסיס מההוראות: אם ההתחלה או הסוף חסומים - אין פתרון
        if (!maze[0][0] || !maze[width - 1][height - 1]) return null;

        Queue<Point> queue = new LinkedList<>();
        boolean[][] visited = new boolean[width][height];
        Point[][] parent = new Point[width][height];

        Point start = new Point(0, 0);
        queue.add(start);
        visited[0][0] = true;

        boolean found = false;
        while (!queue.isEmpty()) {
            Point curr = queue.poll();
            if (curr.x == width - 1 && curr.y == height - 1) {
                found = true;
                break;
            }

            for (int[] d : DIRS) {
                int nx = curr.x + d[0];
                int ny = curr.y + d[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height && maze[nx][ny] && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    parent[nx][ny] = curr;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        if (!found) return null;

        // שחזור המסלול מהסוף להתחלה והפיכתו
        List<Point> path = new ArrayList<>();
        Point curr = new Point(width - 1, height - 1);
        while (curr != null) {
            path.add(curr);
            curr = parent[curr.x][curr.y];
        }
        Collections.reverse(path);
        return path;
    }
}