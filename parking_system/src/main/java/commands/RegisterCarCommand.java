package commands;

import java.util.Properties;
import java.util.UUID;

import enums.CarType;
import models.Car;
import services.ParkingOffice;

/*
 * Command to register a new car for a customer.
 * Required parameters: ownerid (UUID), license (String), cartype (SUV or COMPACT)
 * Owner must already be registered in the system before registering a car.
 */
public class RegisterCarCommand implements Command {
    
    private ParkingOffice parkingOffice;

    public RegisterCarCommand(ParkingOffice parkingOffice) {
        this.parkingOffice = parkingOffice;
    }

    @Override
    public String getCommandName() {
        return "car";
    }

    @Override
    public String getDisplayName() {
        return "Register Car";
    }
    
    @Override
    public void checkParameters(Properties params) throws IllegalArgumentException {
        if (params == null || params.size() < 2) {
            throw new IllegalArgumentException("Missing required parameters");
        }
        
        if (parkingOffice.value(params, "ownerid").isBlank()) {
        	throw new IllegalArgumentException("Missing required parameter: ownerid");
        }
        if (parkingOffice.value(params, "license").isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: license");
        }
        if (parkingOffice.value(params, "cartype").isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: cartype");
        }
    }

    @Override
    public String execute(Properties params) {
        UUID ownerId;
        try {
            ownerId = UUID.fromString(parkingOffice.value(params, "ownerid"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ownerid format");
        }

        String license = parkingOffice.value(params, "license");
        CarType carType;
        try {
            carType = CarType.valueOf(parkingOffice.value(params, "cartype").toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Car Type. Valid types are SUV or COMPACT.");
        }
        
        Car car = new Car();
        car.setOwner(ownerId);
        car.setLicense(license);
        car.setType(carType);
        
        this.parkingOffice.register(car);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Car registered successfully:\n");
        sb.append("Owner ID: ").append(car.getOwner()).append("\n");
        sb.append("License: ").append(car.getLicense()).append("\n");
        sb.append("Car Type: ").append(car.getType()).append("\n");
        return sb.toString();    }
}
