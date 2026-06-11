package view;

import model.MazeConfig;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.Point;
import javax.swing.Timer;

public class MazePanel extends JPanel {
    private boolean[][] maze;
    private MazeConfig config;
    private List<Point> solutionPath;
    private int animationIndex = 0;
    private Timer timer;

    public MazePanel(MazeConfig config) {
        this.config = config;
    }

    public boolean[][] getMazeArray() {
        return this.maze;
    }

    public void setMaze(boolean[][] maze) {
        this.maze = maze;
        this.solutionPath = null;
        this.animationIndex = 0;
        if (timer != null) timer.stop();
        repaint();
    }

    public void startAnimation(List<Point> path) {
        this.solutionPath = path;
        this.animationIndex = 0;

        timer = new Timer(1, e -> {
            if (animationIndex < solutionPath.size()) {
                animationIndex += 60; // כאן אנחנו קופצים 60 צעדים קדימה בכל פעימה
                repaint();
            } else {
                animationIndex = solutionPath.size() - 1; // וודא שלא נחרוג
                repaint();
                timer.stop();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2 = (Graphics2D) g; // הגדרת g2 כדי שנוכל לצייר קווים עבים
        int cols = maze.length;
        int rows = maze[0].length;
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                g2.setColor(maze[i][j] ? Color.WHITE : config.getWallCellColor());
                g2.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
            }
        }

        if (solutionPath != null) {
            g2.setColor(config.getPathColor());
            g2.setStroke(new BasicStroke(10));

            int limit = Math.min(animationIndex, solutionPath.size() - 1);

            for (int i = 0; i < limit; i++) {
                Point p1 = solutionPath.get(i);
                Point p2 = solutionPath.get(i + 1);

                int x1 = p1.x * cellWidth + cellWidth / 2;
                int y1 = p1.y * cellHeight + cellHeight / 2;
                int x2 = p2.x * cellWidth + cellWidth / 2;
                int y2 = p2.y * cellHeight + cellHeight / 2;

                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}