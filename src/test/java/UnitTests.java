import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class UnitTests {
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
            client.connect("blah", "blah", "babbage.cs.pdx.edu", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.makeDirectory("testDir", sftp));
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
            client.connect("blah", "blah", "babbage.cs.pdx.edu", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.makeDirectoryWithPath("testDir/newDir", sftp));
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    public void removeFileTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect("blah", "blah", "babbage.cs.pdx.edu", 22);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.removeFile("testDir/testfile.txt", sftp));
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

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
            assertTrue(client.uploadFile(src, sftp, destination));
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
        }

    }
}

