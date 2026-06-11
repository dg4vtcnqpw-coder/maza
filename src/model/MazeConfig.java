package model;

import java.awt.Color;

public class MazeConfig {
    private Color wallCellColor;
    private Color pathColor;
    private boolean drawGrid;
    private Color gridColor;
    private int animationDelayMs;

    // ה-Getters שה-MazePanel וכל שאר המחלקות צריכים
    public Color getWallCellColor() {
        return wallCellColor != null ? wallCellColor : Color.BLACK;
    }

    public Color getPathColor() {
        return pathColor != null ? pathColor : Color.GREEN;
    }

    public Color getGridColor() {
        return gridColor != null ? gridColor : Color.GRAY;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public int getAnimationDelayMs() {
        return animationDelayMs;
    }

    // Setters
    public void setWallCellColor(String hex) { this.wallCellColor = Color.decode(hex); }
    public void setPathColor(String hex) { this.pathColor = Color.decode(hex); }
    public void setDrawGrid(boolean drawGrid) { this.drawGrid = drawGrid; }
    public void setGridColor(String hex) { this.gridColor = Color.decode(hex); }
    public void setAnimationDelayMs(int ms) { this.animationDelayMs = ms; }
}