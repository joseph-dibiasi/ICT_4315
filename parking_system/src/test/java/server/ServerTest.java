package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
	void testHandleClientWritesSuccessResponseAndClosesSocket() throws Exception {
		ParkingService service = mock(ParkingService.class);
		ParkingResponse response = new ParkingResponse(200, "ok");
		when(service.handleJsonInput(org.mockito.ArgumentMatchers.any())).thenReturn(response);

		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		Server server = new Server(service);
		Method handleClient = Server.class.getDeclaredMethod("handleClient", Socket.class);
		handleClient.setAccessible(true);
		handleClient.invoke(server, socket);

		assertEquals(response.toJson(), outputStream.toString(StandardCharsets.UTF_8.name()));
		verify(socket).close();
	}

	@Test
	void testHandleClientWritesFailureResponse() throws Exception {
		ParkingService service = mock(ParkingService.class);
		when(service.handleJsonInput(org.mockito.ArgumentMatchers.any())).thenThrow(new RuntimeException("boom"));

		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		Server server = new Server(service);
		Method handleClient = Server.class.getDeclaredMethod("handleClient", Socket.class);
		handleClient.setAccessible(true);
		handleClient.invoke(server, socket);

		ParkingResponse response = ParkingResponse.fromJson(outputStream.toString(StandardCharsets.UTF_8.name()));
		assertEquals(500, response.getStatusCode());
		assertEquals("boom", response.getMessage());
		verify(socket).close();
	}

	@Test
	void testHandleClientHandlesCloseFailure() throws Exception {
		ParkingService service = mock(ParkingService.class);
		when(service.handleJsonInput(org.mockito.ArgumentMatchers.any())).thenReturn(new ParkingResponse(200, "ok"));

		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		doThrow(new java.io.IOException("close failed")).when(socket).close();

		Server server = new Server(service);
		Method handleClient = Server.class.getDeclaredMethod("handleClient", Socket.class);
		handleClient.setAccessible(true);
		handleClient.invoke(server, socket);
	}

	@Test
	void testHandleClientHandlesFailureWhileWritingErrorResponse() throws Exception {
		ParkingService service = mock(ParkingService.class);
		when(service.handleJsonInput(any())).thenThrow(new RuntimeException("boom"));

		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
		when(socket.getOutputStream()).thenThrow(new java.io.IOException("write failed"));

		Server server = new Server(service);
		Method handleClient = Server.class.getDeclaredMethod("handleClient", Socket.class);
		handleClient.setAccessible(true);
		handleClient.invoke(server, socket);

		verify(socket).close();
	}

	@Test
	void testWriteAllWritesBytes() throws Exception {
		Server server = new Server(mock(ParkingService.class));
		Method writeAll = Server.class.getDeclaredMethod("writeAll", OutputStream.class, String.class);
		writeAll.setAccessible(true);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		writeAll.invoke(server, outputStream, "hello");

		assertEquals("hello", outputStream.toString(StandardCharsets.UTF_8.name()));
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
	void testStartServerPropagatesAcceptFailureWhenCloseAlsoFails() throws Exception {
		ParkingService service = mock(ParkingService.class);
		services.ParkingOffice po = new services.ParkingOffice();
		po.setName("Test Office");
		po.setAddress(new models.Address.AddressBuilder().streetAddress1("1 Test").city("City").state("ST").zipCode("00000").build());
		when(service.getParkingOffice()).thenReturn(po);

		try (MockedConstruction<ServerSocket> mocked = org.mockito.Mockito.mockConstruction(ServerSocket.class,
				(serverSocket, context) -> {
					doNothing().when(serverSocket).setReuseAddress(true);
					when(serverSocket.accept()).thenThrow(new java.io.IOException("stop"));
					doThrow(new java.io.IOException("close failed")).when(serverSocket).close();
				})) {
			Server server = new Server(service);
			java.io.IOException exception = assertThrows(java.io.IOException.class, server::startServer);
			assertEquals("stop", exception.getMessage());
			assertEquals(1, exception.getSuppressed().length);
			assertEquals("close failed", exception.getSuppressed()[0].getMessage());
			assertEquals(1, mocked.constructed().size());
		}
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

}
