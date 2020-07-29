import Client.Client;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.userauth.UserAuthException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    public void testConnect() {
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
        // Arrange
        doThrow(new UserAuthException("Invalid credentials")).when(sshClient).authPassword(anyString(), anyString());

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

    @Test
    public void testGetRemoteFile() throws IOException {
        // Arrange
        when(sshClient.isConnected()).thenReturn(true);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() {
                return sshClient;
            }

            @Override
            protected SFTPClient createSFTPClient() {
                return sftpClient;
            }
        };

        // Act
        boolean result = client.getRemoteFile("", "");

        // Verify
        assertTrue(result);
    }

    @Test
    public void testGetRemoteFile_SSHClientNotConnected() throws IOException {
        // Arrange
        when(sshClient.isConnected()).thenReturn(false);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() {
                return sshClient;
            }

            @Override
            protected SFTPClient createSFTPClient() {
                return sftpClient;
            }
        };

        // Act
        boolean result = client.getRemoteFile("", "");

        // Verify
        assertFalse(result);
    }

    @Test
    public void testGetRemoteFile_SFTPClientFailedToGetFile() throws IOException {
        // Arrange
        when(sshClient.isConnected()).thenReturn(true);
        doThrow(new IOException()).when(sftpClient).get(anyString(), anyString());

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() {
                return sshClient;
            }

            @Override
            protected SFTPClient createSFTPClient() {
                return sftpClient;
            }
        };

        // Act
        boolean result = client.getRemoteFile("", "");

        // Verify
        assertFalse(result);
    }

    @Test
    public void testPutMultipleFiles() throws IOException {
        // Arrange
        when(sshClient.isConnected()).thenReturn(true);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() {
                return sshClient;
            }

            @Override
            protected SFTPClient createSFTPClient() {
                return sftpClient;
            }
        };

        // Act
        String[] arr = {"t1","t2"};
        boolean result = client.uploadMultipleFiles(arr, sftpClient, "something/");

        // Verify
        assertTrue(result);
    }

    @Test
    public void testPutMultipleFiles_SSHClientNotConnected() throws IOException {
        when(sshClient.isConnected()).thenReturn(false);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() {
                return sshClient;
            }

            @Override
            protected SFTPClient createSFTPClient() {
                return sftpClient;
            }
        };

        // Act
        String[] arr = {"t1","t2"};
        boolean result = client.uploadMultipleFiles(arr, sftpClient, "something/");

        // Verify
        assertFalse(result);
    }

    @Test
    public void testLogOff_Successful() {
        // Arrange
        when(sshClient.isConnected()).thenReturn(true);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() { return sshClient; }
        };

        // Act
        boolean result = client.logoff();

        // Verify
        assertTrue(result);
    }

    @Test
    public void testLogOff_Failure() {
        // Arrange
        when(sshClient.isConnected()).thenReturn(true);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() { return sshClient; }
        };

        // Act
        boolean result = client.logoff();

        // Verify
        assertTrue(result);
    }

    @Test
    public void testLogOff_SSHClientNotConnected() {
        // Arrange
        when(sshClient.isConnected()).thenReturn(false);

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() { return sshClient; }
        };

        // Act
        boolean result = client.logoff();

        // Verify
        assertTrue(result);
    }

    @Test
    public void testLogOff_ErrorWhileDisconnecting() throws IOException {
        // Arrange
        when(sshClient.isConnected()).thenReturn(true);
        doThrow(new IOException()).when(sshClient).disconnect();

        Client client = new Client() {
            @Override
            public SSHClient getSshClient() { return sshClient; }
        };

        // Act
        boolean result = client.logoff();

        // Verify
        assertFalse(result);
    }

    @Test
    public void testRenameFile_FileDoesNotExist() throws IOException {
        Client client = new Client();

        assertFalse(client.renameLocalFile("path","t1", "t2"));
    }
}
