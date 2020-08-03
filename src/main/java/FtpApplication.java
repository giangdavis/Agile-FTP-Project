import Client.Client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FtpApplication {
    private static Map connectionOptions = new LinkedHashMap<String, String>()
    {
        {
            put("Command", "Description");
            put("E", "Exit Program");
            put("T", "Type Connection Info");
            put("C", "Connect Using Saved Connection Info");
        }
    };

    private static Map commands = new LinkedHashMap<String, String>()
    {
        {
            put("Command", "Description");
            put("L", "Log Off");
            put("E", "Exit Program");
            put("LRF", "List Remote Files");
            put("GF", "Get File (From Remote)");
            put("GMF", "Get Multiple Files (From Remote)");
            put("UF", "Upload File");
            put("UPF", "Upload Multiple Files");
            put("RF", "Remove File");
            put("MRD", "Make Remote Directory");
            put("MRDFP", "Make Remote Directory (With Full Path)");
            put("DRD", "Delete Remote Directory");
        }
    };

    public static void main(String [] args) {
        Scanner scan = new Scanner(System.in);
        String username;
        String password;
        String hostname;
        int port;
        boolean connected = false;
        boolean running = true;
        Client client = new Client();

        String menu_choice;
        String path;
        String src;
        String dst;

        do {
            // Connect to remote server
            System.out.println("----------SFTP Client----------");
            System.out.println("Please enter one for the following commands");
            printFormattedMap(connectionOptions);
            menu_choice = scan.next().toUpperCase();

            switch (menu_choice) {
                case "E":
                    running = false;
                    break;
                case "T":
                    System.out.print("Username: ");
                    username = scan.next();

                    System.out.print("Password: ");
                    password = scan.next();

                    System.out.print("Hostname: ");
                    hostname = scan.next();

                    System.out.print("Port: ");
                    port = scan.nextInt();

                    client.connect(username, password, hostname, port);
                    connected = true;
                    break;
                case "C":
                    System.out.println("NOT IMPLEMENTED YET");
                    break;
                default:
                    System.out.println("Please select one of the listed commands!");
                    break;
            }

            do {
                System.out.println("Please enter one of the following commands: ");
                printFormattedMap(commands);
                System.out.print("Type your selected command here: ");
                menu_choice = scan.next().toUpperCase();

                switch (menu_choice) {
                    case "E":
                        client.logoff();
                        running = false;
                        connected = false;
                        break;
                    case "L":
                        client.logoff();
                        connected = false;
                        break;
                    case "LRF":
                        System.out.print("Please the directory/file path you would like to list: ");
                        path = scan.next();
                        try {
                            client.listRemoteFiles(path);
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "GF":
                        System.out.print("Path of file to get: ");
                        src = scan.next();

                        System.out.print("Destination path: ");
                        dst = scan.next();

                        try {
                            client.getRemoteFile(src, dst);
                        } catch(IOException e) {
                            System.err.println("Error occurred while getting file " + src + " :" + e);
                        }
                        break;
                    case "GMF":
                        System.out.println("NOT IMPLEMENTED YET");
                        break;
                    case "UF":
                        System.out.print("Path of file to upload: ");
                        src = scan.next();

                        System.out.print("Destination path: ");
                        dst = scan.next();

//                        try {
//                            client.uploadFile(src, dst);
//                        } catch(IOException e) {
//                            System.err.println("Error occurred while getting file " + src + " :" + e);
//                        }
                        break;
                    case "UFF":
                        break;
                    case "RF":
                        break;
                    case "MRD":
                        break;
                    case "MRDFP":
                        break;
                    case "DRD":
                        break;
                    default:
                        System.out.println("Please select one of the listed commands!");
                        break;
                }
            } while(connected);
        } while(running);

        System.out.println("\n\nExiting program...");
    }

    private static void printFormattedMap(Map map) {
        Set<String> keys = map.keySet();
        for(String key : keys) {
            System.out.print(String.format("%1$-8s", key));
            System.out.println("- " + map.get(key));
        }
    }
}
