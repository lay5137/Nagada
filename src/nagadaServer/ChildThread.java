package nagadaServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChildThread implements Runnable {

    Socket childSocket;		// 클라이언트와 통신하기 위한 소켓
    Server server;

    // 문자열을 주고받기만 하는 간단한 통신에서 효율적
    // Object 스트림은 객체를 직렬화하여 전송하는 방법
    BufferedReader reader;
    PrintWriter writer;

    // 클라이언트로부터 전송받은 데이터를 저장하기 위한 변수
    String receivedMessage;
    // 로그인 성공 시 미리 저장해둠
    String userID;

    Connection conn;
    String url = "jdbc:mysql://localhost:3306/nagada?serverTimezone=UTC";

    String databaseID = "root";
    String databasePW = "1234";

    Statement stmt;
    ResultSet result;

    // 생성자
    public ChildThread(Socket socket, Server server) {
        childSocket = socket;	// 클라이언트와 통신할 수 있는 소켓 정보를 childSocket에 저장
        this.server = server;

        try {
            // Initialize reader and writer in the constructor
            reader = new BufferedReader(new InputStreamReader(childSocket.getInputStream()));
            // true: autoFlush 설정
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(childSocket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // t.start()하면 실행되는 부분
    @Override
    public void run() {
        try {
            while (true) {	// 여기서 반복문 걸려서 계속 연결된 상태로 유지되고 있음
                receivedMessage = reader.readLine();
                System.out.println("Server Receive: " + receivedMessage + "  @From:" + childSocket.getInetAddress());
                divideMessageAndProcess(receivedMessage);
            }
        } catch (Exception e) {
            server.removeClient(this);	// 클라이언트가 종료되면 해당 클라이언트 스레드를 서버에서 제거
        } finally {
            try {
                childSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 클라이언트로 메시지 출력
    public void sendMessage(String message) {
        System.out.println("Server Send: " + message  + "  @To:" + childSocket.getInetAddress());
        writer.println(message);
    }


    // 클라이언트로부터 받은 문자열 처리하고 응답 메시지 보내는 메소드
    public void divideMessageAndProcess(String message) {
        String[] parts = message.split("\\|");

        if (parts.length == 0) {
            // 오류 메시지 전송 또는 로깅
            sendMessage("ErrorMessage");
            return;
        }

        switch (parts[0]) {
            case "LOGIN":
                handleLogin(parts);
                break;
            case "ID_CHECK":
                handleIdCheck(parts);
                break;
            case "SIGNUP":
                handleSignup(parts);
                break;
            case "APPLY":
                handleApply(parts);
                break;
            case "CANCEL":
                handleCancel(parts);
                break;
            default:
                // 알 수 없는 명령에 대한 처리
                sendMessage("UNKNOWN_COMMAND");
                break;
        }
    }


    private void handleLogin(String[] parts) {
        if (parts.length < 3) {
            sendMessage("LOGIN_FAIL|Invalid message format");
            return;
        }
        String clientID = parts[1];
        String clientPW = parts[2];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, databaseID, databasePW);
            System.out.println("DB연결완료");

            stmt = conn.createStatement();
            String query = "SELECT * FROM user WHERE id = '" + clientID + "' AND pw = '" + clientPW + "' AND position = '사용자'";
            result = stmt.executeQuery(query);

            boolean loginSuccessful = false;
            if (result.next()) {
                loginSuccessful = true; // 사용자가 테이블에 존재하는 경우
                userID = clientID;      // 로그인 성공한 사용자 ID 저장
                System.out.println("USER_LOGIN_SUCCESS");
            } else {
                System.out.println("USER_LOGIN_FAIL");
            }

            if (loginSuccessful) {
                sendMessage("LOGIN_SUCCESS");
            } else {
                sendMessage("LOGIN_FAIL");
            }

            result.close();
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB연결오류");
        }

    }


    private void handleIdCheck(String[] parts) {
        if (parts.length < 2) {
            sendMessage("ID_CHECK_FAIL|Invalid message format");
            return;
        }
        String clientID = parts[1];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, databaseID, databasePW);
            System.out.println("DB연결완료");

            stmt = conn.createStatement();
            String query = "SELECT COUNT(*) FROM user WHERE id = '" + clientID + "'";
            result = stmt.executeQuery(query);

            boolean idAvailable = true;
            if (result.next() && result.getInt(1) > 0) {
                idAvailable = false; // ID가 데이터베이스에 이미 존재함
            }

            if (idAvailable) {
                sendMessage("ID_AVAILABLE");
            } else {
                sendMessage("ID_UNAVAILABLE");
            }

            result.close();
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }

    }


    private void handleSignup(String[] parts) {
        if (parts.length < 9) {
            sendMessage("SIGNUP_FAIL|Invalid message format");
            return;
        }
        String clientID = parts[1];
        String clientPW = parts[2];
        String clientName = parts[3];
        String clientGender = parts[4];
        String clientAge = parts[5];
        String clientPhone = parts[6];
        String clientAcc = parts[7];
        String clientBank = parts[8];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, databaseID, databasePW);
            System.out.println("DB연결완료");

            String query = "INSERT INTO user (id, pw, name, gender, age, phone, acc, bank, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, clientID);
                pstmt.setString(2, clientPW);
                pstmt.setString(3, clientName);
                pstmt.setString(4, clientGender);
                pstmt.setString(5, clientAge);
                pstmt.setString(6, clientPhone);
                pstmt.setString(7, clientAcc);
                pstmt.setString(8, clientBank);
                pstmt.setString(9, "사용자");
                int result = pstmt.executeUpdate();

                if (result > 0) {
                    sendMessage("SIGNUP_SUCCESS");
                } else {
                    sendMessage("SIGNUP_FAIL");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
            sendMessage("SIGNUP_FAIL|Driver load error");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            sendMessage("SIGNUP_FAIL|Database error");
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleApply(String[] parts) {
        if (parts.length < 3) {
            sendMessage("APPLY_FAIL|Invalid message format");
            return;
        }
        String dayTime = parts[1]; // "주간" 또는 "야간"
        String date = parts[2]; // 날짜
        server.processAddApplication(dayTime, date, userID);
        String response = "APPLY_SUCCESS";
        sendMessage(response);
    }


    private void handleCancel(String[] parts) {
        if (parts.length < 3) {
            sendMessage("CANCEL_FAIL|Invalid message format");
            return;
        }
        String dayTime = parts[1]; // "주간" 또는 "야간"
        String date = parts[2];  // 날짜
        server.processDeleteApplication(dayTime, date, userID);
        String response = "CANCEL_SUCCESS";
        sendMessage(response);
    }



    // Getter for userID
    public String getUserID() {
        return userID;
    }


}




