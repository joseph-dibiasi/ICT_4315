package server;

import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import clients.ServerClient;
import dtos.ParkingResponse;
import services.ParkingOffice;
import services.ParkingService;

public class ServerIntegrationTest {

    @Test
    public void concurrentClientsRegisterCustomers() throws Exception {
        ParkingOffice office = new ParkingOffice();
        office.setName("Test Office");
        office.setAddress(new models.Address.AddressBuilder().streetAddress1("1 Test").city("City").state("ST").zipCode("00000").build());
        ParkingService service = new ParkingService(office).registerDefaultCommands();
        Server server = new Server(service);

        ExecutorService serverExec = Executors.newSingleThreadExecutor();
        Future<?> serverFuture = serverExec.submit(() -> {
            try {
                server.startServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // wait for server to start accepting connections
        long startWait = System.currentTimeMillis();
        boolean listening = false;
        while (System.currentTimeMillis() - startWait < 5000) {
            try (java.net.Socket s = new java.net.Socket("127.0.0.1", 7777)) {
                listening = true;
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
        if (!listening) {
            // give up — fail fast with clear message
            throw new IllegalStateException("Server did not start listening on port 7777");
        }

        int clients = 10;
        ExecutorService clientsExec = Executors.newFixedThreadPool(clients);
        CompletionService<ParkingResponse> cs = new ExecutorCompletionService<>(clientsExec);

        for (int i = 0; i < clients; i++) {
            final int idx = i;
            cs.submit(() -> {
                Properties props = new Properties();
                props.setProperty("name", "Test" + idx);
                props.setProperty("address", "addr");
                props.setProperty("phonenumber", "3030000" + idx);
                return ServerClient.runCommand("CUSTOMER", props);
            });
        }

        for (int i = 0; i < clients; i++) {
            Future<ParkingResponse> f = cs.take();
            ParkingResponse resp = f.get(5, TimeUnit.SECONDS);
            assertNotNull(resp);
        }

        clientsExec.shutdownNow();

        // stop server
        server.stopServer();
        serverExec.shutdownNow();
    }
}
