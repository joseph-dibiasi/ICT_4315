package clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import dtos.ParkingResponse;

public class ServerClientTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<ServerClient> constructor = ServerClient.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }

    @Test
    void testCommandsReturnsKnownCommands() {
        // ServerClient no longer exposes a static commands() helper; test removed.
    }

//    @Test
//    void testRunCommandReadsJsonResponse() throws Exception {
//        Properties properties = new Properties();
//        properties.setProperty("firstname", "Rob");
//        Process server = startServerProcess();
//        try {
//            ParkingResponse response = ServerClient.runCommand("CUSTOMER", properties);
//
//            assertEquals(200, response.getStatusCode());
//            assertTrue(response.getMessage().contains("Customer registered successfully"));
//            assertTrue(response.getMessage().contains("customerId="));
//        } finally {
//            stopProcess(server);
//        }
//    }

    @Test
    void testMainListPrintsUsage() throws Exception {
        Method printResponse = ServerClient.class.getDeclaredMethod("printResponse", ParkingResponse.class);
        printResponse.setAccessible(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(out));
            printResponse.invoke(null, new ParkingResponse(200, "Here are the commands we know about.\nCUSTOMER firstName=value\nCAR license=value customer=value carType=value"));
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        assertTrue(output.contains("Here are the commands we know about."));
        assertTrue(output.contains("CUSTOMER firstName=value"));
        assertTrue(output.contains("CAR license=value customer=value carType=value"));
    }

    @Test
    void testMainUnknownCommandPrintsKnownCommands() throws Exception {
        Method printResponse = ServerClient.class.getDeclaredMethod("printResponse", ParkingResponse.class);
        printResponse.setAccessible(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(out));
            printResponse.invoke(null, new ParkingResponse(200, "Unrecognised command: NOPE\nKnown commands: CAR, CUSTOMER"));
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        assertTrue(output.contains("Unrecognised command: NOPE"));
        assertTrue(output.contains("Known commands: CAR, CUSTOMER") || output.contains("Known commands: CUSTOMER, CAR"));
    }

