import java.io.*;
import java.net.*;

public class MyClient {

    private static void send(DataOutputStream dout, String message) {
        try {
            System.out.println("Sending: " + message);
            dout.write(message.getBytes());
            dout.flush();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private static String receive(BufferedReader in) {
        try {
            String reply = in.readLine();
            System.out.println("Server said: " + reply + "\n");
            return reply;
        }
        catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String user = "Ethan";

            send(dout, "HELO\n");
            String reply = receive(in);

            if (!reply.equals("OK")) {
                return;
            }

            send(dout, "AUTH " + user + "\n");
            reply = receive(in);

            if (!reply.equals("OK")) {
                return;
            }

            send(dout, "REDY\n");
            reply = receive(in);
            String jobID = reply.split(" ")[2];

            send(dout, "GETS All\n");
            reply = receive(in);

            int num = Integer.parseInt(reply.split(" ")[1]);
            String[] servers = new String[num];

            send(dout, "OK\n");
            reply = receive(in);

            int index = 0;
            while(!reply.equals(".")){
                servers[index] = reply;
                index++;
                send(dout, "OK\n");
                reply = receive(in);
            }
            String[] largestServer = FindLargest(servers).split(" ");

            send(dout, String.format("SCHD %s %s %s\n", jobID, largestServer[0], largestServer[1]));
            reply = receive(in);

            // send(dout, "REDY\n");
            // reply = receive(in);

            // while(!reply.equals("NONE")){
            //     jobID = reply.split(" ")[2];

            //     send(dout, String.format("SCHD %s %s %s\n", jobID, largestServer[0], largestServer[1]));
            //     reply = receive(in);

            //     send(dout, "REDY\n");
            //     reply = receive(in);
            // }

            send(dout, "QUIT\n");
            reply = receive(in);

            s.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private static String FindLargest(String[] servers){
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
}  