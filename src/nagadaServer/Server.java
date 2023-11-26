package nagadaServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

public class Server {

    ServerSocket serverSocket;	// 서버소켓
    static final int PORT = 8000;	// 포트번호
    Socket childSocket;	// 자식스레드에게 넘겨줄 소켓

    // 여러 클라이언트를 동시에 처리하기 위해서 리스트 생성해서 연결된 클라이언트 저장
    List<ChildThread> childList = new ArrayList<>();
    // 스케줄러를 선언
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 날짜별 주간 및 야간 지원자 명단 관리 맵
    private Map<String, List<String>> dayApplicantNames = new HashMap<>();
    private Map<String, List<String>> nightApplicantNames = new HashMap<>();

    // 날짜별 주간 및 야간 지원자 수 관리 맵
    private Map<String, Integer> dayApplicantCounts = new HashMap<>();
    private Map<String, Integer> nightApplicantCounts = new HashMap<>();

    private ServerGUI serverGUI;

    Connection conn;
    String url = "jdbc:mysql://localhost:3306/nagada?serverTimezone=UTC";

    String databaseID = "root";
    String databasePW = "1234";

    Statement stmt;
    ResultSet result;

    // 생성자
    public Server() {

        new LoginGUI(this);		// 서버 열기 전 관리자 로그인 실행

        try {
            serverSocket = new ServerSocket(PORT);	// 서버 열기
            System.out.println("서버가 열림");
            startPeriodicBroadcast();	// 주기적인 메시지 전송 시작
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        while(true) {
            try {
                childSocket = serverSocket.accept();	// accept -> 1. 소켓 접속 대기, 2. 소켓 접속 허락

                ChildThread childThread = new ChildThread(childSocket, this);
                addClient(childThread);
                Thread t = new Thread(childThread);
                t.start();	// 자식 스레드 실행

            } catch(Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }


    // 로그인 성공 시 호출되는 메소드
    public void onLoginSuccess() {
        serverGUI = new ServerGUI(this); // 로그인 성공 후 ServerGUI 생성 및 표시
        serverGUI.setVisible(true);
    }


    // **********브로드캐스트**********
    // 주기적으로 broadcast() 메소드를 호출하는 메소드를 추가합니다.
    private void startPeriodicBroadcast() {
        final Runnable broadcaster = new Runnable() {
            public void run() {
                // broadcast 메소드를 사용해 모든 클라이언트에게 메시지를 보냅니다.
                // 여기서 혼잡 정보를 보내면 됨
                broadcast("11/20|초과|충족|미만|초과|충족|충족|충족|초과|충족|미만|초과|미만|미만|미만");

                scheduler.schedule(new Runnable() {
                    public void run() {
                        // Update all clients with their personalized status
                        updateAllClientsWithPersonalizedStatus();
                    }
                }, 30, TimeUnit.SECONDS);

            }
        };

        // 스케줄러를 시작하여 매 1분마다 broadcaster 작업을 실행합니다.
        scheduler.scheduleAtFixedRate(broadcaster, 0, 60, TimeUnit.SECONDS);
    }


    public void sendPersonalizedStatus(ChildThread client) {
        String userId = client.getUserID();
        String personalizedMessage = generateStatusMessageForUser(userId);
        client.sendMessage("PERSONAL|" + personalizedMessage);
    }


    private String generateStatusMessageForUser(String userId) {
        StringBuilder statusMessage = new StringBuilder();
        LocalDate today = LocalDate.now(); // Get today's date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // Append today's date as the starting date of the status message
        statusMessage.append(today.format(formatter));
        for (int i = 0; i < 7; i++) {
            String dayStatus = getUserDayStatus(userId, i, "day");
            statusMessage.append("|").append(dayStatus);
        }

        for (int i = 0; i < 7; i++) {
            String nightStatus = getUserDayStatus(userId, i, "night");
            statusMessage.append("|").append(nightStatus);
        }

        return statusMessage.toString();
    }


    private String getUserDayStatus(String userId, int dayIndex, String period) {
        Map<String, List<String>> applicantNames = (period.equals("day")) ? dayApplicantNames : nightApplicantNames;
        String dayKey = getDayKey(dayIndex); // 날짜 키 생성

        // 해당 날짜에 지원한 사람들이 누구인지
        List<String> applicants = applicantNames.getOrDefault(dayKey, new ArrayList<>());
        System.out.print(applicants);

        // 명단 정렬 (필요한 경우, 쓸지는 모르겠다)
        //sortApplicantList(applicantNames, dayKey);

        int position = applicants.indexOf(userId);
        System.out.print(position);

        // 사용자의 상태 결정
        if (position == 0) {	// -1이 아예없느 상태 0~순서
            return "가확정";
        } else if (position == 1) {
            return "예비";
        } else {
            return "대기";
        }
    }

    private String getDayKey(int dayIndex) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(dayIndex);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return targetDate.format(formatter);
    }

    public void updateAllClientsWithPersonalizedStatus() {
        synchronized (childList) {
            for (ChildThread client : childList) {
                sendPersonalizedStatus(client);
            }
        }
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


    // 지원자 정보를 처리하는 메소드
    public void processAddApplication(String period, String date, String userId) {
        // 명단에 지원자 추가 및 지원자 수 증가는 동기화된 블록 내에서 수행
        synchronized (this) {
            List<String> applicantNames;
            Map<String, Integer> applicantCounts;

            if ("주간".equals(period)) {
                applicantNames = dayApplicantNames.computeIfAbsent(date, k -> new ArrayList<>());
                applicantCounts = dayApplicantCounts;
            } else {
                applicantNames = nightApplicantNames.computeIfAbsent(date, k -> new ArrayList<>());
                applicantCounts = nightApplicantCounts;
            }

            applicantNames.add(userId);	// 명단 넣는 코드 명단이 정렬되어야 함
            int newCount = applicantCounts.getOrDefault(date, 0) + 1;
            applicantCounts.put(date, newCount);

            // GUI 업데이트
            updateApplicantNumbers(date, period, newCount);
        }
    }


    // 지원 취소 처리 메소드
    public void processDeleteApplication(String period, String date, String userId) {
        synchronized (this) {
            List<String> applicantNames;
            Map<String, Integer> applicantCounts;

            if ("주간".equals(period)) {
                applicantNames = dayApplicantNames.getOrDefault(date, new ArrayList<>());
                applicantCounts = dayApplicantCounts;
            } else {
                applicantNames = nightApplicantNames.getOrDefault(date, new ArrayList<>());
                applicantCounts = nightApplicantCounts;
            }

            // 지원자 명단에서 사용자 ID 제거
            applicantNames.remove(userId);

            // 지원자 수 감소
            int newCount = applicantCounts.getOrDefault(date, 0) - 1;
            newCount = Math.max(newCount, 0); // 음수가 되지 않도록 보장
            applicantCounts.put(date, newCount);

            // GUI 업데이트
            updateApplicantNumbers(date, period, newCount);
        }
    }


    // 지원자 수 업데이트 및 GUI 업데이트 메소드
    public void updateApplicantNumbers(String date, String period, int count) {
        Map<String, Integer> applicantCounts = period.equals("주간") ? dayApplicantCounts : nightApplicantCounts;
        applicantCounts.put(date, count);
        serverGUI.updateApplicantNumbers(dayApplicantCounts, nightApplicantCounts);
        serverGUI.updatePanels();
    }


    // 상세보기 기능을 위한 메서드
    public void showApplicantDetails(String date, String period) {
        List<String> applicantIds = getApplicantIds(date, period);
        List<Applicant> detailsList = getApplicant(applicantIds);

        // ServerGUI의 updateApplicantTable 메서드를 호출하여 지원자 정보를 표시합니다.
        serverGUI.updateApplicantTable(detailsList, date, period);
    }

    // 해당 날짜와 주야간에 따른 userId 목록을 가져오는 메서드
    private List<String> getApplicantIds(String date, String period) {
        Map<String, List<String>> applicantMap = period.equals("주간") ? dayApplicantNames : nightApplicantNames;
        return applicantMap.getOrDefault(date, new ArrayList<>());
    }

    // 데이터베이스에서 userId에 해당하는 상세 정보를 가져오는 메서드
    private List<Applicant> getApplicant(List<String> userIds) {
        List<Applicant> detailsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, databaseID, databasePW);
            System.out.println("DB연결완료");

            // 각 userId에 대해 정보 검색
            for (String userId : userIds) {
                String query = "SELECT name, age, gender, phone FROM user WHERE id = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userId);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String age = rs.getString("age");
                    String gender = rs.getString("gender");
                    String phone = rs.getString("phone");

                    detailsList.add(new Applicant(userId, name, age, gender, phone));
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return detailsList;
    }



    // 모든 클라이언트에게 내용 전달
    public synchronized void broadcast(String message) {
        // 연결된 클라이언트들에게 모두 보냄
        for (int i = 0; i < childList.size(); i++) {
            ChildThread child = (ChildThread) childList.get(i);
            child.sendMessage("BROADCAST|" + message);
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
