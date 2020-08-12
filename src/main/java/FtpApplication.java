import Client.Client;

import java.io.IOException;
import java.util.*;

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

    //Prompts for hostname (used for asking which server user wants to connect to)
    private static String promptForHost() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the hostname of the server you wish to connect to.");
        String hostname = scan.nextLine();

        return hostname;
    }

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
            put("UMF", "Upload Multiple Files");
            put("CFP", "Change Remote File Permissions");
            put("RRF", "Remove Remote File");
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
        String permissions;
        String source;
        String destination;
        String sentinel;

        do {
            // Connect to remote server
            System.out.println("----------SFTP Client----------");
            System.out.println("Please enter one for the following commands");
            printFormattedMap(connectionOptions);
            System.out.print("Type your selected command here: ");
            menu_choice = scan.nextLine().toUpperCase();

            switch (menu_choice) {
                case "E":
                    running = false;
                    break;
                case "T":
                    System.out.print("Username: ");
                    username = scan.nextLine();

                    System.out.print("Password: ");
                    password = scan.nextLine();

                    System.out.print("Hostname: ");
                    hostname = scan.nextLine();

                    System.out.print("Port: ");
                    port = scan.nextInt();
                    scan.nextLine();

                    if(client.connect(username, password, hostname, port)) {
                        connected = true;

                        // Ask if they would like to save their connection information
                        do {
                            System.out.print("Would you like to save your connection information? (Y/n)");
                            sentinel = scan.nextLine().toUpperCase();

                            if(sentinel.equals("Y")) {
                                try {
                                    client.saveConnectionInformation(username, password, hostname, Integer.toString(port));
                                    System.out.println("Your connection information has been saved!");
                                } catch(IOException e) {
                                    System.err.println("An error has occurred while trying to save your connection information:" + e);
                                }
                            }
                        } while(sentinel.equals("N") && sentinel.equals("Y"));
                    } else {
                        connected = false;
                    }
                    break;
                case "C":
                    if(client.printConnections() == true) {
                        String savedHostName = promptForHost();
                        client.connectWithSavedInfo(savedHostName);
                        connected=true;
                    }
                    break;
                default:
                    System.out.println("Please select one of the listed commands!");
                    break;
            }

            while(connected) {
                System.out.println("\n\n\nPlease enter one of the following commands: ");
                printFormattedMap(commands);
                System.out.print("Type your selected command here: ");
                menu_choice = scan.nextLine().toUpperCase();

                switch (menu_choice) {
                    case "E":
                        System.out.println(commands.get(menu_choice));
                        client.logoff();
                        running = false;
                        connected = false;
                        break;
                    case "L":
                        System.out.println(commands.get(menu_choice));
                        client.logoff();
                        connected = false;
                        break;
                    case "LRF":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Please the directory/file path you would like to list: ");
                        path = scan.nextLine();
                        try {
                            client.listRemoteFiles(path);
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "GF":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Path of file to get: ");
                        source = scan.nextLine();

                        System.out.print("Destination path: ");
                        destination = scan.nextLine();

                        try {
                            client.getRemoteFile(source, destination);
                        } catch(IOException e) {
                            System.err.println("Error occurred while getting file " + source + " :" + e);
                        }
                        break;
                    case "GMF":
                        System.out.println(commands.get(menu_choice));
                        System.out.println("NOT IMPLEMENTED YET");
                        break;
                    case "UF":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Path of file to upload: ");
                        source = scan.nextLine();

                        System.out.print("Destination path: ");
                        destination = scan.nextLine();

                        try {
                            client.uploadFile(source, destination);
                        } catch(IOException e) {
                            System.err.println("Error occurred while getting file " + source + " :" + e);
                        }
                        break;
                    case "UMF":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Destination path: ");
                        destination = scan.nextLine();

                        Set uploadFileSet = new HashSet<String>();
                        do {
                            System.out.print("Path of file to upload: ");
                            source = scan.nextLine();

                            uploadFileSet.add(source);

                            System.out.print("Upload another file? (Y/n)");
                            sentinel = scan.nextLine().toUpperCase();
                        } while(sentinel.equals("Y"));

                        client.uploadMultipleFiles(Arrays.copyOf(uploadFileSet.toArray(), uploadFileSet.toArray().length, String[].class), destination);
                        break;
                    case "RRF":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Please enter the path to the remote file you would like to delete: ");
                        path = scan.nextLine();
                        try {
                            client.removeFile(path);
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "MRD":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Please enter the path of the directory you would like to create: ");
                        path = scan.nextLine();
                        try {
                            client.makeDirectory(path);
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "MRDFP":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Please enter the path of the directory chain you would like to create: ");
                        path = scan.nextLine();
                        try {
                            client.makeDirectoryWithPath(path);
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "CFP":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Please enter the path of the file you wish to change: ");
                        path = scan.nextLine();

                        System.out.print("Please enter the the new file permissions (e.g. 777, 600, 444): ");
                        permissions = scan.nextLine();

                        try {
                            client.changeRemotePermissions(path, permissions);
                        } catch(IOException e) {
                            System.err.println("Error occurred while changing  remote file permissions:" + e);
                        }
                        break;
                    case "DRD":
                        System.out.println(commands.get(menu_choice));
                        System.out.print("Please enter the path to the remote directory you would like to delete: ");
                        path = scan.nextLine();
                        try {
                            client.deleteDirectory(path);
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    default:
                        System.out.println("Please select one of the listed commands!");
                        break;
                }
            }
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
