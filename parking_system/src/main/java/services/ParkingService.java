package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Logger;

import commands.Command;
import dtos.ParkingRequest;
import dtos.ParkingResponse;

/*
 * ParkingService is the core service class that processes parking-related commands and manages the ParkingOffice.
 * It maintains a registry of available commands and their implementations, allowing for dynamic command handling.
 * The handleJsonInput method reads incoming JSON requests, parses them into ParkingRequest objects, and executes the corresponding commands.
 * The performCommand method looks up the command by name, checks parameters, and executes it, returning a ParkingResponse with the result or error message.
 * This design allows for easy extension by simply implementing new Command classes and registering them with the service.
 */
public class ParkingService {

	private final ParkingOffice parkingOffice;

	private final java.util.concurrent.ConcurrentMap<String, Command> commandMap = new java.util.concurrent.ConcurrentHashMap<>();
	private static final Logger logger = Logger.getLogger(ParkingService.class.getName());
 
	public ParkingService(ParkingOffice parkingOffice) {
		this.parkingOffice = parkingOffice;
	}
	
	public ParkingService registerDefaultCommands() {
		register(new commands.RegisterCustomerCommand(parkingOffice));
		register(new commands.RegisterCarCommand(parkingOffice));
		return this;
	}
	

	public void register(Command command) {
		commandMap.put(command.getCommandName().toUpperCase(), command);
	}

	public ParkingResponse handleJsonInput(InputStream inputStream) {
		String raw = readAll(inputStream);
		logger.info("Received request: " + (raw == null ? "<null>" : raw));
		ParkingRequest request = ParkingRequest.fromJson(raw);
		if (request == null) {
			return new ParkingResponse(400, "Invalid or empty request");
		}
		return performCommand(request.getCommandName(), request.getProperties());
	}

	private String readAll(InputStream inputStream) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int read;
			while ((read = inputStream.read(data)) != -1) {
				buffer.write(data, 0, read);
			}
			return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read request", ex);
		}
	}

	public ParkingResponse performCommand(String commandName, Properties params) {
		Command command = commandMap.get(commandName.toUpperCase());
		if (command == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Invalid Command: ").append(commandName).append("\n");
			sb.append("Known commands: ").append(String.join(", ", commandMap.keySet()));
			return new ParkingResponse(400, sb.toString());
		}

		try {
			command.checkParameters(params);
			return new ParkingResponse(200, command.execute(params));
		} catch (IllegalArgumentException e) {
			return new ParkingResponse(400, e.getMessage());
		}
	}

	public ParkingOffice getParkingOffice() {
		return parkingOffice;
	}


}
