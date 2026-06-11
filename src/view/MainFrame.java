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
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mazePanel = new MazePanel(currentConfig);

        JPanel topPanel = new JPanel();
        JButton btnFetch = new JButton("Refresh Config");
        JButton btnGetMaze = new JButton("GET MAZE");
        JButton btnCheck = new JButton("Check Solution"); // הכפתור החדש

        topPanel.add(new JLabel("W:")); topPanel.add(txtWidth);
        topPanel.add(new JLabel("H:")); topPanel.add(txtHeight);
        topPanel.add(btnFetch); topPanel.add(btnGetMaze);
        topPanel.add(btnCheck); // הוספה לפאנל

        add(topPanel, BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);

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
                        mazePanel = new MazePanel(currentConfig);
                        remove(((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER));
                        add(mazePanel, BorderLayout.CENTER);
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
            try {
                int w = Integer.parseInt(txtWidth.getText());
                int h = Integer.parseInt(txtHeight.getText());
                BufferedImage img = networkManager.fetchMazeImage(w, h);
                boolean[][] maze = networkManager.parseMaze(img);
                mazePanel.setMaze(maze);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
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