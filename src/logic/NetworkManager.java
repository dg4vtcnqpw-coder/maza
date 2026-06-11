package logic;

import model.MazeConfig;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NetworkManager {
    private final HttpClient client = HttpClient.newHttpClient();

    // פונקציה למשיכת הגדרות הציור מהשרת
    public MazeConfig fetchConfig() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://backend-qcf9.onrender.com/fm1/get-render-config"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        System.out.println("קיבלתי מהשרת: " + json);

        MazeConfig config = new MazeConfig();
        config.setWallCellColor(extractString(json, "wallCellColor"));
        config.setPathColor(extractString(json, "pathColor"));
        config.setDrawGrid(Boolean.parseBoolean(extractString(json, "drawGrid")));
        config.setGridColor(extractString(json, "gridColor"));
        config.setAnimationDelayMs(Integer.parseInt(extractString(json, "animationDelayMs")));
        return config;
    }

    // פונקציה למשיכת תמונת המבוך מהשרת
    public BufferedImage fetchMazeImage(int width, int height) throws Exception {
        String urlString = "https://backend-qcf9.onrender.com/fm1/get-maze-image?width=" + width + "&height=" + height;
        return ImageIO.read(new URL(urlString));
    }

    // פונקציה להמרת התמונה למערך בוליאני של מבוך
    public boolean[][] parseMaze(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        boolean[][] maze = new boolean[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = image.getRGB(i, j);
                // פיקסל לבן נחשב למעבר (true), אחרת זה קיר (false)
                maze[i][j] = (pixel == -1);
            }
        }
        return maze;
    }

    // פונקציית עזר לחילוץ ערכים מתוך ה-JSON
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