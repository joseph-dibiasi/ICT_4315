package commands;

import java.util.UUID;

import models.Car;
import models.CarType;
import models.ParkingOffice;

public class RegisterCarCommand implements Command {
    
    private ParkingOffice parkingOffice;

    public RegisterCarCommand(ParkingOffice parkingOffice) {
        this.parkingOffice = parkingOffice;
    }

    @Override
    public String getCommandName() {
        return "registerCar";
    }

    @Override
    public String getDisplayName() {
        return "Register Car";
    }
    
    @Override
    public void checkParameters(String[] params) throws IllegalArgumentException {
        if (params == null || params.length < 3) {
            throw new IllegalArgumentException("Missing required parameter: ownerId, license, or carType");
        }
        if (params[0] == null || params[0].isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: ownerId");
        }
        if (params[1] == null || params[1].isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: license");
        }
        if (params[2] == null || params[2].isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: carType");
        }
    }

    @Override
    public String execute(String[] params) {
        UUID ownerId;
        try {
            ownerId = UUID.fromString(params[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ownerId format");
        }

        String license = params[1];
        CarType carType;
        try {
            carType = CarType.valueOf(params[2].toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid carType");
        }
        
        Car car = new Car();
        car.setOwner(ownerId);
        car.setLicense(license);
        car.setType(carType);
        
        this.parkingOffice.register(car);
        
        return "Car registered successfully";
    }
}
