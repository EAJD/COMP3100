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

        for (String str: servers){
            String[] server = str.split(" ");
            if (Integer.parseInt(server[4]) > size){
                size = Integer.parseInt(server[4]);
                result = str;
            }
        }

        return result;
    }

    public void run() {
        try {
            String user = "Ethan";
            String reply = dialogue("HELO\n");
            reply = dialogue("AUTH " + user + "\n");

            reply = dialogue("REDY\n");
            String jobID = reply.split(" ")[2];

            reply = dialogue("GETS All\n");

            int length = Integer.parseInt(reply.split(" ")[1]);

            send("OK\n");

            String[] servers = receiveLines(length);

            reply = dialogue("OK\n");

            String[] largestServer = FindLargest(servers).split(" ");

            send(String.format("SCHD %s %s %s\n", jobID, largestServer[0], largestServer[1]));
            reply = receive();

            send("QUIT\n");
            reply = receive();

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
