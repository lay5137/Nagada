package nagadaServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    ServerSocket serverSocket;   // 서버소켓
    static final int PORT = 8000;   // 포트번호
    Socket childSocket;   // 자식스레드에게 넘겨줄 소켓

    // 여러 클라이언트를 동시에 처리하기 위해서 리스트 생성해서 연결된 클라이언트 저장
    List<ChildThread> childList = new ArrayList<>();
    // 스케줄러를 선언
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 생성자
    public Server() {

        new LoginGUI();      // 서버 열기 전 관리자 로그인 실행

        try {
            serverSocket = new ServerSocket(PORT);   // 서버 열기
            System.out.println("서버 구동");
            startPeriodicBroadcast();   // 주기적인 메시지 전송 시작
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        System.out.println("서버는 클라이언트 소켓의 접속 요청을 기다림");

        while(true) {
            try {
                childSocket = serverSocket.accept();   // accept -> 1. 소켓 접속 대기, 2. 소켓 접속 허락

                ChildThread childThread = new ChildThread(childSocket, this);
                addClient(childThread);
                Thread t = new Thread(childThread);
                t.start();   // 자식 스레드 실행

            } catch(Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }




    // **********브로드캐스트**********
    // 주기적으로 broadcast() 메소드를 호출하는 메소드를 추가합니다.
    private void startPeriodicBroadcast() {
        final Runnable broadcaster = new Runnable() {
            public void run() {
                // broadcast 메소드를 사용해 모든 클라이언트에게 메시지를 보냅니다.
                // 여기서 혼잡 정보를 보내면 됨
                broadcast("혼잡");
            }
        };

        // 스케줄러를 시작하여 매 10초마다 broadcaster 작업을 실행합니다.
        scheduler.scheduleAtFixedRate(broadcaster, 0, 10, TimeUnit.SECONDS);
    }


    // 서버 종료 시 호출해야 하는 메소드
    public void stopServer() {
        scheduler.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // **********공유자원**********

    // 모든 클라이언트에게 내용 전달
    public synchronized void broadcast(String message) {
        // 연결된 클라이언트들에게 모두 보냄
        for (int i = 0; i < childList.size(); i++) {
            ChildThread child = (ChildThread) childList.get(i);
            child.sendMessage(message + "|BROADCAST");
        }
    }


    // 클라이언트가 퇴장 시 호출되며, 리스트에 클라이언트 담당 쓰레드 제거
    public synchronized void removeClient(ChildThread child) {
        childList.remove(child);
        System.out.println(childSocket.getInetAddress() + "님이 종료");
        System.out.println("현재 인원 : " + childList.size() + "명");
    }


    // 클라이언트 입장 시 호출되며, 리스트에 클라이언트 담당 쓰레드 저장
    public synchronized void addClient(ChildThread child) {
        // 리스트에 ChildThread 객체 저장
        childList.add(child);
        System.out.println(childSocket.getInetAddress() + "님이 접속");
        System.out.println("현재 인원 : " + childList.size() + "명");
    }




}