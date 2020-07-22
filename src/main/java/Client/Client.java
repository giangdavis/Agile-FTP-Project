package Client;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.util.List;

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

    protected SSHClient createSSHClient() {
        return new SSHClient();
    }

    protected SFTPClient createSFTPClient() throws IOException {
        return sshClient.newSFTPClient();
    }
}