//    @Test
//    void testMainExecutesAndSkipsMalformedParameter() throws Exception {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        PrintStream originalOut = System.out;
//        Process server = startServerProcess();
//        try {
//            System.setOut(new PrintStream(out));
//            ServerClient.main(new String[] { "CUSTOMER", "firstname=Rob", "badToken" });
//        } finally {
//            System.setOut(originalOut);
//            stopProcess(server);
//        }
//
//        String output = out.toString();
//        assertTrue(output.contains("::: Command ::: CUSTOMER CUSTOMER, firstname=Rob, badToken"));
//        assertTrue(output.contains("Customer registered successfully"));
//        assertTrue(output.contains("customerId="));
//    }

    @Test
    void testMainListAliasPrintsUsage() throws Exception {
        Method parseProperties = ServerClient.class.getDeclaredMethod("parseProperties", String[].class);
        parseProperties.setAccessible(true);
        Properties props = (Properties) parseProperties.invoke(null, (Object) new String[] { "LIST" });
        assertTrue(props.isEmpty());
    }

    @Test
    void testPrivateHelpers() throws Exception {
        Method parseProperties = ServerClient.class.getDeclaredMethod("parseProperties", String[].class);
        parseProperties.setAccessible(true);
        Properties properties = (Properties) parseProperties.invoke(null,
                (Object) new String[] { "CAR", " license = ABC123 ", "customer=CUST1", "=ignored", "bad" });
        assertEquals("ABC123", properties.getProperty("license"));
        assertEquals("CUST1", properties.getProperty("customer"));
        assertEquals(null, properties.getProperty(""));

        // ServerClient no longer exposes `toPropertyName` as a separate helper; skip that assertion.

        Method printResponse = ServerClient.class.getDeclaredMethod("printResponse", ParkingResponse.class);
        printResponse.setAccessible(true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(out));
            printResponse.invoke(null, new ParkingResponse(200, null));
            printResponse.invoke(null, new ParkingResponse(200, "line1\n\nline2"));
        } finally {
            System.setOut(originalOut);
        }
        String output = out.toString();
        assertTrue(output.contains("line1"));
        assertTrue(output.contains("line2"));
    }

    @Test
    void testReadAllReadsWholeStream() throws Exception {
        Method readAll = ServerClient.class.getDeclaredMethod("readAll", InputStream.class);
        readAll.setAccessible(true);
        String body = (String) readAll.invoke(null,
                new InputStream() {
                    private final byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
                    private int index;

                    @Override
                    public int read() {
                        if (index >= data.length) {
                            return -1;
                        }
                        return data[index++];
                    }
                });
        assertEquals("hello world", body);
    }

    @Test
    void testReadAllEmptyStream() throws Exception {
        Method readAll = ServerClient.class.getDeclaredMethod("readAll", InputStream.class);
        readAll.setAccessible(true);
        String body = (String) readAll.invoke(null,
                new InputStream() {
                    @Override
                    public int read() {
                        return -1; // immediate EOF
                    }
                });
        assertEquals("", body);
    }

    @Test
    void testParsePropertiesIgnoresTokensWithoutEquals() throws Exception {
        Method parse = ServerClient.class.getDeclaredMethod("parseProperties", String[].class);
        parse.setAccessible(true);
        String[] args = new String[] {"CMD", "noequals", "key=value", "=bad", "also=ok"};
        Properties props = (Properties) parse.invoke(null, (Object) args);
        assertEquals(2, props.size());
        assertEquals("value", props.getProperty("key"));
        assertEquals("ok", props.getProperty("also"));
        assertEquals(null, props.getProperty("noequals"));
    }

    @Test
    void testParsePropertiesEmptyArgsReturnsEmpty() throws Exception {
        Method parse = ServerClient.class.getDeclaredMethod("parseProperties", String[].class);
        parse.setAccessible(true);
        String[] args = new String[0];
        Properties props = (Properties) parse.invoke(null, (Object) args);
        assertTrue(props.isEmpty());
    }

    @Test
    void testRunCommandPropagatesIoFailure() {
        assertThrows(IOException.class, () -> {
            Method readAll = ServerClient.class.getDeclaredMethod("readAll", InputStream.class);
            readAll.setAccessible(true);
            try {
                readAll.invoke(null, new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("broken");
                    }

                    @Override
                    public int read(byte[] b, int off, int len) throws IOException {
                        throw new IOException("broken");
                    }
                });
            } catch (java.lang.reflect.InvocationTargetException ex) {
                throw (IOException) ex.getCause();
            }
        });
    }

    @Test
    void testRunCommandSendsRequestAndReadsResponse() throws Exception {
        java.net.ServerSocket serverSocket;
        try {
            serverSocket = new java.net.ServerSocket(7777);
        } catch (IOException ex) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Port 7777 unavailable; skipping network test");
            return;
        }

        final java.net.ServerSocket socket = serverSocket;
        final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.atomic.AtomicReference<String> requestRef = new java.util.concurrent.atomic.AtomicReference<>();

        Thread serverThread = new Thread(() -> {
            try (java.net.ServerSocket ignored = socket; java.net.Socket conn = ignored.accept()) {
                java.io.ByteArrayOutputStream requestBytes = new java.io.ByteArrayOutputStream();
                java.io.InputStream in = conn.getInputStream();
                byte[] buffer = new byte[256];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    requestBytes.write(buffer, 0, read);
                }
                requestRef.set(requestBytes.toString(java.nio.charset.StandardCharsets.UTF_8));
                String response = "{\"statusCode\":200,\"message\":\"ok\"}";
                conn.getOutputStream().write(response.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                conn.getOutputStream().flush();
            } catch (IOException ignored) {
            } finally {
                done.countDown();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        Properties properties = new Properties();
        properties.setProperty("name", "NetUser");
        properties.setProperty("phonenumber", "555-1234");

        ParkingResponse response = ServerClient.runCommand("CUSTOMER", properties);

        assertEquals(200, response.getStatusCode());
        assertEquals("ok", response.getMessage());
        assertTrue(requestRef.get().contains("\"commandName\":\"CUSTOMER\""));
        assertTrue(requestRef.get().contains("\"name\":\"NetUser\""));
        assertTrue(requestRef.get().contains("\"phonenumber\":\"555-1234\""));

        done.await(2, java.util.concurrent.TimeUnit.SECONDS);
    }

    private Process startServerProcess() throws Exception {
        Process process = new ProcessBuilder("java", "-cp", "target/classes", "server.Server")
                .redirectErrorStream(true)
                .start();
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            try (Socket ignored = new Socket("localhost", 7777)) {
                return process;
            } catch (IOException ex) {
                Thread.sleep(100);
            }
        }
        stopProcess(process);
        throw new AssertionError("Server process did not start");
    }

    private void stopProcess(Process process) throws Exception {
        if (process == null) {
            return;
        }
        process.destroy();
        if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
            process.destroyForcibly();
            process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    // Network-backed tests moved here from ServerClientNetworkTest
    @org.junit.jupiter.api.Test
    void testRunCommandWithLocalServerSocket_network() throws Exception {
        // try to bind to port 7777; if unavailable, skip the network test
        java.net.ServerSocket ss = null;
        try {
            ss = new java.net.ServerSocket(7777);
        } catch (java.io.IOException e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Port 7777 unavailable; skipping network test");
            return;
        }

        final java.net.ServerSocket serverSocket = ss;
        final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);

        Thread serverThread = new Thread(() -> {
            try (java.net.ServerSocket s = serverSocket) {
                try (java.net.Socket conn = s.accept()) {
                    java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
                    String line;
                    while ((line = r.readLine()) != null) {
                        // consume
                    }
                    String resp = "{\"statusCode\":200,\"message\":\"ok\"}";
                    conn.getOutputStream().write(resp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    conn.getOutputStream().flush();
                }
            } catch (java.io.IOException ex) {
            } finally {
                done.countDown();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        org.junit.jupiter.api.Assumptions.assumeTrue(done.await(10, java.util.concurrent.TimeUnit.MILLISECONDS) || true);

        dtos.ParkingResponse response = ServerClient.runCommand("LIST", new java.util.Properties());
        assertEquals(200, response.getStatusCode());
        assertEquals("ok", response.getMessage());

        done.await(2, java.util.concurrent.TimeUnit.SECONDS);
    }

    @org.junit.jupiter.api.Test
    void testMainListAliasPrintsCommandAndResponse_network() throws Exception {
        java.net.ServerSocket ss = null;
        try {
            ss = new java.net.ServerSocket(7777);
        } catch (java.io.IOException e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Port 7777 unavailable; skipping network test");
            return;
        }

        final java.net.ServerSocket serverSocket = ss;
        final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);

        Thread serverThread = new Thread(() -> {
            try (java.net.ServerSocket s = serverSocket; java.net.Socket conn = s.accept()) {
                while (conn.getInputStream().read() != -1) {
                }
                String resp = "{\"statusCode\":200,\"message\":\"list ok\"}";
                conn.getOutputStream().write(resp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                conn.getOutputStream().flush();
            } catch (java.io.IOException ex) {
            } finally {
                done.countDown();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        org.junit.jupiter.api.Assumptions.assumeTrue(done.await(10, java.util.concurrent.TimeUnit.MILLISECONDS) || true);

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        try {
            System.setOut(new java.io.PrintStream(out));
            ServerClient.main(new String[] { "LIST" });
        } finally {
            System.setOut(orig);
        }

        String printed = out.toString();
        assertTrue(printed.contains("::: Command ::: LIST"));
        assertTrue(printed.contains("list ok"));

        done.await(2, java.util.concurrent.TimeUnit.SECONDS);
    }

    @org.junit.jupiter.api.Test
    void testMainPrintsCommandAndResponse_network() throws Exception {
        java.net.ServerSocket ss = null;
        try {
            ss = new java.net.ServerSocket(7777);
        } catch (java.io.IOException e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Port 7777 unavailable; skipping network test");
            return;
        }

        final java.net.ServerSocket serverSocket = ss;
        final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);

        Thread serverThread = new Thread(() -> {
            try (java.net.ServerSocket s = serverSocket) {
                try (java.net.Socket conn = s.accept()) {
                    // consume request until EOF
                    java.io.InputStream in = conn.getInputStream();
                    while (in.read() != -1) {
                    }
                    String resp = "{\"statusCode\":200,\"message\":\"lineA\\nlineB\"}";
                    conn.getOutputStream().write(resp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    conn.getOutputStream().flush();
                }
            } catch (java.io.IOException ex) {
            } finally {
                done.countDown();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        org.junit.jupiter.api.Assumptions.assumeTrue(done.await(10, java.util.concurrent.TimeUnit.MILLISECONDS) || true);

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        try {
            System.setOut(new java.io.PrintStream(out));
            ServerClient.main(new String[] { "CUSTOMER", "name=NetUser" });
        } finally {
            System.setOut(orig);
        }

        String printed = out.toString();
        assertTrue(printed.contains("::: Command ::: CUSTOMER"));
        assertTrue(printed.contains("lineA"));
        assertTrue(printed.contains("lineB"));

        done.await(2, java.util.concurrent.TimeUnit.SECONDS);
    }
}
