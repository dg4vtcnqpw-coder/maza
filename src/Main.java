import logic.MazeGenerator;
import logic.MazeSolver;
import logic.NetworkManager;
import model.MazeConfig;
import view.MazePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.Point;

public class Main extends JFrame {
    private final MazeGenerator mazeGenerator = new MazeGenerator();
    private final NetworkManager networkManager = new NetworkManager();
    private final MazeSolver mazeSolver = new MazeSolver();
    private MazeConfig config;

    private final JLabel lblWallColor = new JLabel("צבע קירות: -");
    private final JLabel lblPathColor = new JLabel("צבע נתיב: -");
    private final JLabel lblGrid = new JLabel("רשת: -");
    private final JLabel lblDelay = new JLabel("השהיית אנימציה: -");

    private final JTextField txtWidth = new JTextField("30", 5);
    private final JTextField txtHeight = new JTextField("30", 5);

    private final JButton btnRefresh = new JButton("Refresh Config");
    private final JButton btnGetMaze = new JButton("Generate Maze");
    private final JButton btnCheckSolution = new JButton("Check Solution");
    private final JButton btnStopAnimation = new JButton("Stop Animation"); // משימה 8
    private final JCheckBox chkDrawGrid = new JCheckBox("Show Grid");
    private final JLabel lblErrorMessage = new JLabel(" ");
    private final JLabel lblClickHint = new JLabel("Select start/end: left click=start, right click=end");

    // משימה 2 + משימה 5: תוויות טקסט חדשות להצגת נתוני המבוך
    private final JLabel lblMazeSize = new JLabel("Maze size: -");
    private final JLabel lblSolutionLength = new JLabel("Solution length: -");

    private final MazePanel mazePanel;

