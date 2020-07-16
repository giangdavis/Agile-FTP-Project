import Client.Client;

import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.FileAttributes;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;



public class UploadUnitTests {
    private Client client = new Client();

    @Test
    public void createUploadTest() throws IOException {
        try {
            final String username = "giang";
            final String password = "hg9e-fM5de";
            final String hostname = "babbage.cs.pdx.edu";
            final int port = 22;
            final String src = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "test";

            client.connect(username, password, hostname, port);

            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.uploadFile(src, sftp);
            FileAttributes att = sftp.statExistence("test");
            assertTrue(att != null);
        }
        finally {
            sftp.close();
            client.disconnect();
            System.out.println("disconnected");
        }
    }
}