package nagadaClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private Socket socket;
    String clientIP;

    private BufferedReader reader;
    private PrintWriter writer;
    private String receivedMessage;

    public Client(String ip, int port) {
        // 플래그가 true 인 동안 루프에서 서버의 메시지를 읽으면서 서버와의 지속적인 통신을 담당
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            clientIP = socket.getLocalAddress().getHostAddress();

            new LoginGUI(this);		// 스윙 실행

            while (true) {
                String message = reader.readLine();

                if(message == null) {
                    System.out.println("Server Not Open");
                    break;
                }

                receivedMessage = message;
                System.out.println("Client(" + clientIP + ") Receive: " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
            System.exit(0);
        }

    }


    // Method to send a message through the socket
    public synchronized void sendMessage(String message) {
        writer.println(message);
        writer.flush();
        System.out.println("Client(" + clientIP + ") Send: " + message);
    }


    // 참조로 GUI에서 가져다 쓰기 위함
    public synchronized String getReceivedMessage() {
        return receivedMessage;
    }


    private void closeResources() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}






