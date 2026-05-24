package clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import dtos.ParkingRequest;
import dtos.ParkingResponse;

/*
 * ServerClient is a simple client that connects to the parking system server, sends commands, and prints responses.
 * Sending no command, List, or an unrecognized command will print currently registered commands.
 */
public class ServerClient {

    private static final Logger logger = Logger.getLogger(ServerClient.class.getName());

    private static final int PORT = 7777;
    private static final String SERVER = "localhost";

    private ServerClient() {
    }

    public static ParkingResponse runCommand(String command, Properties properties) throws IOException {
        InetAddress host = InetAddress.getByName(SERVER);
        try (Socket link = new Socket(host, PORT)) {
            logger.info("You are now connected to: " + host.getHostAddress());
            ParkingRequest request = new ParkingRequest(command, properties);
            OutputStream output = link.getOutputStream();
            output.write(request.toJson().getBytes(StandardCharsets.UTF_8));
            output.flush();
            link.shutdownOutput();
            return ParkingResponse.fromJson(readAll(link.getInputStream()));
        }
    }

    public static void main(String[] args) throws IOException {
    	String command;

        if (args.length == 0 || "LIST".equalsIgnoreCase(args[0])) {
        	command = "LIST";
        } else {
        	command = args[0].toUpperCase();
        }

        Properties properties = parseProperties(args);
        System.out.println("::: Command ::: " + command + " " + String.join(", ", Arrays.asList(args)));
        ParkingResponse response = runCommand(command, properties);
        printResponse(response);
    }

    private static Properties parseProperties(String[] args) {
        Properties properties = new Properties();
        for (int i = 1; i < args.length; i++) {
            String token = args[i];
            int split = token.indexOf('=');
            if (split <= 0) {
                continue;
            }
            String key = token.substring(0, split).trim().toLowerCase();
            String value = token.substring(split + 1).trim();
            properties.setProperty(key, value);
        }
        return properties;
    }

    private static void printResponse(ParkingResponse response) {
        String message = response.getMessage() == null ? "" : response.getMessage();
        for (String line : message.split("\\R")) {
            if (!line.trim().isEmpty()) {
                System.out.println(line);
            }
        }
    }

    private static String readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int read;
        while ((read = inputStream.read(data)) != -1) {
            buffer.write(data, 0, read);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }
}
