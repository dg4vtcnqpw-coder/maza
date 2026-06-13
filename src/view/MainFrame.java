package view;

import logic.NetworkManager;
import model.MazeConfig;
import logic.MazeSolver; // ייבוא של הפותר
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.Point;

public class MainFrame extends JFrame {
    private final NetworkManager networkManager = new NetworkManager();
    private MazeConfig currentConfig = new MazeConfig();

    private JTextField txtWidth = new JTextField("30", 5);
    private JTextField txtHeight = new JTextField("30", 5);
    private MazePanel mazePanel;

    public MainFrame() {
        setTitle("Maze Game");
        setSize(1000, 700); // גודל חלון מוקטן יותר כבקשתך
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mazePanel = new MazePanel(currentConfig);

        JPanel topPanel = new JPanel();
        JButton btnFetch = new JButton("Refresh Config");
        JButton btnGetMaze = new JButton("GET MAZE");
        JButton btnCheck = new JButton("Check Solution"); // הכפתור החדש
        JButton btnZoomIn = new JButton("+"); // כפתור זום אין
        JButton btnZoomOut = new JButton("-"); // כפתור זום אאוט

        topPanel.add(new JLabel("W:")); topPanel.add(txtWidth);
        topPanel.add(new JLabel("H:")); topPanel.add(txtHeight);
        topPanel.add(btnFetch); topPanel.add(btnGetMaze);
        topPanel.add(btnCheck); // הוספה לפאנל
        topPanel.add(btnZoomIn);
        topPanel.add(btnZoomOut);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(mazePanel), BorderLayout.CENTER); // עטיפה בפאנל גלילה

        // פעולות כפתורי הזום
        btnZoomIn.addActionListener(e -> mazePanel.zoomIn());
        btnZoomOut.addActionListener(e -> mazePanel.zoomOut());

        // לוגיקה לרענון הגדרות
        btnFetch.addActionListener(e -> {
            // הפעלה ב-SwingWorker כדי לא לתקוע את הממשק
            new SwingWorker<MazeConfig, Void>() {
                @Override
                protected MazeConfig doInBackground() throws Exception {
                    // פעולת הרשת תרוץ כאן ברקע
                    return networkManager.fetchConfig();
                }

                @Override
                protected void done() {
                    try {
                        currentConfig = get(); // קבלת התוצאה מהרקע
                        // רענון ה-Panel בבטחה
                        boolean[][] oldMaze = mazePanel.getMazeArray();
                        mazePanel = new MazePanel(currentConfig);
                        if (oldMaze != null) {
                            mazePanel.setMaze(oldMaze); // החזרת המבוך לאחר העדכון
                        }
                        Component center = ((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
                        if (center instanceof JScrollPane) {
                            ((JScrollPane) center).setViewportView(mazePanel);
                        } else {
                            remove(center);
                            add(new JScrollPane(mazePanel), BorderLayout.CENTER);
                        }
                        revalidate();
                        repaint();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Error fetching config: " + ex.getMessage());
                    }
                }
            }.execute(); // הפעלת העבודה ברקע
        });
        // לוגיקה למשיכת מבוך
        btnGetMaze.addActionListener(e -> {
            btnGetMaze.setEnabled(false); // חסימת הכפתור עד לסיום הטעינה
            int w, h;
            try {
                w = Integer.parseInt(txtWidth.getText());
                h = Integer.parseInt(txtHeight.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Width and height must be valid integers.");
                btnGetMaze.setEnabled(true);
                return;
            }

            // הפעלה ב-SwingWorker כדי לא לתקוע את הממשק
            new SwingWorker<boolean[][], Void>() {
                @Override
                protected boolean[][] doInBackground() throws Exception {
                    BufferedImage img = networkManager.fetchMazeImage(w, h);
                    if (img == null) {
                        throw new Exception("Failed to read image from server");
                    }
                    return networkManager.parseMaze(img);
                }

                @Override
                protected void done() {
                    btnGetMaze.setEnabled(true); // שחרור הכפתור לאחר סיום
                    try {
                        boolean[][] maze = get();
                        mazePanel.setMaze(maze);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Error fetching maze: " + ex.getMessage());
                    }
                }
            }.execute();
        });

        // לוגיקה לפתרון המבוך
        btnCheck.addActionListener(e -> {
            if (mazePanel.getMazeArray() == null) return;

            MazeSolver solver = new MazeSolver();
            List<Point> path = solver.solve(mazePanel.getMazeArray());

            if (path == null) {
                JOptionPane.showMessageDialog(this, "No solution found");
            } else {
                mazePanel.startAnimation(path);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}