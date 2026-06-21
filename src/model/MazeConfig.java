package model;

import java.awt.Color;

public class MazeConfig {
    private Color wallCellColor = Color.BLACK;
    private Color pathColor = Color.GREEN;
    private boolean drawGrid = false;
    private Color gridColor = Color.GRAY;
    private int animationDelayMs = 50;

    // Getters
    public Color getWallCellColor() { return wallCellColor; }
    public Color getPathColor() { return pathColor; }
    public boolean isDrawGrid() { return drawGrid; }
    public Color getGridColor() { return gridColor; }
    public int getAnimationDelayMs() { return animationDelayMs; }

    // Setters הבנויים לקבלת מחרוזות הקסדצימליות מה-JSON או אובייקטים של Color
    public void setWallCellColor(String hex) { this.wallCellColor = Color.decode(hex); }
    public void setPathColor(String hex) { this.pathColor = Color.decode(hex); }
    public void setDrawGrid(boolean drawGrid) { this.drawGrid = drawGrid; }
    public void setGridColor(String hex) { this.gridColor = Color.decode(hex); }
    public void setAnimationDelayMs(int ms) { this.animationDelayMs = ms; }
}