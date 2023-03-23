import java.io.*;
import java.net.*;

public class MyClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 6666);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                
            dout.writeUTF("HELLO");
            dout.flush();

            String str = din.readUTF();
            if (str.equals("G'DAY")) {
                dout.writeUTF("BYE");
                dout.flush();
            }

            str = din.readUTF();
            if (str.equals("BYE")) {
                din.close();
                s.close();
                System.out.println("Server said: BYE");
            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }  
}  