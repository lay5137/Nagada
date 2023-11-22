package nagadaServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;

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

    Connection con = null;
    Statement stmt = null;
    String url = "jdbc:mysql://localhost:3306/Nagada?serverTimezone=UTC";
    String user = "root";
    String passwd = "1234";
    PreparedStatement ps;
    ResultSet rs;

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

        // MYSQL과 연동
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, passwd);
            stmt = con.createStatement();
            System.out.println("MySQL 서버 연동 성공");
        } catch(ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch(SQLException e) {
            System.out.println("DB 연결 오류");
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
        String id = parts[1];
        String pw = parts[2];

        //TODO: DB 구현
        boolean loginSuccessful = logincheck(id, pw); // 임시로 항상 성공으로 설정
        if (loginSuccessful) {
            sendMessage("LOGIN_SUCCESS");
            userID = parts[1];
        } else {
            sendMessage("LOGIN_FAIL");
        }
    }


    private void handleIdCheck(String[] parts) {
        if (parts.length < 2) {
            sendMessage("ID_CHECK_FAIL|Invalid message format");
            return;
        }
        String id = parts[1];
        // TODO: 데이터베이스 ID 중복 검사 로직 구현
        boolean idAvailable = getIdByCheck(id); // 임시로 항상 사용 가능으로 설정
        if (idAvailable) {
            sendMessage("ID_AVAILABLE");
        } else {
            sendMessage("ID_UNAVAILABLE");
        }
    }


    private void handleSignup(String[] parts) {
        if (parts.length < 9) {
            sendMessage("SIGNUP_FAIL|Invalid message format");
            return;
        }
        String id = parts[1];
        String pw = parts[2];
        String name = parts[3];
        String gender = parts[4];
        String age = parts[5];
        String phone = parts[6];
        String acc = parts[7];
        String bank = parts[8];
        // TODO: 회원가입 로직 구현
        boolean isFinishSignup = joinCheck(id, pw, name, gender, age, phone, acc, bank); // 임시로 항상 성공으로 설정
        if (isFinishSignup) {
            sendMessage("SIGNUP_SUCCESS");
        } else {
            sendMessage("SIGNUP_FAIL");
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
        // TODO: 지원 로직 구현
        String response = "CANCEL_SUCCESS";
        sendMessage(response);
    }



    // Getter for userID
    public String getUserID() {
        return userID;
    }

    //로그인 DB
    boolean logincheck(String _i, String _p) {
        boolean flag = false;

        String id = _i;
        String pw = _p;

        try {
            String checkingStr = "SELECT pw FROM Client WHERE ID='" + id + "'";
            ResultSet result = stmt.executeQuery(checkingStr);

            int count = 0;
            while(result.next()) {
                if(pw.equals(result.getString("pw"))) {
                    flag = true;
                    System.out.println("로그인 성공");
                }

                else {
                    flag = false;
                    System.out.println("로그인 실패");
                }
                count++;
            }
        } catch(Exception e) {
            flag = false;
            System.out.println("로그인 실패 > " + e.toString());
        }

        return flag;
    }

    //회원가입 DB
    boolean joinCheck(String _i, String _p, String _n, String _g, String _a, String _ph, String _ac, String _b) {
        boolean flag = false;

        String id = _i;
        String pw = _p;
        String nm = _n;
        String gd = _g;
        String ag = _a;
        String ph = _ph;
        String ac = _ac;
        String bk = _b;

        try {
            String insertStr = "INSERT INTO Client VALUES('" + id + "', '" + pw + "' , '" + nm + "', '" + gd + "', '" + ag + "', '" + ph + "', '" + ac + "', '" + bk + "')";
            stmt.executeUpdate(insertStr);

            flag = true;
            System.out.println("회원가입 성공");
        } catch(Exception e) {
            flag = false;
            System.out.println("회원가입 실패 > " + e.toString());
        }

        return flag;
    }

    //아이디 중복 확인 DB
    public boolean getIdByCheck(String id) {
        boolean result = true;

        try {
            ps = con.prepareStatement("SELECT * FROM Client WHERE ID=?");
            ps.setString(1, id.trim());
            rs = ps.executeQuery(); //실행
            if (rs.next())
                result = false; //레코드가 존재하면 false

        } catch (SQLException e) {
            System.out.println(e + "=>  getIdByCheck fail");
        }

        return result;

    }

}