package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import dtos.ParkingResponse;
import services.ParkingService;

public class ServerTest {

	@Test
	void testConstructorCreatesServer() throws Exception {
		Constructor<Server> constructor = Server.class.getDeclaredConstructor(ParkingService.class);
		assertNotNull(constructor.newInstance(mock(ParkingService.class)));
	}

	@Test
	void testStartServerAcceptsClientAndPropagatesTerminalIoException() throws Exception {
		ParkingService service = mock(ParkingService.class);
		services.ParkingOffice po = new services.ParkingOffice();
		po.setName("Test Office");
		po.setAddress(new models.Address.AddressBuilder().streetAddress1("1 Test").city("City").state("ST").zipCode("00000").build());
		when(service.getParkingOffice()).thenReturn(po);
		when(service.handleJsonInput(any())).thenReturn(new ParkingResponse(200, "ok"));

		Socket client = mock(Socket.class);
		when(client.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
		when(client.getOutputStream()).thenReturn(new ByteArrayOutputStream());

		try (MockedConstruction<ServerSocket> mocked = org.mockito.Mockito.mockConstruction(ServerSocket.class,
				(serverSocket, context) -> {
					doNothing().when(serverSocket).setReuseAddress(true);
					when(serverSocket.accept()).thenReturn(client).thenThrow(new java.io.IOException("stop"));
				})) {
			Server server = new Server(service);
			java.io.IOException exception = assertThrows(java.io.IOException.class, server::startServer);
			assertEquals("stop", exception.getMessage());
			assertEquals(1, mocked.constructed().size());
		}
	}

	@Test
	void testStartServerPropagatesAcceptFailure() throws Exception {
		ParkingService service = mock(ParkingService.class);
		services.ParkingOffice po = new services.ParkingOffice();
		po.setName("Test Office");
		po.setAddress(new models.Address.AddressBuilder().streetAddress1("1 Test").city("City").state("ST").zipCode("00000").build());
		when(service.getParkingOffice()).thenReturn(po);

		try (MockedConstruction<ServerSocket> mocked = org.mockito.Mockito.mockConstruction(ServerSocket.class,
				(serverSocket, context) -> {
					doNothing().when(serverSocket).setReuseAddress(true);
					when(serverSocket.accept()).thenThrow(new java.io.IOException("stop"));
				})) {
			Server server = new Server(service);
			java.io.IOException exception = assertThrows(java.io.IOException.class, server::startServer);
			assertEquals("stop", exception.getMessage());
			assertEquals(1, mocked.constructed().size());
		}
	}

	@Test
	void testHandleClientStreamsReturnsServiceResponse() {
		ParkingService service = mock(ParkingService.class);
		ParkingResponse expected = new ParkingResponse(200, "ok");
		when(service.handleJsonInput(any())).thenReturn(expected);

		Server server = new Server(service);
		ParkingResponse actual = server.handleClientStreams(
				new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)),
				new ByteArrayOutputStream());

		assertEquals(expected, actual);
	}

	@Test
	void testHandleClientStreamsFallsBackWhenServiceReturnsNull() {
		ParkingService service = mock(ParkingService.class);
		when(service.handleJsonInput(any())).thenReturn(null);

		Server server = new Server(service);
		ParkingResponse actual = server.handleClientStreams(
				new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)),
				new ByteArrayOutputStream());

		assertEquals(500, actual.getStatusCode());
		assertEquals("Server produced no response", actual.getMessage());
	}

	@Test
	void testStopServerClosesSocketAndWorkers() throws Exception {
		ParkingService service = mock(ParkingService.class);
		Server server = new Server(service);
		ServerSocket serverSocket = mock(ServerSocket.class);
		ExecutorService workers = mock(ExecutorService.class);

		setField(server, "serverSocket", serverSocket);
		setField(server, "workers", workers);
		setField(server, "running", true);

		server.stopServer();

		verify(serverSocket).close();
		verify(workers).shutdownNow();
	}

	@Test
	void testMainConstructsServerAndStartsIt() throws Exception {
		try (MockedConstruction<Server> mocked = org.mockito.Mockito.mockConstruction(Server.class,
				(server, context) -> doNothing().when(server).startServer())) {
			Server.main(new String[0]);
			assertEquals(1, mocked.constructed().size());
			verify(mocked.constructed().get(0)).startServer();
		}
	}

	private static void setField(Object target, String fieldName, Object value) throws Exception {
		Field field = Server.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

}
