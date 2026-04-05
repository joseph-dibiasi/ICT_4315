package commands;

import models.Address;
import models.Customer;
import models.ParkingOffice;

public class RegisterCustomerCommand implements Command {

    private ParkingOffice parkingOffice;

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
    public void checkParameters(String[] params) throws IllegalArgumentException {
        if (params == null || params.length < 3) {
            throw new IllegalArgumentException("Missing required parameter: name, address, or phoneNumber");
        }
        if (params[0] == null || params[0].isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: name");
        }
        if (params[1] == null || params[1].isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: address");
        }
        if (params[2] == null || params[2].isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: phoneNumber");
        }
    }

    @Override
    public String execute(String[] params) {
        String name = params[0];
        String addressText = params[1];
        String phoneNumber = params[2];

        Address address = new Address();
        address.setStreetAddress1(addressText);

        Customer customer = new Customer();
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhoneNumber(phoneNumber);

        this.parkingOffice.register(customer);

        return "Customer registered successfully";
    }

}
