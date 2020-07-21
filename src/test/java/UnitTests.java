import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class UnitTests {
    private Client client = new Client();


    @Test
    public void ListFilesTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect("xxxx", "xxxx", "xxxxx", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.listRemoteFiles(".", sftp);
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }


    }
<<<<<<< Updated upstream
=======

    @Test
    public void ListFilesTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect("xxxx", "xxxx", "xxxx", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.listRemoteFiles(".", sftp);
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }
}
>>>>>>> Stashed changes
