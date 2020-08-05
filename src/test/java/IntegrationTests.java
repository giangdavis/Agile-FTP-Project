import Client.Client;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {
    // You can put your credentials in here -- REMOVE THEM BEFORE PUSHING TO GITHUB
    // Or you can have a .env file with your credentials in it and the values in the .env file will be used
    private Credentials credentials = new Credentials("devyani", "hexap*M92m", "babbage.cs.pdx.edu", 22);

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
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();
        try {
            client.makeDirectory(REMOTE_TEST_DIRECTORY);
            FileAttributes att = sftp.statExistence(REMOTE_TEST_DIRECTORY);
            assertTrue(att != null); // if the file exists, this att should not be null
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(2)
    public void createDirectoryWithPathTest() throws IOException {
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();
        try {
            client.makeDirectoryWithPath(REMOTE_NESTED_TEST_DIRECTORY);
            FileAttributes att = sftp.statExistence(REMOTE_NESTED_TEST_DIRECTORY);
            assertTrue(att != null); // if the file exists, this att should not be null
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(3)
    public void createUploadTest() throws IOException {
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();
        try {
            final String src = LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE;
            final String destination = REMOTE_NESTED_TEST_DIRECTORY;

            assertTrue(client.uploadFile(src, destination));

            // Check that the file was uploaded
            FileAttributes att = sftp.statExistence(destination + "/" + TEST_FILE_ONE);
            assertTrue(att != null); // if the file exists, this att should not be null
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
    public void ChangePermissionsOnRemoteTest() throws IOException {
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();
        try {
            assertTrue(client.changeRemotePermissions(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE, "000"));

            // Check file permissions did change
            FileAttributes att = sftp.statExistence(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE);
            assertEquals(0, att.getMode().getPermissionsMask());


            // Test again (just in case file permission had previously been set to 000 already)
            assertTrue(client.changeRemotePermissions(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE, "444"));

            // Check file permissions did change
            att = sftp.statExistence(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE);
            assertEquals(292, att.getMode().getPermissionsMask());

        } finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(6)
    public void removeFileTest() throws IOException {
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();

        try {
            assertTrue(client.removeFile(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE));

            // Check the file was deleted
            FileAttributes att = sftp.statExistence(REMOTE_NESTED_TEST_DIRECTORY + "/" + TEST_FILE_ONE);
            assertTrue(att == null); // if the file exists, this att should not be null
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }

    @Test
    @Order(7)
    public void putMultipleTest() throws IOException {
        final String file_one = LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE;
        final String file_two = LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_TWO;
        final String destination = REMOTE_TEST_DIRECTORY;
        final String[] files = {file_one, file_two};

        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();

        try {
            assertTrue(client.uploadMultipleFiles(files, destination));

            // Check the files were uploaded
            FileAttributes att_one = sftp.statExistence(REMOTE_TEST_DIRECTORY + "/" + TEST_FILE_ONE);
            FileAttributes att_two = sftp.statExistence(REMOTE_TEST_DIRECTORY + "/" + TEST_FILE_TWO);
            assertTrue(att_one != null); // if the file exists, this att should not be null
            assertTrue(att_two != null); // if the file exists, this att should not be null
        }
        finally {
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
        }
    }

    @Test
    @Order(8)
    public void getMultipleTest() throws IOException {
        final String file_one = REMOTE_TEST_DIRECTORY + "/" + TEST_FILE_ONE;
        final String file_two = REMOTE_TEST_DIRECTORY + "/" + TEST_FILE_TWO;
        File tempFile1 = new File(LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_ONE);
        File tempFile2 = new File(LOCAL_TEST_DIRECTORY + File.separator + TEST_FILE_TWO);
        final String destination = LOCAL_TEST_DIRECTORY;
        final String[] files = {file_one, file_two};

        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();

        try {
            assertTrue(client.getMultipleRemoteFiles(files, destination));
            assertTrue(tempFile1.exists());
            assertTrue(tempFile2.exists());
        }
        finally {
            client.removeFile(REMOTE_TEST_DIRECTORY + File.separator + TEST_FILE_ONE);
            client.removeFile(REMOTE_TEST_DIRECTORY + File.separator + TEST_FILE_TWO);
            sftp.close();
            client.getSshClient().disconnect();
            System.out.println("disconnected");
            tempFile1.delete();
            tempFile2.delete();
        }
    }

    @Test
    @Order(9)
    public void deleteDirectoryTest() throws IOException {
        client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
        final SFTPClient sftp = client.getSshClient().newSFTPClient();
        try {
            assertTrue(client.deleteDirectory(REMOTE_NESTED_TEST_DIRECTORY));
            assertTrue(client.deleteDirectory(REMOTE_TEST_DIRECTORY));

            // Check the directories were deleted
            FileAttributes att_one = sftp.statExistence(REMOTE_TEST_DIRECTORY);
            FileAttributes att_two = sftp.statExistence(REMOTE_NESTED_TEST_DIRECTORY);
            assertTrue(att_one == null); // if the file exists, this att should not be null
            assertTrue(att_two == null); // if the file exists, this att should not be null
        }
        finally {
            sftp.close();
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
    public void renameLocalFileTest() throws IOException {
        assertTrue(client.renameLocalFile(LOCAL_TEST_DIRECTORY + File.separator, TEST_FILE_ONE, "temp"));
        assertTrue(client.renameLocalFile(LOCAL_TEST_DIRECTORY + File.separator, "temp", TEST_FILE_ONE));
    }

    // This test really doesn't need to connect to a SFTP server, this is purely just to write to a properties file
    // Usage of the saveConnectionInformation() method would be just passing in the connection details a user enters
    @Test
    public void saveConnectionTest() throws IOException {
        client.saveConnectionInformation("est29", "test2", "babbage.cs.pdx.edu.pdx.jasdlasj", "test3");
    }

    @Test
    public void listRemoteFilesTest() throws IOException {
        try {
            client.connect(credentials.getUser(), credentials.getPassword(), credentials.getHostname(), credentials.getPort());
            client.listRemoteFiles(".");
        } finally {
            client.getSshClient().disconnect();
            System.out.println("Disconnected!");
        }
    }
}

