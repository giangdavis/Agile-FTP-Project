import Client.Client;
import net.schmizz.sshj.SSHClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class ClientTest {

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConnect() throws IOException {
//        when(client.connect(anyString(), anyString(), anyString(), anyInt()))
//            .thenReturn(new SSHClient());

        final SSHClient sshClient = mock(SSHClient.class);

        doNothing().when(sshClient).loadKnownHosts();
        doNothing().when(sshClient).connect(anyString(), anyInt());
        doNothing().when(sshClient).authPassword(anyString(), anyString());
//        Client c = new Client() {
//            @Override
//            protected SSHClient createSSHClient() {
//                return sshClient;
//            }
//        };

//        c.connect("1", "2", "3", 4);

//        verify(client).connect("1", "2", "3", 4);

        // Make the ssh.connect a thing in the interface, then use 'when' on like client.connect() to return the ssh
    }
}
