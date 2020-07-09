package Client;

import net.schmizz.sshj.SSHClient;

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
        //ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.loadKnownHosts();
        ssh.connect(getHostname(), getPort());
        ssh.authPassword(getUsername(), getPassword());
        return ssh;
    }
}
