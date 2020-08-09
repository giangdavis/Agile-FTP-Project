import Client.Client;
import Client.CustomType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

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
        CustomType username = new CustomType();
        CustomType password = new CustomType();
        CustomType hostname = new CustomType();
        CustomType port = new CustomType();
        boolean connected = false;
        boolean running = true;
        Client client = new Client();

        final CustomType menu_choice = new CustomType();
        final CustomType path = new CustomType();
        final CustomType permissions = new CustomType();
        final CustomType source = new CustomType();
        final CustomType destination = new CustomType();
        final CustomType sentinel = new CustomType();

        final ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Object> future;

        do {
            // Connect to remote server
            System.out.println("----------SFTP Client----------");
            System.out.println("Please enter one for the following commands");
            printFormattedMap(connectionOptions);
            System.out.print("Type your selected command here: ");
            try {
                future = service.submit(() -> {
                    menu_choice.setStringValue(scan.nextLine().toUpperCase());
                    return 0;
                });
                future.get(30, TimeUnit.MINUTES);
            }
            catch(TimeoutException | InterruptedException | ExecutionException e) {
                System.out.println("\nTimed OUT!");
                System.exit(0);
            }

            switch (menu_choice.getStringValue()) {
                case "E":
                    running = false;
                    break;
                case "T":
                    try {
                        future = service.submit(() -> {
                            System.out.print("Username: ");
                            username.setStringValue(scan.nextLine());

                            System.out.print("Password: ");
                            password.setStringValue(scan.nextLine());

                            System.out.print("Hostname: ");
                            hostname.setStringValue(scan.nextLine());

                            System.out.print("Port: ");
                            port.setIntegerValue(scan.nextInt());
                            scan.nextLine();

                            return 0;
                        });
                        future.get(30, TimeUnit.MINUTES);
                    } catch(TimeoutException | InterruptedException | ExecutionException e) {
                        System.out.println("\nTimed OUT!");
                        System.exit(0);
                    }

                    if(client.connect(username.getStringValue(), password.getStringValue(), hostname.getStringValue(), port.getIntegerValue())) {
                        connected = true;

                        // Ask if they would like to save their connection information
                        do {
                            try{
                                future = service.submit(() ->  {
                                    System.out.print("Would you like to save your connection information? (Y/n)");
                                    sentinel.setStringValue(scan.nextLine().toUpperCase());
                                    return 0;
                                });
                                future.get(30, TimeUnit.MINUTES);
                            } catch(TimeoutException | InterruptedException | ExecutionException e) {
                                System.out.println("\nTimed OUT!");
                                client.logoff();
                                System.exit(0);
                            }

                            if(sentinel.getStringValue().equals("Y")) {
                                try {
                                    client.saveConnectionInformation(username.getStringValue(), password.getStringValue(), hostname.getStringValue(), Integer.toString(port.getIntegerValue()));
                                    System.out.println("Your connection information has been saved!");
                                } catch(IOException e) {
                                    System.err.println("An error has occurred while trying to save your connection information:" + e);
                                }
                            }
                        } while(sentinel.getStringValue().equals("N") && sentinel.getStringValue().equals("Y"));
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
                try {
                    future = service.submit(() -> {
                        menu_choice.setStringValue(scan.nextLine().toUpperCase());
                        return 0;
                    });
                    future.get(30, TimeUnit.MINUTES);
                } catch(TimeoutException | InterruptedException | ExecutionException e) {
                    System.out.println("\nTimed OUT!");
                    client.logoff();
                    System.exit(0);
                }

                switch (menu_choice.getStringValue()) {
                    case "E":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        client.logoff();
                        running = false;
                        connected = false;
                        break;
                    case "L":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        client.logoff();
                        connected = false;
                        break;
                    case "LRF":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.print("Please the directory/file path you would like to list: ");
                        try {
                            future = service.submit(() -> {
                                path.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.listRemoteFiles(path.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "GF":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        try {
                            future = service.submit(() -> {
                                System.out.print("Path of file to get: ");
                                source.setStringValue(scan.nextLine());

                                System.out.print("Destination path: ");
                                destination.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.getRemoteFile(source.getStringValue(), destination.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while getting file " + source.getStringValue() + " :" + e);
                        }
                        break;
                    case "GMF":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.println("NOT IMPLEMENTED YET");
                        break;
                    case "UF":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        try {
                            future = service.submit(() -> {
                                System.out.print("Path of file to upload: ");
                                source.setStringValue(scan.nextLine());

                                System.out.print("Destination path: ");
                                destination.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.uploadFile(source.getStringValue(), destination.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while getting file " + source.getStringValue() + " :" + e);
                        }
                        break;
                    case "UMF":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        try {
                            future = service.submit(() -> {
                                System.out.print("Destination path: ");
                                destination.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        Set uploadFileSet = new HashSet<String>();
                        do {
                            System.out.print("Path of file to upload: ");
                            try {
                                future = service.submit(() -> {
                                    source.setStringValue(scan.nextLine());
                                    return 0;
                                });
                                future.get(30, TimeUnit.MINUTES);
                            } catch(TimeoutException | InterruptedException | ExecutionException e) {
                                System.out.println("\nTimed OUT!");
                                client.logoff();
                                System.exit(0);
                            }


                            uploadFileSet.add(source);

                            System.out.print("Upload another file? (Y/n)");
                            sentinel.setStringValue(scan.nextLine().toUpperCase());
                        } while(sentinel.getStringValue().equals("N"));

                        client.uploadMultipleFiles(Arrays.copyOf(uploadFileSet.toArray(), uploadFileSet.toArray().length, String[].class), destination.getStringValue());
                        break;
                    case "RRF":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.print("Please enter the path to the remote file you would like to delete: ");
                        try {
                            future = service.submit(() -> {
                                path.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.removeFile(path.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "MRD":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.print("Please enter the path of the directory you would like to create: ");
                        try {
                            future = service.submit(() -> {
                                path.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.makeDirectory(path.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "MRDFP":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.print("Please enter the path of the directory chain you would like to create: ");
                        try {
                            future = service.submit(() -> {
                                path.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.makeDirectoryWithPath(path.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while listing remote files:" + e);
                        }
                        break;
                    case "CFP":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.print("Please enter the path of the file you wish to change: ");
                        try {
                            future = service.submit(() -> {
                                path.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        System.out.print("Please enter the the new file permissions (e.g. 777, 600, 444): ");
                        try {
                            future = service.submit(() -> {
                                permissions.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.changeRemotePermissions(path.getStringValue(), permissions.getStringValue());
                        } catch(IOException e) {
                            System.err.println("Error occurred while changing  remote file permissions:" + e);
                        }
                        break;
                    case "DRD":
                        System.out.println(commands.get(menu_choice.getStringValue()));
                        System.out.print("Please enter the path to the remote directory you would like to delete: ");
                        try {
                            future = service.submit(() -> {
                                path.setStringValue(scan.nextLine());
                                return 0;
                            });
                            future.get(30, TimeUnit.MINUTES);
                        } catch(TimeoutException | InterruptedException | ExecutionException e) {
                            System.out.println("\nTimed OUT!");
                            client.logoff();
                            System.exit(0);
                        }

                        try {
                            client.deleteDirectory(path.getStringValue());
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
