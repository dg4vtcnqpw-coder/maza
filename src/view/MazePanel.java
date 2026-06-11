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

        // המספר 5 כאן קובע את המהירות.
        // ככל שהמספר קטן יותר -> המהירות גבוהה יותר!
        timer = new Timer(1, e -> {
            if (animationIndex < solutionPath.size()) {
                animationIndex+=60;
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

        // ציור הפתרון (האנימציה) - עכשיו כקו עבה ורציף
        if (solutionPath != null && solutionPath.size() > 1) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(config.getPathColor());

            // כאן קובעים את העובי של הקו (תשנה את 10 למספר שאתה רוצה!)
            g2.setStroke(new BasicStroke(10));

            for (int i = 0; i < animationIndex - 1; i++) {
                Point p1 = solutionPath.get(i);
                Point p2 = solutionPath.get(i + 1);

                // חישוב המרכז של כל תא
                int x1 = p1.x * cellWidth + cellWidth / 2;
                int y1 = p1.y * cellHeight + cellHeight / 2;
                int x2 = p2.x * cellWidth + cellWidth / 2;
                int y2 = p2.y * cellHeight + cellHeight / 2;

                g2.drawLine(x1, y1, x2, y2);
            }
        }
        }
    }