    public Main() {
        super("Maze Visualizer Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        config = new MazeConfig();
        mazePanel = new MazePanel(config);

        // בניית פאנל השליטה וההגדרות העליון
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        infoPanel.add(lblWallColor);
        infoPanel.add(lblPathColor);
        infoPanel.add(lblGrid);
        infoPanel.add(lblDelay);
        infoPanel.add(btnRefresh);

        inputPanel.add(new JLabel("Width (5-100):"));
        inputPanel.add(txtWidth);
        inputPanel.add(new JLabel("Height (5-100):"));
        inputPanel.add(txtHeight);
        inputPanel.add(btnGetMaze);
        inputPanel.add(btnCheckSolution);
        inputPanel.add(btnStopAnimation); // הוספת כפתור עצירה לחלון
        inputPanel.add(chkDrawGrid);
        inputPanel.add(lblClickHint);

        // הוספת התוויות החדשות למסך
        inputPanel.add(lblMazeSize);
        inputPanel.add(lblSolutionLength);
        inputPanel.add(lblErrorMessage);

        btnCheckSolution.setEnabled(false);
        btnStopAnimation.setEnabled(false); // כבוי כברירת מחדל כשאין אנימציה
        chkDrawGrid.setSelected(config.isDrawGrid());
        lblErrorMessage.setForeground(Color.RED);
        lblErrorMessage.setFont(lblErrorMessage.getFont().deriveFont(Font.BOLD));
        lblErrorMessage.setOpaque(true);
        lblErrorMessage.setBackground(getBackground());

        topPanel.add(infoPanel);
        topPanel.add(inputPanel);
        add(topPanel, BorderLayout.NORTH);

        // פאנל מרכזי חכם שממרכז את המבוך בצורה מושלמת מבלי למתוח אותו
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.LIGHT_GRAY);
        centerWrapper.add(mazePanel);

        add(centerWrapper, BorderLayout.CENTER);

        // הגדרת מאזיני לחיצה
        btnRefresh.addActionListener(e -> loadConfigFromServer());

        chkDrawGrid.addItemListener(e -> {
            config.setDrawGrid(chkDrawGrid.isSelected());
            mazePanel.repaint();
        });

        btnGetMaze.addActionListener(e -> {
            resetFieldColors();
            lblErrorMessage.setText(" ");

            Integer width = parseInput(txtWidth.getText(), txtWidth);
            Integer height = parseInput(txtHeight.getText(), txtHeight);
            if (width == null || height == null) {
                lblErrorMessage.setText("Invalid width or height.");
                return;
            }

            txtWidth.setText(String.valueOf(width));
            txtHeight.setText(String.valueOf(height));

            new Thread(() -> {
                try {
                    boolean[][] generatedMaze = mazeGenerator.generate(width, height);

                    SwingUtilities.invokeLater(() -> {
                        mazePanel.setMaze(generatedMaze);
                        mazePanel.setStartEndDefaults();

                        lblMazeSize.setText("Maze size: " + generatedMaze.length + " x " + generatedMaze[0].length);
                        lblSolutionLength.setText("Solution length: -");

                        btnCheckSolution.setEnabled(true);
                        getContentPane().validate();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error creating maze: " + ex.getMessage()));
                }
            }).start();
        });

        btnCheckSolution.addActionListener(e -> {
            boolean[][] currentMaze = mazePanel.getMazeArray();
            Point start = mazePanel.getStartPoint();
            Point end = mazePanel.getEndPoint();
            if (currentMaze != null && start != null && end != null) {
                List<Point> path = mazeSolver.solve(currentMaze, start, end);
                if (path != null) {
                    lblSolutionLength.setText("Solution length: " + path.size());

                    btnCheckSolution.setEnabled(false);
                    btnGetMaze.setEnabled(false);
                    btnRefresh.setEnabled(false);
                    btnStopAnimation.setEnabled(true);

                    mazePanel.startAnimation(path);
                } else {
                    lblSolutionLength.setText("Solution length: no path");
                    JOptionPane.showMessageDialog(this, "No solution found");
                }
            }
        });

        // משימה 8: לחיצה על כפתור עצור תפסיק מיד את הטיימר
        btnStopAnimation.addActionListener(e -> {
            mazePanel.stopAnimation();
        });

        // משימה 7 + משימה 8: שיחרור הנעילה והחזרת הכפתורים למצב פעיל בסיום/עצירה
        mazePanel.setOnAnimationEnd(() -> {
            btnCheckSolution.setEnabled(true);
            btnGetMaze.setEnabled(true);
            btnRefresh.setEnabled(true);
            btnStopAnimation.setEnabled(false); // ננעל בחזרה
        });

        setSize(1100, 750);
        setLocationRelativeTo(null);
        setVisible(true);

        loadConfigFromServer();
    }

    private void loadConfigFromServer() {
        new Thread(() -> {
            try {
                config = networkManager.fetchConfig();

                SwingUtilities.invokeLater(() -> {
                    lblWallColor.setText("צבע קירות: " + String.format("#%02x%02x%02x", config.getWallCellColor().getRed(), config.getWallCellColor().getGreen(), config.getWallCellColor().getBlue()));
                    lblPathColor.setText("צבע נתיב: " + String.format("#%02x%02x%02x", config.getPathColor().getRed(), config.getPathColor().getGreen(), config.getPathColor().getBlue()));
                    lblGrid.setText("רשת: " + (config.isDrawGrid() ? "כן" : "לא"));
                    lblDelay.setText("השהייה: " + config.getAnimationDelayMs() + "ms");

                    mazePanel.repaint();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "שגיאה בטעינת ההגדרות: " + ex.getMessage()));
            }
        }).start();
    }

    private Integer parseInput(String text, JTextField field) {
        try {
            int val = Integer.parseInt(text.trim());
            if (val >= 5 && val <= 100) return val;
        } catch (NumberFormatException ignored) {}
        field.setBackground(new Color(255, 200, 200));
        field.setOpaque(true);
        return null;
    }

    private void resetFieldColors() {
        txtWidth.setBackground(Color.WHITE);
        txtWidth.setOpaque(true);
        txtHeight.setBackground(Color.WHITE);
        txtHeight.setOpaque(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}