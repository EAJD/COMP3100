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

    public void ma() {
        // My algorithm for stage 2
        
        // Handshake
        String reply = Dialogue("HELO\n");
        String user = System.getProperty("user.name");
        reply = Dialogue("AUTH " + user + "\n");

        reply = Dialogue("REDY\n");
        
        while (!reply.equals("NONE")) {
            switch (reply.split(" ")[0]) {
                case "JOBN": // if the reply is a jobn
                case "JOBP": // or a jobp
                    String[] job = reply.split(" ");
                    ScheduleJob(job);
    
                    reply = Dialogue("REDY\n");
                    break;

                case "CHKQ":
                    Dialogue("DEQJ GQ 0\n");
                    reply = Dialogue("REDY\n");
                    break;
    
                case "JCPL":
                    reply = Dialogue("REDY\n");
                    break;
            }
        }

        // Exit
        Dialogue("QUIT\n");
        try {
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void ScheduleJob(String[] job) {
        String reply = "";
        // Get Capable Servers
        reply = Dialogue(String.format("GETS Capable %s %s %s\n", job[4], job[5], job[6]));
        int length = Integer.parseInt(reply.split(" ")[1]);
        Send("OK\n");
        String[] servers = ReceiveLines(length);
        reply = Dialogue("OK\n");

        boolean scheduled = false;
        for (int i = 0; i < servers.length; i++) {
            String[] server = servers[i].split(" ");
            if (Integer.parseInt(server[4]) >= Integer.parseInt(job[4])) {
                Dialogue(String.format("SCHD %s %s %s\n", job[2], server[0], server[1]));
                scheduled = true;
                break;
            }
        }
        int maxJobs = 1;
        while (!scheduled) {
            for (int i = 0; i < servers.length; i++) {
                String[] server = servers[i].split(" ");
                int serverJobs = Integer.parseInt(server[7]) + Integer.parseInt(server[8]);
                if (serverJobs < maxJobs) {
                    Dialogue(String.format("SCHD %s %s %s\n", job[2], server[0], server[1]));
                    scheduled = true;
                    break;
                }
            }
            maxJobs++;
        }
    }

    public static void main(String[] args) {
        boolean log = false;

        String argString = String.join(" ", args);
        if (argString.contains("-v brief")) {
            log = true; // enables logging in the brief style
        }

        MyClient client = new MyClient(log);
        client.ma();
    }
}
