package Client;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
        } catch(IOException e) {
            System.err.println("Error while trying to connect:" + e);
            return false;
        }
        System.out.println("Connected!");
        setSshClient(ssh);
        return true;
    }

    public boolean listRemoteFiles(String directory) throws IOException {
        if(getSshClient().isConnected()) {
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

    public boolean getRemoteFile(String source, String dest) throws IOException {
        if(getSshClient().isConnected()) {
            SFTPClient client = createSFTPClient();
            try {
                client.get(source, dest);
                System.out.println("Remote files listed successfully");
            } catch (IOException e) {
                System.err.println("Error while getting remote file:" + e);
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
     * @param dirName A string which represents the directory name
     * @param client A SFTPClient object which is used to make the directory
     * @return true or false depending on if the directory was successfully created
     * @throws IOException
     */
    public boolean makeDirectory(String dirName, SFTPClient client) throws IOException {
        FileAttributes att = client.statExistence(dirName);
        if(att == null) {
            try {
                client.mkdir(dirName);
                System.out.println("Directory successfully created!");
                return true;
            }
            catch(IOException e) {
                System.out.println("Something happened, try creating the specified directory again");
                return false;
            }
        }
        else {
            System.out.println("Directory with specified name already exists!");
            return false;
        }
    }

    /**
     * This method returns true or false depending on if a directory with a specified path was created
     * @param path A string which represents the path of where to create a directory
     * @param client A SFTPClient object which is used to make the directory
     * @return true or false depending on if the directory was successfully created
     * @throws IOException
     */
    public boolean makeDirectoryWithPath(String path, SFTPClient client) throws IOException {
        FileAttributes att = client.statExistence(path);
        if(att == null) {
            try {
                client.mkdirs(path);
                System.out.println("Directory successfully created!");
                return true;
            }
            catch(IOException e) {
                System.out.println("Something happened, try creating the specified directory again");
                return false;
            }
        }
        else {
            System.out.println("Directory with specified path already exists!");
            return false;
        }
    }

    /**
     * This method returns true or false, if true is returned the file was successfully deleted, if false the file
     * was not deleted
     * @param path A string which represents the path to a file
     * @param client A SFTPClient object which is used to remove the file
     * @return true or false depending on if the file was deleted
     * @throws IOException
     */
    public boolean removeFile(String path, SFTPClient client) throws IOException {
        FileAttributes att = client.statExistence(path);
        if(att != null) {
            try {
                client.rm(path);
                System.out.println("File was successfully deleted!");
                return true;
            }
            catch(IOException e) {
                System.out.println("Something happened, try deleting the file again");
                return false;
            }
        }
        else {
            System.out.println("File with specified path does not exist!");
            return false;
        }
    }

    /**
     * This method returns true or false depending on if a file is successfully uploaded onto the remote server,
     * if false the file was not uploaded
     * @param filename A string which represents the filename of the file being uploaded
     * @param sftp A SFTPClient object which is used to upload the file
     * @return true or false depending on if the file was succesfully uploaded
     * @throws IOException
     */
    public boolean uploadFile(String filename, SFTPClient sftp, String destination) throws IOException {
        try
        {
            final String fileToTransfer = filename;

            sftp.put(new FileSystemFile(fileToTransfer), destination);
            System.out.println("File upload successful");
            return true;
        }
        catch(IOException e) {
            System.out.println("Error in uploading file to sftp server, try again");
            return false;
        }
    }

    protected SSHClient createSSHClient() {
        return new SSHClient();
    }

    protected SFTPClient createSFTPClient() throws IOException {
        return sshClient.newSFTPClient();
    }

    /**
     * This method is used to upload multiple files. It returns true when file upload is successful else false.
     * @param filename A string which represents the name of the file to be uploaded
     * @param sftp A SFTPClient object used to upload the file
     * @param destination A string which represents the destination path for the file being uploaded
     * @return true or false depending on if the file was uploaded
     * @throws IOException
     */
    public boolean uploadMultipleFiles(String filename, SFTPClient sftp, String destination) throws IOException {
        try {
            uploadFile(filename, sftp, destination);
            String input;

            do {
                System.out.println("Would you like to upload another file? (y/n)");
                Scanner scan = new Scanner(System.in);
                input = scan.next();

                if (input == "y") {
                    System.out.print("Enter the filename: ");
                    String fname = scan.next();
                    System.out.print("Enter the destination: ");
                    String dest = scan.next();
                    uploadMultipleFiles(fname, sftp, dest);
                } else if (input == "n") {
                    return true;
                } else {
                    System.out.println("Please enter either y/n");
                }
            } while(input != "y" || input != "n");
        }
        catch(IOException e) {
            System.out.println("Error uploading " + filename + " to the server, please try again");
            return false;
        }
        return true;
    }

    /**
     * This method returns true or false, if true is returned the directory was successfully deleted, if false the directory
     * was not deleted
     * @param path A string which represents the directory path
     * @param client A SFTPClient object which is used to remove the directory
     * @return true or false depending on if the directory was deleted
     * @throws IOException
     */
    public boolean deleteDirectory(String path, SFTPClient client) throws IOException {
        FileAttributes att = client.statExistence(path);
        if (att != null) {
            try {
                client.rmdir(path);
                System.out.println("Directory was succesfully deleted.");
                return true;
            }
            catch(IOException e) {
                System.out.println("Directory deletion failed for some reason, try again");
                return false;
            }
        }
        else {
            System.out.println("Directory does not exist.");
            return false;
        }
    }
}
