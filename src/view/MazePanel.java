package view;

import model.MazeConfig;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.Point;

public class MazePanel extends JPanel {
    private boolean[][] maze;
    private MazeConfig config;
    private List<Point> solutionPath;
    private int animationIndex = 0;
    private Timer timer;
    private final int cellSize = 12; // הגודל הקבוע והיציב שמתאים בול למסך
    private Runnable onAnimationEnd;

    public MazePanel(MazeConfig config) {
        this.config = config;
        setBackground(Color.DARK_GRAY);
    }

    public boolean[][] getMazeArray() {
        return this.maze;
    }

    public void setOnAnimationEnd(Runnable onAnimationEnd) {
        this.onAnimationEnd = onAnimationEnd;
    }

    public void setMaze(boolean[][] maze) {
        if (timer != null) timer.stop();
        this.maze = maze;
        this.solutionPath = null;
        this.animationIndex = 0;

        if (maze != null) {
            setPreferredSize(new Dimension(maze.length * cellSize, maze[0].length * cellSize));
        } else {
            setPreferredSize(new Dimension(360, 360));
        }
        revalidate();
        repaint();
    }

    // משימה 7: הפעלת האנימציה צעד אחר צעד
    public void startAnimation(List<Point> path) {
        if (timer != null) timer.stop();
        this.solutionPath = path;
        this.animationIndex = 0;

        timer = new Timer(config.getAnimationDelayMs(), e -> {
            if (animationIndex < solutionPath.size()) {
                animationIndex++;
                repaint();
            } else {
                timer.stop();
                if (onAnimationEnd != null) onAnimationEnd.run(); // שחרור כפתורים במיין
            }
        });
        timer.start();
    }

    // משימה 8: כפתור עצירת האנימציה באמצע בצורה תקינה
    public void stopAnimation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            if (onAnimationEnd != null) onAnimationEnd.run(); // שחרור מיידי של הכפתורים במיין
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        int cols = maze.length;
        int rows = maze[0].length;

        // ציור המבוך הבסיסי
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (maze[i][j]) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(config.getWallCellColor());
                }
                g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);

                if (config.isDrawGrid()) {
                    g.setColor(config.getGridColor());
                    g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
            }
        }

        // משימה 3: סימון ברור של נקודת ההתחלה (ירוק) ונקודת הסיום (כחול)
        g.setColor(Color.GREEN); // התחלה ב-(0,0)
        g.fillRect(0, 0, cellSize, cellSize);

        g.setColor(Color.BLUE); // סיום בפינה הימנית התחתונה
        g.fillRect((cols - 1) * cellSize, (rows - 1) * cellSize, cellSize, cellSize);

        // ציור נתיב הפתרון המתקדם באנימציה
        if (solutionPath != null) {
            g.setColor(config.getPathColor());
            for (int i = 0; i < animationIndex; i++) {
                Point p = solutionPath.get(i);
                // דילוג על ציור ההתחלה והסיום כדי לשמור על הצבעים המקוריים שלהם חצי גלויים
                if ((p.x == 0 && p.y == 0) || (p.x == cols - 1 && p.y == rows - 1)) {
                    continue;
                }
                g.fillRect(p.x * cellSize + 1, p.y * cellSize + 1, cellSize - 2, cellSize - 2);
            }
        }
    }
}