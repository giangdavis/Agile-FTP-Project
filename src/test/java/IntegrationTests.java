import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;

import static org.junit.Assert.assertTrue;

public class IntegrationTests {
    // You can put your credentials in here -- REMOVE THEM BEFORE PUSHING TO GITHUB
    // Or you can have a .env file with your credentials in it and the values in the .env file will be used
    private Credentials credentials = new Credentials("", "", "babbage.cs.pdx.edu", 22);


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
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            client.listRemoteFiles(".");
        } finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    public void createDirectoryTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
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
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
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
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final String file_name = "nonexistent_file.txt";
            File tempFile = new File(TEST_DIRECTORY_PREFIX + file_name);
            client.getRemoteFile(file_name, TEST_DIRECTORY_PREFIX + file_name);
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
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            client.getRemoteFile(file_name, TEST_DIRECTORY_PREFIX + file_name);
            assertTrue(tempFile.exists());
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
            tempFile.delete();
        }
    }

    @Test
    public void removeFileTest() throws IOException {
        try {
            // tested using babbage, insert your creds here for testing, REMEMBER TO REMOVE THEM BEFORE PUSHING TO GITHUB
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
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
            final String src = System.getProperty("user.home") + File.separator + "Desktop"+ File.separator + "test";
            final String destination = System.getProperty("user.home") + File.separator + "ubuntu" + File.separator + "tmp";

            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());

            sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.uploadFile(src, sftp, destination));
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
        }
    }

    @Test
    public void putMultipleTest() throws IOException {
        // To use these test files you would have to create files named "test" and "test2" on your desktop

        final String file = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "test";
        final String file2 = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "test2";
        final String destination = "INSERT_DESIRED_DESTINATION HERE";
        final String[] files = {file, file2};
        SFTPClient sftp = null;

        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.uploadMultipleFiles(files, sftp, destination));
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
        }
    }

    @Test
    public void deleteDirectoryTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.deleteDirectory("testDir", sftp));
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    public void logOffTest() {
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        boolean result = client.logoff();
        assertTrue(result);
    }

    // This test really doesn't need to connect to a SFTP server, this is purely just to write to a properties file
    // Usage of the saveConnectionInformation() method would be just passing in the connection details a user enters
    @Test
    public void saveConnectionTest() throws IOException {
        client.saveConnectionInformation("est29", "test2", "babbage.cs.pdx.edu.pdx.jasdlasj", "test3");
    }
}

