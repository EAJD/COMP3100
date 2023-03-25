import java.io.*;
import java.net.*;

public class MyClient {

    private Socket s;
    private DataOutputStream output;
    private BufferedReader input;

    private boolean log;

    public MyClient(boolean log) {
        try {
            s = new Socket("localhost", 50000);
            output = new DataOutputStream(s.getOutputStream());
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }
        catch (Exception e) {
            System.out.println(e);
        }
        this.log = log;
    }

    private void send(String message) {
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

    private String receive() {
        try {
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

    private String[] receiveLines(int length) {
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

    private String dialogue(String message) {
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

    private String FindLargest(String[] servers){
        int size = 0;
        String result = null;

        for (String server: servers){
            if (Integer.parseInt(server.split(" ")[4]) > size){
                size = Integer.parseInt(server.split(" ")[4]);
                result = server;
            }
        }

        return result;
    }

    public void run() {
        try {
            // Handshake
            String user = "Ethan";
            String reply = dialogue("HELO\n");
            reply = dialogue("AUTH " + user + "\n");

            // Get first job
            reply = dialogue("REDY\n");
            String[] job = reply.split(" ");

            // Get Servers
            reply = dialogue("GETS All\n");
            int length = Integer.parseInt(reply.split(" ")[1]);
            send("OK\n");
            String[] servers = receiveLines(length);
            reply = dialogue("OK\n");

            // Get largest server
            String[] largestServer = FindLargest(servers).split(" ");

            while(!reply.equals("NONE")) {
                // Schedule job
                if (job != null) {
                    dialogue(String.format("SCHD %s %s %s\n", job[2], largestServer[0], largestServer[1]));
                }
                
                // Get next job
                reply = dialogue("REDY\n");
                job = reply.split(" ");
                if (!job[0].equals("JOBN")) {
                    job = null;
                }
            }

            // Exit
            dialogue("QUIT\n");

            s.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        boolean log = false;

        if (args.length > 1) {
            if (args[0].equals("-v") && args[1].equals("brief")) {
                log = true;
            }
            else {
                System.out.println("Incorrect arguements");
                return;
            }
        }

        MyClient client = new MyClient(log);
        client.run();
    }
}