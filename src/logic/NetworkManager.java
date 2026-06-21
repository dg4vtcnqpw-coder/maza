package logic;

import model.MazeConfig;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NetworkManager {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();

    // שליפת ה-JSON ופירוקו הידני ללא ספריות חיצוניות
    public MazeConfig fetchConfig() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://backend-qcf9.onrender.com/fm1/get-render-config"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        MazeConfig config = new MazeConfig();
        config.setWallCellColor(extractString(json, "wallCellColor"));
        config.setPathColor(extractString(json, "pathColor"));
        config.setDrawGrid(Boolean.parseBoolean(extractString(json, "drawGrid")));
        config.setGridColor(extractString(json, "gridColor"));
        config.setAnimationDelayMs(Integer.parseInt(extractString(json, "animationDelayMs")));
        return config;
    }

    // משיכת תמונת המבוך הגולמית מהשרת
    public BufferedImage fetchMazeImage(int width, int height) throws Exception {
        String urlString = "https://backend-qcf9.onrender.com/fm1/get-maze-image?width=" + width + "&height=" + height;
        return ImageIO.read(new URL(urlString));
    }

    // שלב 6 + 7: מעבר על הפיקסלים ופענוח המבנה (פיקסל לבן = מעבר חופשי)
    public boolean[][] parseMaze(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        boolean[][] maze = new boolean[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgb = image.getRGB(i, j);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // בדיקה אם הפיקסל לבן (מעבר) או צבעוני/כהה (קיר)
                maze[i][j] = (r == 255 && g == 255 && b == 255);
            }
        }
        return maze;
    }

    private String extractString(String json, String key) {
        String keyWithQuotes = "\"" + key + "\":";
        int start = json.indexOf(keyWithQuotes) + keyWithQuotes.length();
        int valueStart = json.indexOf("\"", start);
        if (valueStart == -1 || valueStart > json.indexOf(",", start)) {
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        } else {
            int valueEnd = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        }
    }
}