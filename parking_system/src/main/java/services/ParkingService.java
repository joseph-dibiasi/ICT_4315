package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import commands.Command;
import dtos.ParkingRequest;
import dtos.ParkingResponse;

public class ParkingService {

	private final ParkingOffice parkingOffice;

	private static Map<String, Command> commandMap = new HashMap<>();
 
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
		ParkingRequest request = ParkingRequest.fromJson(readAll(inputStream));
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
