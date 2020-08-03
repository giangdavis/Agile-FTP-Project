import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {
    // You can put your credentials in here -- REMOVE THEM BEFORE PUSHING TO GITHUB
    // Or you can have a .env file with your credentials in it and the values in the .env file will be used
    private Credentials credentials = new Credentials(null, null, null, 22);

    private Client client = new Client();

    public static final String REMOTE_TEST_DIRECTORY = "test_directory";
    public static final String REMOTE_NESTED_TEST_DIRECTORY = REMOTE_TEST_DIRECTORY + "/nested_test_directory";
    public static final String LOCAL_TEST_DIRECTORY = "test_resources";
    public static final String LOCAL_TEST_DIRECTORY_PREFIX = "test_resources" + File.separator;
    public static final String TEST_FILE_ONE = "test_file_1";
    public static final String TEST_FILE_TWO = "test_file_2";

    @BeforeAll
    public static void setup() throws IOException {
        File dir = new File(IntegrationTests.LOCAL_TEST_DIRECTORY);

        // Create a local test directory
        boolean success = dir.mkdir();
        if(!success) {
            System.out.println("Test directory already exists");
        }

        // Create local files for testing
        File file_one = new File(IntegrationTests.LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE);
        File file_two = new File(IntegrationTests.LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_TWO);
        if (!file_one.createNewFile()) {
            System.out.println("Test file one already exists");
        }
        if (!file_two.createNewFile()) {
            System.out.println("Test file two already exists");
        }
    }

    @AfterAll
    public static void cleanup() {
        // Delete local test files
        File file_one = new File(IntegrationTests.LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE);
        File file_two = new File(IntegrationTests.LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_TWO);
        if (!file_one.delete()) {
            System.out.println("Could not delete test file one");
        }
        if (!file_two.delete()) {
            System.out.println("Could not delete test file two");
        }

        // Delete local test directory
        File file = new File(IntegrationTests.LOCAL_TEST_DIRECTORY);
        boolean success = file.delete();
        if(!success) {
            System.out.println("Could not delete test directory");
        }
    }

    /****************************************************************************************************************/
    /**********************************************   ORDERED TESTS   ***********************************************/
    /****************************************************************************************************************/
    @Test
    @Order(1)
    public void createDirectoryTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.makeDirectory(REMOTE_TEST_DIRECTORY, sftp);
            FileAttributes att = sftp.statExistence(REMOTE_TEST_DIRECTORY);
            assertTrue(att != null); // if the file exists, this att should not be null
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(2)
    public void createDirectoryWithPathTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            client.makeDirectoryWithPath(REMOTE_NESTED_TEST_DIRECTORY, sftp);
            FileAttributes att = sftp.statExistence(REMOTE_NESTED_TEST_DIRECTORY);
            assertTrue(att != null); // if the file exists, this att should not be null
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(3)
    public void createUploadTest() throws IOException {
        SFTPClient sftp = null;

        try {
            final String src = LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE;
            final String destination = REMOTE_NESTED_TEST_DIRECTORY;

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
    @Order(4)
    public void getFileTest() throws IOException {
        final String local_file_name = "from_remote" + TEST_FILE_ONE;
        File tempFile = new File(LOCAL_TEST_DIRECTORY_PREFIX + local_file_name);
        final String remote_file_name = TEST_FILE_ONE;

        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            client.getRemoteFile(REMOTE_NESTED_TEST_DIRECTORY + "/" + remote_file_name, LOCAL_TEST_DIRECTORY_PREFIX + local_file_name);
            assertTrue(tempFile.exists());
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
            tempFile.delete();
        }
    }

    @Test
    @Order(5)
    public void removeFileTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.removeFile(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE, sftp));
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(6)
    public void putMultipleTest() throws IOException {
        final String file_one = LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE;
        final String file_two = LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_TWO;
        final String destination = REMOTE_TEST_DIRECTORY;
        final String[] files = {file_one, file_two};
        SFTPClient sftp = null;

        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.uploadMultipleFiles(files, sftp, destination));
        }
        finally {
//            client.removeFile(REMOTE_TEST_DIRECTORY + "/" + TEST_FILE_ONE, sftp);
//            client.removeFile(REMOTE_TEST_DIRECTORY + "/" + TEST_FILE_TWO, sftp);
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
        }
    }

    @Test
    @Order(7)
    public void deleteDirectoryTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final SFTPClient sftp = client.getSshClient().newSFTPClient();
            assertTrue(client.deleteDirectory(REMOTE_NESTED_TEST_DIRECTORY, sftp));
            assertTrue(client.deleteDirectory(REMOTE_TEST_DIRECTORY, sftp));
        }
        finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }


    /****************************************************************************************************************/
    /*********************************************   UNORDERED TESTS   **********************************************/
    /****************************************************************************************************************/
    @Test
    public void getNonexistentFileTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            final String file_name = "nonexistent_file.txt";
            File tempFile = new File(LOCAL_TEST_DIRECTORY_PREFIX + file_name);
            client.getRemoteFile(file_name, LOCAL_TEST_DIRECTORY_PREFIX + file_name);
            assertTrue(!tempFile.exists());
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
}

