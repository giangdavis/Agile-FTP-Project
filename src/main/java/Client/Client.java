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

    public void connect(String username, String password, String hostname, int port) throws IOException {
        final SSHClient ssh = new SSHClient();
        setHostname(hostname);
        setUsername(username);
        setPassword(password);
        setPort(port);
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.loadKnownHosts();
        ssh.connect(getHostname(), getPort());
        ssh.authPassword(getUsername(), getPassword());
        System.out.println("Connected!");
        setSshClient(ssh);
    }



    public void listRemoteFiles(String directory, SFTPClient client) throws IOException{
        String Dir = (directory.equals("."))? "root":directory;
            try {
                System.out.println("List of Remote Files in " + Dir + ":");
                List fileList = client.ls(directory);

                for (int i = 0; i < fileList.size(); i++) {
                    System.out.println(fileList.get(i).toString());
                }

                System.out.println("Remote files listed successfully");
            } catch (IOException e) {
                System.err.println("Error while listing remote files" + e);
            }

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
