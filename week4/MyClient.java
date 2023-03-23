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

            send(dout, "QUIT\n");
            
            reply = receive(in);

        } catch(Exception e) {
            System.out.println(e);
        }
    } 
}  