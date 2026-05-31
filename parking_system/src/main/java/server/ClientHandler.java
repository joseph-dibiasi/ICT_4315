package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import dtos.ParkingResponse;

/*
 * ClientHandler is a Runnable that processes a single client connection to the server.
 * It reads the client's request, delegates processing to the ParkingService, and sends back a response.
 * It also logs the time taken to handle the request and any errors that occur during processing.
 * This was broken out into a separate class to keep the Server class focused on accepting connections and managing the thread pool, 
 * while ClientHandler focuses on the client interaction and request processing.	
 */
public class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private final Socket client;
    private final Server server;

    public ClientHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try (Socket c = this.client) {
            ParkingResponse response = server.handleClientStreams(c.getInputStream(), c.getOutputStream());
            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000;
            if (response == null) {
                response = new ParkingResponse(500, "Server produced no response");
            }
            String timedMessage = (response.getMessage() == null ? "" : response.getMessage()) + " (handled in " + durationMs + " ms)";
            response.setMessage(timedMessage);
            writeAll(c.getOutputStream(), response.toJson());
            logger.info(String.format("Handled client %s in %d ms", client.getRemoteSocketAddress(), durationMs));
        } catch (RuntimeException | IOException ex) {
            logger.log(Level.WARNING, "Error handling client: " + client.getRemoteSocketAddress(), ex);
            try {
                long end = System.nanoTime();
                long durationMs = (end - start) / 1_000_000;
                ParkingResponse err = new ParkingResponse(500, ex.getMessage() + " (handled in " + durationMs + " ms)");
                writeAll(client.getOutputStream(), err.toJson());
            } catch (IOException ignored) {
            }
        }
    }

    private void writeAll(OutputStream outputStream, String body) throws IOException {
        outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

}
