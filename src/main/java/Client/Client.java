package Client;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private String hostname;
    private String username;
    private String password;
    private int port;
    SSHClient sshClient;

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SSHClient getSshClient() {
        return sshClient;
    }

    public void setSshClient(SSHClient sshClient) {
        this.sshClient = sshClient;
    }

    /**
     * This method connects the user to a remote server.
     *
     * @param username A string containing the user's username
     * @param password A string containing the user's password
     * @param hostname A string representing which remote server to connect to
     * @param port A integer representing what port to use when SSH'ing into the remote server
     * @return true or false depending on if the user is able to be connected
     */
    public boolean connect(String username, String password, String hostname, int port) {
        final SSHClient ssh = createSSHClient();
        setHostname(hostname);
        setUsername(username);
        setPassword(password);
        setPort(port);
        try {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.loadKnownHosts();
            ssh.connect(getHostname(), getPort());
            ssh.authPassword(getUsername(), getPassword());
        } catch (IOException e) {
            System.err.println("Error while trying to connect:" + e);
            return false;
        }
        System.out.println("Connected!");
        setSshClient(ssh);
        return true;
    }

    /**
     * This method lists all the files/directories of the given path
     *
     * @param directory The path of the directory
     * @return returns true if listed successfully else returns false
     * @throws IOException
     */
    public boolean listRemoteFiles(String directory) throws IOException {
        if (getSshClient().isConnected()) {
            String Dir = (directory.equals(".")) ? "root" : directory;
            SFTPClient client = createSFTPClient();
            try {
                System.out.println("List of Remote Files in " + Dir + ":");
                List fileList = client.ls(directory);

                for (int i = 0; i < fileList.size(); i++) {
                    System.out.println(fileList.get(i).toString());
                }

                System.out.println("Remote files listed successfully");
            } catch (IOException e) {
                System.err.println("Error while listing remote files:" + e);
                return false;
            } finally {
                client.close();
            }
        } else {
            System.err.println("Error while listing remote files: SSH Client is not connected");
            return false;
        }
        return true;
    }

    /**
     * This method returns true or false depending on if a remote file is successfully downloaded or not.
     *
     * @param source A string which represents the path to the file to download
     * @param dest A string which represents the path to where the file should be downloaded to locally
     * @return true or false depending on if the file is successfully downloaded
     * @throws IOException
     */
    public boolean getRemoteFile(String source, String dest) throws IOException {
        if (getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            try {
                client.get(source, dest);
                System.out.println("Remote files listed successfully");
            } catch (IOException e) {
                System.err.println("Error while getting remote file: " + e);
                return false;
            } finally {
                client.close();
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
        return true;
    }

    /**
     * This method returns true or false depending on if a directory with a specified name was created,
     * this by default will create the directory in the home directory
     *
     * @param dirName A string which represents the directory name
     * @return true or false depending on if the directory was successfully created
     * @throws IOException
     */
    public boolean makeDirectory(String dirName) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            FileAttributes att = client.statExistence(dirName);
            if (att == null) {
                try {
                    client.mkdir(dirName);
                    System.out.println("Directory successfully created!");
                    return true;
                } catch (IOException e) {
                    System.out.println("Something happened, try creating the specified directory again");
                    return false;
                } finally {
                    client.close();
                }
            } else {
                System.out.println("Directory with specified name already exists!");
                return false;
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * This method returns true or false depending on if a directory with a specified path was created
     *
     * @param path   A string which represents the path of where to create a directory
     * @return true or false depending on if the directory was successfully created
     * @throws IOException
     */
    public boolean makeDirectoryWithPath(String path) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            FileAttributes att = client.statExistence(path);
            if (att == null) {
                try {
                    client.mkdirs(path);
                    System.out.println("Directory successfully created!");
                    return true;
                } catch (IOException e) {
                    System.out.println("Something happened, try creating the specified directory again");
                    return false;
                } finally {
                    client.close();
                }
            } else {
                System.out.println("Directory with specified path already exists!");
                return false;
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * This method returns true or false, if true is returned the file was successfully deleted, if false the file
     * was not deleted
     *
     * @param path A string which represents the path to a file
     * @return true or false depending on if the file was deleted
     * @throws IOException
     */
    public boolean removeFile(String path) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            FileAttributes att = client.statExistence(path);
            if (att != null) {
                try {
                    client.rm(path);
                    System.out.println("File was successfully deleted!");
                    return true;
                } catch (IOException e) {
                    System.out.println("Something happened, try deleting the file again");
                    return false;
                } finally {
                    client.close();
                }
            } else {
                System.out.println("File with specified path does not exist!");
                return false;
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * This method returns true or false depending on if a file is successfully uploaded onto the remote server,
     * if false the file was not uploaded
     *
     * @param filename A string which represents the filename of the file being uploaded
     * @return true or false depending on if the file was succesfully uploaded
     * @throws IOException
     */
    public boolean uploadFile(String filename, String destination) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            try {
                final String fileToTransfer = filename;

                client.put(new FileSystemFile(fileToTransfer), destination);
                System.out.println("File upload successful");
                return true;
            } catch (IOException e) {
                System.out.println("Error in uploading " + filename + " to sftp server, try again");
                return false;
            } finally {
                client.close();
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * This method is used to upload multiple files. It returns true when file upload is successful else false.
     *
     * @param files An array of strings which represents the names of the files to be uploaded
     * @param destination A string which represents the destination path for the files being uploaded
     * @return true or false depending on if the file was uploaded
     */
    public boolean uploadMultipleFiles(String[] files, String destination) {
        if(getSshClient().isConnected()) {
            try {
                for (String filename : files) {
                    uploadFile(filename, destination);
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * This method returns true or false, if true is returned the directory was successfully deleted, if false the directory
     * was not deleted
     *
     * @param path   A string which represents the directory path
     * @return true or false depending on if the directory was deleted
     * @throws IOException
     */
    public boolean deleteDirectory(String path) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            FileAttributes att = client.statExistence(path);
            if (att != null) {
                try {
                    client.rmdir(path);
                    System.out.println("Directory was succesfully deleted.");
                    return true;
                } catch (IOException e) {
                    System.out.println("Directory deletion failed for some reason, try again");
                    return false;
                } finally {
                    client.close();
                }
            } else {
                System.out.println("Directory does not exist.");
                return false;
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * This method disconnects the SSH connection, returning true if successful and returning false if unsuccessful.
     *
     * @return returns true or false dependong in if logout is successful
     */
    public boolean logoff() {
        if (!getSshClient().isConnected()) {
            System.out.println("Already disconnected!");
            return true;
        }

        try {
            getSshClient().disconnect();
            System.out.println("Disconnected!");
            return true;
        } catch (IOException e) {
            System.err.println("Error occurred while logging off:" + e);
            return false;
        }
    }
    /**
     * This method returns true or false, if true the file was renamed successfully,
     * if false the file was not renamed
     *
     * @param path   A string which represents the directory path
     *               Leave this as an empty string if you wish to create a new file
     *               in current directory
     * @param oldName A string that specifies the old file name
     * @param newName A string that specifies the new name of the file
     * @return true or false depending on if the file was renamed
     * @throws IOException
     */
    public boolean renameLocalFile(String path, String oldName, String newName) throws IOException {
        File oldFile = new File(path + oldName);
        File newFile = new File(path + newName);

        if (!oldFile.exists())
            throw new IOException("Renaming file failed! File does not exist.");
        if (newFile.exists())
            throw new IOException("Renaming file failed! The new filename already existed.");

        boolean success = oldFile.renameTo(newFile);
        if (success == true) {
            System.out.println(oldName + " has been renamed to " + newName + " successfully!");
            return true;
        } else {
            System.err.println("Error occurred trying to rename a file");
            return false;
        }
    }

    /**
     * This method saves a connection for unique hostnames. If details to a hostname exists in the connection.properties
     * file the method will exit.
     *
     * @param username A string which contains the username
     * @param password A string which contains the password
     * @param hostname A string which contains the hostname
     * @param port A string which contains the port
     * @throws IOException
     */
    public void saveConnectionInformation(String username, String password, String hostname, String port) throws IOException {
        File file = new File("src/main/resources/connection.properties");
        FileOutputStream os;
        file.createNewFile();

        if(isInConnectionFile(file, hostname)) {
            return; // ALREADY IN CONNECTION FILE
        } else {
            os = new FileOutputStream(file, true);
        }

        Properties prop = new Properties();

        try(OutputStream out = os) {
            prop.setProperty(hostname + "_username", username);
            prop.setProperty(hostname + "_password", password);
            prop.setProperty(hostname + "_hostname", hostname);
            prop.setProperty(hostname + "_port", port);
            prop.store(out, null);
        }
    }

    /**
     * This is a helper method that is used in saveConnectionInformation(). It checks to see if a property with a specific
     * hostname exists in the connection.properties file already. If it does it returns true else if there is no
     * property it will return false.
     *
     * @param connectionFile A File object for the connection.properites file
     * @param hostname A string which contains the hostname
     * @return true or false depending on if there is an existing property in the connection.properties file
     * @throws IOException
     */
    public boolean isInConnectionFile(File connectionFile, String hostname) throws IOException {
        InputStream input = new FileInputStream(connectionFile);
        Properties prop = new Properties();
        prop.load(input);

        if(prop.getProperty(hostname + "_hostname") == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * The code changes the permissions on the remote file.
     *
     * @param path        path of the file
     * @param permission  file permissions to be passed to chmod
     * @return returns true if the permission changed successfully else returns false
     */
    public boolean changeRemotePermissions(String path, String permission) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            try {
                client.chmod(path, Integer.parseInt(permission, 8));
                System.out.println("Successfully changed the file permissions..!!");
                return true;
            } catch (NumberFormatException | IOException e) {
                System.out.println("Failed to change file permissions");
                System.out.println(e.getMessage());
                System.out.println("Error. Could not change permissions or invalid chmod code. See the message above.");
                return false;
            } finally {
                client.close();
            }
        } else {
            System.err.println("Error while getting remote file: SSH Client is not connected");
            return false;
        }
    }

    /**
     * Returns an SSHClient -- Useful when using mocks for unit testing.
     *
     * @return
     */
    protected SSHClient createSSHClient() {
        return new SSHClient();
    }

    /**
     * * Returns an SFTPClient -- Useful when using mocks for unit testing.
     *
     * @return
     * @throws IOException
     */
    protected SFTPClient createSFTPClient() throws IOException {
        return sshClient.newSFTPClient();
    }

    /**
     * This method prints all the hostnames from the saved info from the prop file.
     *
     * @return true or false depending on if the properties file existed and printed
     */
    public boolean printConnections() {
        try(InputStream input = new FileInputStream("src/main/resources/connection.properties")){
            Properties prop = new Properties();

            if(input==null) {
                System.out.println("Sorry, unable to find the properties file");
                return false;
            }

            prop.load(input);

            for(Object key : prop.keySet()) {
                String keyString = key.toString();
                if(keyString.contains("_hostname")) {
                    System.out.println(prop.getProperty(keyString));
                }
            }
            return true;

        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method takes a hostname and gets the saved connection info from the properties file
     * then connects to the server with the connection info.
     *
     * @param hostname hostname of server to connect to
     * @throws IOException
     */

    public boolean connectWithSavedInfo(String hostname) {
        try(InputStream input = new FileInputStream("src/main/resources/connection.properties")) {
            Properties prop = new Properties();

            prop.load(input);

            String username = prop.getProperty(hostname + "_username");
            String password = prop.getProperty(hostname + "_password");
            String host = prop.getProperty(hostname + "_hostname");
            String portString = prop.getProperty(hostname + "_port");
            int port=Integer.parseInt(portString);

            return connect(username, password, host, port);
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
