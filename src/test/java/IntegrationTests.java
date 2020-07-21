import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;

import static org.junit.Assert.assertTrue;

public class IntegrationTests {
    // Your credentials here -- REMOVE BEFORE YOU PUSH TO GITHUB
    private String user = "xxxx";
    private String password = "xxxx";
    private String host_name = "babbage.cs.pdx.edu";
    private int port = 22;

    private Client client = new Client();

    public static final String TEST_DIRECTORY = "test_resources";
    public static final String TEST_DIRECTORY_PREFIX = "test_resources\\";


    @BeforeClass
    public static void setup() throws FileSystemException {
        File file = new File(IntegrationTests.TEST_DIRECTORY);
        boolean success = file.mkdir();
        if(!success) {
            throw new FileSystemException("Could not create test_resource directory");
        }
    }

    @AfterClass
    public static void cleanup() throws FileSystemException {
        File file = new File(IntegrationTests.TEST_DIRECTORY);
        boolean success = file.delete();
        if(!success) {
            throw new FileSystemException("Could not delete test_resource directory");
        }
    }

    @Test
    public void ListFilesTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect(user, password, host_name, port);
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
            client.connect(user, password, host_name, port);
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
            client.connect(user, password, host_name, port);
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

    @Test
    public void getNonexistentFileTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect(user, password, host_name, port);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            final String file_name = "nonexistent_file.txt";
            File tempFile = new File(TEST_DIRECTORY_PREFIX + file_name);
            client.getRemoteFile(file_name, TEST_DIRECTORY_PREFIX + file_name, sftp);
            assertTrue(!tempFile.exists());
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    public void getFileTest() throws IOException {
        final String file_name = ".dmrc";
        File tempFile = new File(TEST_DIRECTORY_PREFIX + file_name);

        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect(user, password, host_name, port);
            final SFTPClient sftp = client.getSshClient().newSFTPClient();

            client.getRemoteFile(file_name, TEST_DIRECTORY_PREFIX + file_name, sftp);
            assertTrue(tempFile.exists());
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
            tempFile.delete();
        }
    }
}

