import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serving static files from "public" folder
        server.createContext("/", new StaticFileHandler("public"));
        server.setExecutor(null);

        System.out.println("Server started at http://localhost:" + port);
        server.start();
    }
}

class StaticFileHandler implements HttpHandler {
    private final String directory;

    public StaticFileHandler(String directory) {
        this.directory = directory;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html"; // Default page

        java.nio.file.Path filePath = Paths.get(directory + path);
        if (Files.exists(filePath)) {
            byte[] fileBytes = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, fileBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        } else {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
