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
    private double zoomFactor = 1.0;

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

        // חישוב זום התחלתי דינמי כדי שהמבוך יתחיל תמיד בגודל קטן ונוח למסך (אך לא קטן מידי)
        if (maze != null && maze.length > 0) {
            // הגדרת יעד גודל שמתאים בדיוק למסך (כ-650 פיקסלים) מבלי לחרוג מהשוליים של החלון
            double targetSize = 650.0; 
            int maxDim = Math.max(maze.length, maze[0].length);
            this.zoomFactor = targetSize / (maxDim * 10.0); 
        } else {
            this.zoomFactor = 1.0;
        }

        if (timer != null) timer.stop();

        updatePreferredSize();
        
        // חזרה חלקה להתחלת המבוך (למעלה ושמאלה) כשנטען מבוך חדש
        SwingUtilities.invokeLater(() -> scrollRectToVisible(new Rectangle(0, 0, 1, 1)));
    }

    public void zoomIn() {
        zoomFactor *= 1.2;
        updatePreferredSize();
    }

    public void zoomOut() {
        zoomFactor /= 1.2;
        updatePreferredSize();
    }

    private void updatePreferredSize() {
        // מחשב את גודל הפאנל בהתאם לגודל המבוך ורמת הזום הנוכחית
        if (maze != null && maze.length > 0) {
            int w = (int) (maze.length * 10 * zoomFactor);
            int h = (int) (maze[0].length * 10 * zoomFactor);
            setPreferredSize(new Dimension(w, h));
            revalidate();
        }
        repaint();
    }

    public void startAnimation(List<Point> path) {
        this.solutionPath = path;
        this.animationIndex = 0;

        timer = new Timer(1, e -> {
            if (animationIndex < solutionPath.size()) {
                animationIndex += 300; // קפיצה משמעותית כדי שהפתרון ירוץ הרבה יותר מהר
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // חישוב גודל תא מדויק לפי הזום כדי למנוע חריגה מהשוליים
        int cols = maze.length;
        int rows = maze[0].length;
        double cellWidth = 10.0 * zoomFactor;
        double cellHeight = 10.0 * zoomFactor;

        // צביעת הרקע (הקירות) בפעולה אחת
        g2.setColor(config.getWallCellColor());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (maze[i][j]) {
                    g2.fillRect((int) (i * cellWidth), (int) (j * cellHeight), (int) Math.ceil(cellWidth), (int) Math.ceil(cellHeight));
                }
            }
        }

        if (solutionPath != null) {
            g2.setColor(config.getPathColor());
            // עובי קיצוני במיוחד (פי 4 מגודל התא) - ימלא כל חלל אפשרי וימחק הפרדות בין שורות
            float strokeWidth = (float) (Math.max(cellWidth, cellHeight) * 4.0);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int limit = Math.min(animationIndex, solutionPath.size() - 1);

            for (int i = 0; i < limit; i++) {
                Point p1 = solutionPath.get(i);
                Point p2 = solutionPath.get(i + 1);

                int x1 = (int) (p1.x * cellWidth + cellWidth / 2);
                int y1 = (int) (p1.y * cellHeight + cellHeight / 2);
                int x2 = (int) (p2.x * cellWidth + cellWidth / 2);
                int y2 = (int) (p2.y * cellHeight + cellHeight / 2);

                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}