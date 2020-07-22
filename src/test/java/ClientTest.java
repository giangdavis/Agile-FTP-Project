import Client.Client;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.userauth.UserAuthException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ClientTest {
    @Mock
    private SSHClient sshClient;

    @Mock
    private SFTPClient sftpClient;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConnect() throws IOException {
        // Arrange
        Client client = new Client() {
            @Override
            protected SSHClient createSSHClient() {
                return sshClient;
            }
        };

        // Act
        boolean result = client.connect("", "", "", 22);

        // Verify
        assertTrue(result);
    }

    @Test
    public void testConnect_UserAuthFails() throws IOException {
        doThrow(new UserAuthException("Invalid credentials")).when(sshClient).authPassword(anyString(), anyString());

        // Arrange
        Client client = new Client() {
            @Override
            protected SSHClient createSSHClient() {
                return sshClient;
            }
        };

        // Act
        boolean result = client.connect("", "", "", 22);

        // Verify
        assertFalse(result);
    }
}
