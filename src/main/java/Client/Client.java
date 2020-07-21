package Client;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;

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

    public void makeDirectory(String dirName, SFTPClient client) throws IOException {
        FileAttributes att = client.statExistence(dirName);
        if(att == null) {
            try {
                client.mkdir(dirName);
                System.out.println("Directory successfully created!");
            }
            catch(IOException e) {
                System.out.println("Something happened, try creating the specified directory again");
            }
        }
        else {
            System.out.println("Directory with specified name already exists!");
        }
    }

    public void makeDirectoryWithPath(String path, SFTPClient client) throws IOException {
        FileAttributes att = client.statExistence(path);
        if(att == null) {
            try {
                client.mkdirs(path);
                System.out.println("Directory successfully created!");
            }
            catch(IOException e) {
                System.out.println("Something happened, try creating the  specified directory again");
            }
        }
        else {
            System.out.println("Directory with specified path already exists!");
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
}
