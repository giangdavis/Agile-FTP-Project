package Client;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.IOException;

public class Client {
    private String hostname;
    private String username;
    private String password;
    private int port;

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

    public SSHClient connect(String username, String password, String hostname, int port) throws IOException {
        final SSHClient ssh = new SSHClient();
        setHostname(hostname);
        setUsername(username);
        setPassword(password);
        setPort(port);
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.loadKnownHosts();
        ssh.connect(getHostname(), getPort());
        ssh.authPassword(getUsername(), getPassword());
        return ssh;
    }

    public void uploadFile(String filename, SFTPClient sftp) throws IOException {
        try
        {
            final String fileToTransfer = filename;
            sftp.put(new FileSytemFile(fileToTransfer), "/tmp");
//            try {
//                sftp.put(new FileSytemFile(fileToTransfer), "/tmp");
//            }
//            finally {
//                sftp.close();
//            }
        }
        catch(IOException e) {
           System.out.println("Error in uploading file to sftp server, try again");
        }
    }
}
