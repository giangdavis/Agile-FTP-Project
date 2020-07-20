package Client;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

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
}
