package logic;

import java.awt.Point;
import java.util.*;

public class MazeSolver {

    // הפונקציה הראשית שתחזיר לנו את המסלול מההתחלה (0,0) לסוף
    public List<Point> solve(boolean[][] maze) {
        int width = maze.length;
        int height = maze[0].length;
        Point start = new Point(0, 0);
        Point end = new Point(width - 1, height - 1);

        // אם ההתחלה או הסוף הם קירות, אין פתרון
        if (!maze[0][0] || !maze[width - 1][height - 1]) return null;

        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>(); // כדי לשחזר את הדרך
        queue.add(start);
        parentMap.put(start, null);

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // ימינה, שמאלה, למטה, למעלה

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                return reconstructPath(parentMap, end);
            }

            for (int[] dir : directions) {
                int nextX = current.x + dir[0];
                int nextY = current.y + dir[1];
                Point next = new Point(nextX, nextY);

                if (isValid(maze, nextX, nextY) && maze[nextX][nextY] && !parentMap.containsKey(next)) {
                    queue.add(next);
                    parentMap.put(next, current);
                }
            }
        }
        return null; // לא נמצא פתרון
    }

    private boolean isValid(boolean[][] maze, int x, int y) {
        return x >= 0 && y >= 0 && x < maze.length && y < maze[0].length;
    }

    private List<Point> reconstructPath(Map<Point, Point> parentMap, Point end) {
        List<Point> path = new ArrayList<>();
        for (Point at = end; at != null; at = parentMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}