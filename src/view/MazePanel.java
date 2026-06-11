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
        int fastSpeed = 50;

        timer = new Timer(config.getAnimationDelayMs(), e -> {
            if (animationIndex < solutionPath.size()) {
                animationIndex++;
                repaint();
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        int cols = maze.length;
        int rows = maze[0].length;
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                g.setColor(maze[i][j] ? Color.WHITE : config.getWallCellColor());
                g.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
            }
        }

        if (solutionPath != null) {
            g.setColor(config.getPathColor());
            for (int i = 0; i < animationIndex; i++) {
                Point p = solutionPath.get(i);
                g.fillRect(p.x * cellWidth, p.y * cellHeight, cellWidth, cellHeight);
            }
        }
    }
}