package commands;

import java.util.Properties;

import models.Address;
import models.Customer;
import models.ParkingOffice;

public class RegisterCustomerCommand implements Command {

	private ParkingOffice parkingOffice;
	
	// Command-specific parameter keys
	private static final String PARAM_NAME = "name";
	private static final String PARAM_ADDRESS = "address";
	private static final String PARAM_PHONE = "phoneNumber";

	public RegisterCustomerCommand(ParkingOffice parkingOffice) {
        this.parkingOffice = parkingOffice;
    }

    @Override
    public String getCommandName() {
        return "registerCustomer";
    }

    @Override
    public String getDisplayName() {
        return "Register Customer";
    }
	
	@Override
	public void checkParameters(Properties params) throws IllegalArgumentException {
		if (!params.containsKey(PARAM_NAME) || params.getProperty(PARAM_NAME).isBlank()) {
			throw new IllegalArgumentException("Missing required parameter: name");
		}
		if (!params.containsKey(PARAM_ADDRESS)) {
			throw new IllegalArgumentException("Missing required parameter: address");
		}
		if (!params.containsKey(PARAM_PHONE) || params.getProperty(PARAM_PHONE).isBlank()) {
			throw new IllegalArgumentException("Missing required parameter: phoneNumber");
		}
	}

	@Override
	public String execute(Properties params) {
		String name = params.getProperty(PARAM_NAME);
		Address address = (Address) params.get(PARAM_ADDRESS);
		String phoneNumber = params.getProperty(PARAM_PHONE);
        
		Customer customer = new Customer();
		customer.setName(name);
		customer.setAddress(address);
		customer.setPhoneNumber(phoneNumber);
		
		this.parkingOffice.register(customer);
        
		return "Customer registered successfully";
	}


}
