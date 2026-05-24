package commands;

import java.util.Properties;
import java.util.UUID;

import models.Address.AddressBuilder;
import models.Customer;
import models.Customer.CustomerBuilder;
import services.ParkingOffice;

/*
 * Command to register a new customer.
 * Required parameters: name, address, phoneNumber
 * CustomerID is generated automatically and is unique for each customer.
 * This ID can then be used to register cars.
 */
public class RegisterCustomerCommand implements Command {

    private ParkingOffice parkingOffice;

    public RegisterCustomerCommand(ParkingOffice parkingOffice) {
        this.parkingOffice = parkingOffice;
    }

    @Override
    public String getCommandName() {
        return "customer";
    }

    @Override
    public String getDisplayName() {
        return "Register Customer";
    }

    @Override
    public void checkParameters(Properties params) throws IllegalArgumentException {
        if (params == null || params.size() < 3) {
            throw new IllegalArgumentException("Missing required parameters");
        }
        if (parkingOffice.value(params, "name").isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: name");
        }
        if (parkingOffice.value(params, "address").isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: address");
        }
        if (parkingOffice.value(params, "phonenumber").isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: phoneNumber");
        }
    }

    @Override
    public String execute(Properties params) {
        String name = parkingOffice.value(params, "name");
        String addressText = parkingOffice.value(params, "address");
        String phoneNumber = parkingOffice.value(params, "phonenumber");

        AddressBuilder addressBuilder = new AddressBuilder();
        addressBuilder.streetAddress1(addressText);

        CustomerBuilder customerBuilder = new CustomerBuilder();
        customerBuilder.name(name);
        customerBuilder.address(addressBuilder.build());
        customerBuilder.phoneNumber(phoneNumber);
        Customer customer = customerBuilder.build();

        customer = this.parkingOffice.register(customer);

        StringBuilder sb = new StringBuilder();
        sb.append("Customer registered successfully:\n");
        sb.append("Name: ").append(customer.getName()).append("\n");
        sb.append("CustomerID: ").append(customer.getCustomerId()).append("\n");
        sb.append("Address: ").append(customer.getAddress().getStreetAddress1()).append("\n");
        sb.append("Phone Number: ").append(customer.getPhoneNumber()).append("\n");
        return sb.toString();
    }

}
