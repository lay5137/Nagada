package nagadaClient;

import nagadaServer.Server;

public class NagadaClientMain {

    public static void main(String[] args) {
        // 서버와 클라이언트를 localhost에서 실행하는 메인 메서드

        // 서버 실행
        Thread serverThread = new Thread(() -> new nagadaServer.Server());
        serverThread.start();

        // 클라이언트 실행
        Thread clientThread1 = new Thread(() -> new nagadaClient.Client("10.2.17.9", 8000));
        clientThread1.start();

        //Thread clientThread2 = new Thread(() -> new nagadaClient.Client("192.168.219.105", 8000));
        //clientThread2.start();
    }
}
