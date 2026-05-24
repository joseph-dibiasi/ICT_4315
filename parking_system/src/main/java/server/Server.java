package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import dtos.ParkingResponse;
import models.Address;
import services.ParkingOffice;
import services.ParkingService;

public class Server {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tc %4$-7s (%2$s) %5$s %6$s%n");
    }

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 7777;

    private final ParkingService service;

    public Server(ParkingService service) {
        this.service = service;
    }

    public void startServer() throws IOException {
        logger.setLevel(Level.FINE);
        logger.info("Starting server: " + InetAddress.getLocalHost().getHostAddress());
        logger.info("Parking Office " + service.getParkingOffice().getName() + " is open for business.");
        logger.info("Address: " + service.getParkingOffice().getAddress().getAddressInfo());
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket client = serverSocket.accept();
                handleClient(client);
            }
        }
    }

    private void handleClient(Socket client) {
        try {
            ParkingResponse response = service.handleJsonInput(client.getInputStream());
            writeAll(client.getOutputStream(), response.toJson());
        } catch (RuntimeException | IOException ex) {
            try {
                writeAll(client.getOutputStream(), new ParkingResponse(500, ex.getMessage()).toJson());
            } catch (IOException ignored) {
            }
        } finally {
            try {
                client.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Failed to close client socket.", ex);
            }
        }
    }

    private void writeAll(OutputStream outputStream, String body) throws IOException {
        outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public static void main(String[] args) throws Exception {
        ParkingOffice parkingOffice = new ParkingOffice();
        parkingOffice.setName("Parking Office");

        Address.AddressBuilder addressBuilder = new Address.AddressBuilder();
        addressBuilder.streetAddress1("2199 S. University Blvd").city("Denver").state("CO").zipCode("80208");
        parkingOffice.setAddress(addressBuilder.build());
        
        new Server(new ParkingService(parkingOffice).registerDefaultCommands()).startServer();
    }
}
