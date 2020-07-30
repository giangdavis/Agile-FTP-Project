import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.nio.file.FileSystemException;

public class Credentials {
    private Dotenv dotenv;
    private String user;
    private String password;
    private String hostname;
    private int port;

    public Credentials() throws FileSystemException {
        File env = new File(".env");
        if(env.exists()) {
            this.dotenv = Dotenv.load();
        } else {
            throw new FileSystemException("No .env file was found");
        }

        this.user = dotenv.get("USER");
        this.password = dotenv.get("PASSWORD");
        this.hostname = dotenv.get("HOST_NAME");
        this.port = dotenv.get("PORT") == null ? 22 : Integer.parseInt(dotenv.get("PORT"));
    }

    public Credentials(String user, String password, String hostname, int port) {
        File env = new File(".env");
        if(env.exists()) {
            this.dotenv = Dotenv.load();

            this.user = user != null ? user : dotenv.get("USER");
            this.password = password != null ? password : dotenv.get("PASSWORD");
            this.hostname = hostname != null ? hostname : dotenv.get("HOST_NAME");

            int default_port = dotenv.get("PORT") == null ? 22 : Integer.parseInt(dotenv.get("PORT"));
            this.port = port != 0 ? port : default_port;
        } else {
            System.out.println("No .env file was found -- using user provided inputs");

            this.user = user;
            this.password = password;
            this.hostname = hostname;
            this.port = port;
        }
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }
}