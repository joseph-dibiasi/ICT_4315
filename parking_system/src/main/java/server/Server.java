package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import dtos.ParkingResponse;
import models.Address;
import services.ParkingOffice;
import services.ParkingService;

/*
 * Server is a simple multi-threaded TCP server that listens for client connections, processes parking requests, and returns responses.
 * It uses a thread pool to handle multiple clients concurrently and delegates request processing to the ParkingService via the handleClientStreams method.
 */
public class Server {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tc %4$-7s (%2$s) %5$s %6$s%n");
    }

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 7777;

    private final ParkingService service;
    private ServerSocket serverSocket;
    private ExecutorService workers;
    private volatile boolean running = false;

    public Server(ParkingService service) {
        this.service = service;
    }

    public void startServer() throws IOException {
        logger.setLevel(Level.FINE);
        logger.info("Starting server: " + InetAddress.getLocalHost().getHostAddress());
        logger.info("Parking Office " + service.getParkingOffice().getName() + " is open for business.");
        logger.info("Address: " + service.getParkingOffice().getAddress().getAddressInfo());
        int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
        this.workers = Executors.newFixedThreadPool(poolSize);
        this.serverSocket = new ServerSocket(PORT);
        this.serverSocket.setReuseAddress(true);
        this.running = true;
        logger.info("Using thread pool with " + poolSize + " threads");
        try {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    // hand off to a worker
                    workers.submit(new ClientHandler(client, this));
                } catch (SocketException se) {
                    if (!running) {
                        break;
                    }
                    throw se;
                }
            }
        } finally {
            if (workers != null) {
                workers.shutdown();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing server socket", e);
                }
            }
        }
    }

    /**
     * Handle a client given its input and output streams. This method delegates
     * to the ParkingService to parse and execute the request and returns the
     * resulting ParkingResponse. 
     */
    public ParkingResponse handleClientStreams(InputStream inputStream, OutputStream outputStream) {
        ParkingResponse response = service.handleJsonInput(inputStream);
        if (response == null) {
            return new ParkingResponse(500, "Server produced no response");
        }
        return response;
    }

    public static void main(String[] args) throws Exception {
        ParkingOffice parkingOffice = new ParkingOffice();
        parkingOffice.setName("Parking Office");

        Address.AddressBuilder addressBuilder = new Address.AddressBuilder();
        addressBuilder.streetAddress1("2199 S. University Blvd").city("Denver").state("CO").zipCode("80208");
        parkingOffice.setAddress(addressBuilder.build());
        
        new Server(new ParkingService(parkingOffice).registerDefaultCommands()).startServer();
    }

    /**
     * Stop the server: stop accepting new connections and shutdown workers.
     */
    public void stopServer() {
        this.running = false;
        try {
            if (this.serverSocket != null && !this.serverSocket.isClosed()) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception while closing server socket", e);
        }
        if (this.workers != null) {
            this.workers.shutdownNow();
        }
    }
}
