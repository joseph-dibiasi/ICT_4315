package commands;

import java.util.Properties;
import java.util.UUID;

import models.Car;
import models.CarType;
import models.ParkingOffice;

public class RegisterCarCommand implements Command {
	
    private ParkingOffice parkingOffice;
    
    // Command-specific parameter keys
    private static final String PARAM_OWNER_ID = "ownerId";
    private static final String PARAM_LICENSE = "license";
    private static final String PARAM_CAR_TYPE = "carType";

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
    public void checkParameters(Properties params) throws IllegalArgumentException {
    	 if (!params.containsKey(PARAM_OWNER_ID)) {
    	 	throw new IllegalArgumentException("Missing required parameter: ownerId");
    	 }
    	 if (!params.containsKey(PARAM_LICENSE) || params.getProperty(PARAM_LICENSE).isBlank()) {
    	 	throw new IllegalArgumentException("Missing required parameter: license");
    	 }
    	 if (!params.containsKey(PARAM_CAR_TYPE)) {
    	 	throw new IllegalArgumentException("Missing required parameter: carType");
    	 }
    }

	@Override
	public String execute(Properties params) {
		UUID ownerId = (UUID) params.get(PARAM_OWNER_ID);
		String license = params.getProperty(PARAM_LICENSE);
		CarType carType = (CarType) params.get(PARAM_CAR_TYPE);
		
		Car car = new Car();
		car.setOwner(ownerId);
		car.setLicense(license);
		car.setType(carType);
		
		this.parkingOffice.register(car);
		
		return "Car registered successfully";
	}

}
