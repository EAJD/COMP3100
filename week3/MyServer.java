import java.io.*;
import java.net.*;

public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6666);
            Socket s = ss.accept();
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                
            String str = "";
            while(!str.equals("BYE")) {
                str = din.readUTF();
                System.out.println("Client says: " + str);
                if (str.equals("HELLO")) {
                    dout.writeUTF("G'DAY");
                    dout.flush();
                    System.out.println("Repling: " + "G'DAY");
                }
            }

            dout.writeUTF("BYE");
            dout.flush();
            System.out.println("Repling: " + "BYE");

            din.close();
            s.close();
            ss.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}