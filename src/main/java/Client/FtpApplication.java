package Client;

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
        Client client = new Client();

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

        try {
            client.connect(username, password, hostname, port);
        }

        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}