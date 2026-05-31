package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import dtos.ParkingResponse;

class ClientHandlerTest {

    @Test
    void testRunWritesSuccessfulResponse() throws Exception {
        Server server = mock(Server.class);
        when(server.handleClientStreams(any(), any())).thenReturn(new ParkingResponse(200, "ok"));

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getRemoteSocketAddress()).thenReturn(new InetSocketAddress("localhost", 1234));

        new ClientHandler(socket, server).run();

        String output = outputStream.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.contains("\"statusCode\":200"));
        assertTrue(output.contains("ok"));
        assertTrue(output.contains("handled in"));
        verify(server).handleClientStreams(any(), any());
        verify(socket).close();
    }

    @Test
    void testRunWritesErrorResponseWhenServerFails() throws Exception {
        Server server = mock(Server.class);
        when(server.handleClientStreams(any(), any())).thenThrow(new RuntimeException("boom"));

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getRemoteSocketAddress()).thenReturn(new InetSocketAddress("localhost", 1234));

        new ClientHandler(socket, server).run();

        String output = outputStream.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.contains("\"statusCode\":500"));
        assertTrue(output.contains("boom"));
        assertTrue(output.contains("handled in"));
        verify(socket).close();
    }
}
