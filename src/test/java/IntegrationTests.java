import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class IntegrationTests {
    private Client client = new Client();

    @Test
    public void ListFilesTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect("xxxx", "xxxx", "xxxx", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.listRemoteFiles(".", sftp);
        } finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    public void createDirectoryTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect("blah", "blah", "blah", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.makeDirectory("testDir", sftp);
            FileAttributes att = sftp.statExistence("testDir");
            assertTrue(att != null); // if the file exists, this att should not be null
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    public void createDirectoryWithPathTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect("blah", "blah", "blah", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.makeDirectoryWithPath("testDir/newDir", sftp);
            FileAttributes att = sftp.statExistence("testDir/newDir");
            assertTrue(att != null); // if the file exists, this att should not be null
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }
}

