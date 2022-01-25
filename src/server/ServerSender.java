package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerSender implements Runnable {

    private static final String address = "127.0.0.1";
    private static final int PORT = 23456;
    private final String command;
    public boolean forceKill = false;


    public ServerSender(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(InetAddress.getByName(address), PORT)) {
            try (DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                if (!command.contains("\"type\":\"exit\"")) {
                    try {
                        output.writeUTF(command); // sending message to the server
                        System.out.println("Sent: exit");
                        System.out.println("Received: OK");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String receivedMsg = null; // response message
                    try {
                        receivedMsg = input.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Sent: " + command);
                    System.out.println("Received: " + receivedMsg);
                } else {
                    try {
                        output.writeUTF("exit");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Sent: exit");
                    System.out.println("Received: OK");
                    this.forceKill = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
