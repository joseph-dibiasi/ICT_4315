package services;

import java.util.HashMap;
import java.util.Map;

import commands.Command;
import models.ParkingOffice;

public class ParkingService {

    private final ParkingOffice parkingOffice;

    private static Map<String, Command> commandMap = new HashMap<>();

    public ParkingService(ParkingOffice parkingOffice) {
        this.parkingOffice = parkingOffice;
    }

    public void register(Command command) {
        commandMap.put(command.getCommandName(), command);
    }

    public String performCommand(String name, String[] params) {
        Command command = commandMap.get(name);

        if (command == null) {
            return "Unknown command: " + name;
        }

        try {
            command.checkParameters(params);
            return command.execute(params);
        } catch (IllegalArgumentException e) {
            return "Invalid parameters: " + e.getMessage();
        }
    }
}
