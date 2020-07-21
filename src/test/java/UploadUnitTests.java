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
        SFTPClient sftp = null;

        try {
            final String username = "";
            final String password = "";
            final String hostname = "babbage.cs.pdx.edu";
            final int port = 22;
            final String src = System.getProperty("user.home") + File.separator + "Desktop"+ File.separator + "test";
            final String destination = System.getProperty("user.home") + File.separator + "ubuntu" + File.separator + "tmp";

            client.connect(username, password, hostname, port);

            sftp = client.getSshClient().newSFTPClient();
            client.uploadFile(src, sftp, destination);
            FileAttributes att = sftp.statExistence("test");
            assertTrue(att != null);
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
        }

    }
}