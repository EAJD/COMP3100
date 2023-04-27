import java.io.*;
import java.net.*;

public class MyClient {

    private Socket s;
    private DataOutputStream output;
    private BufferedReader input;

    private boolean log;

    public MyClient(boolean log) {
        try {
            s = new Socket("localhost", 50000); // 50000 is default port for ds-sim
            output = new DataOutputStream(s.getOutputStream());
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }
        catch (Exception e) {
            System.out.println(e);
        }
        this.log = log;
    }

    private void Send(String message) {
        // Sends a message to the server
        try {
            if (log) {
                System.out.println("C SENT " + message.replace("\n", ""));
            }
            output.write(message.getBytes());
            output.flush();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private String[] ReceiveLines(int length) {
        // Receives length number of lines from the server
        try {
            String[] reply = new String[length];
            String line;

            int index = 0;
            while (index < length) {
                line = input.readLine();
                reply[index] = line;
                index++;
            }
            if (log) {
                System.out.println("C RCVD " + reply[0]);
                for (int i = 1; i < length; i++) {
                    System.out.println(reply[i]);
                }
            }
            return reply;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private String Dialogue(String message) {
        // Sends a message to and receives message from the server
        try {
            if (log) {
                System.out.println("C SENT " + message.replace("\n", ""));
            }
            output.write(message.getBytes());
            output.flush();

            String reply = input.readLine();
            if (log) {
                System.out.println("C RCVD " + reply);
            }
            return reply;
        }
        catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    private String FindLargestType(String[] servers) {
        // Returns the type with the most cpu cores
        int size = 0;
        String type = null;
        for (String server: servers) {
            if (Integer.parseInt(server.split(" ")[4]) > size) {
                size = Integer.parseInt(server.split(" ")[4]);
                type = server.split(" ")[0];
            }
        }
        return type;
    }

    private int FindTypeCount(String[] servers, String type) {
        // Counts how many servers match the given type
        int count = 0;
        for (String server: servers) {
            if (server.split(" ")[0].equals(type)) {
                count++;
            }
        }
        return count;
    }

    public void lrr() {
        // Runs the LRR scheduling algorithm
        try {
            // Handshake
            String reply = Dialogue("HELO\n");
            String user = System.getProperty("user.name");
            reply = Dialogue("AUTH " + user + "\n");

            // Get first job
            reply = Dialogue("REDY\n");
            String[] job = reply.split(" ");

            // Get Servers
            reply = Dialogue("GETS All\n");
            int length = Integer.parseInt(reply.split(" ")[1]);
            Send("OK\n");
            String[] servers = ReceiveLines(length);
            reply = Dialogue("OK\n");

            // Get largest server
            String largestType = FindLargestType(servers);
            int serverCount = FindTypeCount(servers, largestType);
            int currentServer = 0;

            while(!reply.equals("NONE")) {
                if (currentServer >= serverCount) {
                    // Go back to first server
                    currentServer = 0;
                }

                // Schedule job
                if (job != null) {
                    Dialogue(String.format("SCHD %s %s %s\n", job[2], largestType, currentServer));
                    currentServer++;
                }
                
                // Get next job
                reply = Dialogue("REDY\n");
                job = reply.split(" ");
                if (!job[0].equals("JOBN")) {
                    job = null;
                }
            }

            // Exit
            Dialogue("QUIT\n");

            s.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public void fc() {
        try {
            // Handshake
            String reply = Dialogue("HELO\n");
            String user = System.getProperty("user.name");
            reply = Dialogue("AUTH " + user + "\n");

            // Get first job
            reply = Dialogue("REDY\n");
            String[] job = reply.split(" ");

            while(!reply.equals("NONE")) {
                if (job != null) {
                    // Get Capable Servers
                    reply = Dialogue(String.format("GETS Capable %s %s %s\n", job[4], job[5], job[6]));
                    int length = Integer.parseInt(reply.split(" ")[1]);
                    Send("OK\n");
                    String[] servers = ReceiveLines(length);
                    reply = Dialogue("OK\n");
                    // First Capable
                    String[] server = servers[0].split(" ");

                    // Schedule job
                    Dialogue(String.format("SCHD %s %s %s\n", job[2], server[0], server[1]));
                }
                
                // Get next job
                reply = Dialogue("REDY\n");
                job = reply.split(" ");
                if (!job[0].equals("JOBN")) {
                    job = null;
                }
            }

            // Exit
            Dialogue("QUIT\n");

            s.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        boolean log = false;

        if (args.length > 2) {
            if (args[1].equals("-v") && args[2].equals("brief")) {
                log = true; // enables logging in the brief style
            }
            else {
                // There are arguments but they are not "-v brief"
                System.out.println("Incorrect arguements, use -v brief or no arguments");
                return;
            }
        }
        MyClient client = new MyClient(log);
        if (args.length > 0) {
            if (args[0].equals("llr")) {
                client.lrr();
            }
            if (args[0].equals("fc")) {
                client.fc();
            }
        }
    }
}