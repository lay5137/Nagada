package nagadaClient;

import nagadaServer.Server;

public class NagadaClientMain {

    public static void main(String[] args) {
        //new Client("LocalHost", 8000);

        // 서버 스레드와 클라이언트 스레드를 각각 생성하여 시작
        Thread serverThread = new Thread(() -> new Server());
        serverThread.start();

        Thread clientThread = new Thread(() -> new Client("LocalHost", 8000));
        clientThread.start();
        Thread clientThread2 = new Thread(() -> new Client("LocalHost", 8000));
        clientThread2.start();
    }

}
