import Client.Client;
import java.io.IOException;
import java.util.Scanner;

public class FtpApplication {
    public static void main(String [] args) {
        Scanner scan = new Scanner(System.in);
        String username;
        String password;
        String hostname;
        int port;
        boolean connected = false;
        boolean running = true;
        Client client = new Client();

        do {
            System.out.println("----------SFTP Client----------");
            System.out.println("Please enter your desired connection information");

            System.out.print("Username: ");
            username = scan.next();

            System.out.print("Password: ");
            password = scan.next();

            System.out.print("Hostname: ");
            hostname = scan.next();

            System.out.print("Port: ");
            port = scan.nextInt();

            client.connect(username, password, hostname, port);

        } while(running);



    }
}