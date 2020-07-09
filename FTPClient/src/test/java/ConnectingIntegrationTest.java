import Client.Client;
import org.junit.Test;

import java.io.IOException;

public class ConnectingIntegrationTest {
    private MockServer server;
    private Client client;

    @Test
    public void test() throws IOException {
        server = new MockServer();
        client = new Client();
        server.createAndStartServer();
        client.connect("test", "test", "localhost", 22);
    }
}
