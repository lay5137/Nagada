package nagadaServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginGUI extends JFrame {

    private JTextField textID;
    private JPasswordField textPW;
    private JButton buttonLogin;
    private JButton buttonSignUp;
    private Server server;

    Connection conn;
    String url = "jdbc:mysql://localhost:3306/nagada?serverTimezone=UTC";

    String databaseID = "root";
    String databasePW = "1234";

    Statement stmt;
    ResultSet result;

    public LoginGUI(Server server) {
        super("관리자 로그인 화면");
        this.server = server;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Components
        JLabel labelNAGADA = new JLabel("나가다", SwingConstants.CENTER);
        labelNAGADA.setFont(new Font(labelNAGADA.getFont().getName(), labelNAGADA.getFont().getStyle(), 30));
        labelNAGADA.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel labelID = new JLabel("ID", SwingConstants.CENTER);
        labelID.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        textID = new JTextField();
        textID.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel labelPW = new JLabel("PW", SwingConstants.CENTER);
        labelPW.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        textPW = new JPasswordField();
        textPW.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        buttonLogin = new JButton("로그인");
        buttonSignUp = new JButton("회원가입");

        // Set component bounds (x, y, width, height)
        int centerX = 120;
        int yOffset = -20; // 전체적으로 위로 올릴 양

        // "나가다" 레이블
        labelNAGADA.setBounds(centerX - 80, 40, 285, 60);

        // ID 레이블과 입력 필드
        int labelIDY = labelNAGADA.getY() + labelNAGADA.getHeight() + 10; // 나가다 레이블 아래로 30px 여백을 줍니다.
        labelID.setBounds(centerX - 80, labelIDY, 80, 40);
        textID.setBounds(centerX + 5, labelIDY, 200, 40);

        // PW 레이블과 입력 필드
        int labelPWY = labelID.getY() + labelID.getHeight() + 10; // ID 레이블 아래로 20px 여백을 줍니다.
        labelPW.setBounds(centerX - 80, labelPWY, 80, 40);
        textPW.setBounds(centerX + 5, labelPWY, 200, 40);

        // 로그인 버튼과 회원가입 버튼
        int buttonY = labelPW.getY() + labelPW.getHeight() + 10; // PW 레이블 아래로 20px 여백을 줍니다.
        buttonLogin.setBounds(centerX - 80, buttonY, 140, 40);
        buttonSignUp.setBounds(centerX + 65, buttonY, 140, 40);

        // Add components to the frame
        add(labelNAGADA);
        add(labelID);
        add(textID);
        add(labelPW);
        add(textPW);
        add(buttonLogin);
        add(buttonSignUp);

        // Set the size of the JFrame and make it visible
        setSize(380, 330);
        setLocationRelativeTo(null);
        setVisible(true);

        // Add ActionListeners
        addListeners();
    }


    public void addListeners() {

        // 로그인 버튼 눌렀을 때
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the entered ID and PW
                String enteredID = textID.getText();
                char[] enteredPW = textPW.getPassword();
                String enteredPWString = new String(enteredPW);

                // Check if ID and PW are not empty
                if (!enteredID.isEmpty() && enteredPW.length > 0) {

                    try {
                        // JDBC 드라이버 로드 및 데이터베이스 연결
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conn = DriverManager.getConnection(url, databaseID, databasePW);
                        System.out.println("DB연결완료");

                        // SQL 쿼리 실행
                        stmt = conn.createStatement();
                        String query = "SELECT * FROM user WHERE id = '" + enteredID + "' AND pw = '" + enteredPWString + "' AND position = '관리자'";
                        result = stmt.executeQuery(query);

                        boolean loginSuccessful = false;
                        if (result.next()) {
                            loginSuccessful = true; // 관리자 계정이 테이블에 존재하는 경우
                            System.out.println("MANAGER_LOGIN_SUCCESS");
                        } else {
                            System.out.println("MANAGER_LOGIN_FAIL");
                        }

                        // 로그인 결과에 따른 UI 피드백
                        if (loginSuccessful) {
                            JOptionPane.showMessageDialog(LoginGUI.this, "로그인 성공");
                            setVisible(false);
                            server.onLoginSuccess();
                        } else {
                            JOptionPane.showMessageDialog(LoginGUI.this, "로그인 실패");
                        }

                        // 자원 해제
                        result.close();
                        stmt.close();
                        conn.close();

                    } catch (ClassNotFoundException ex) {
                        System.out.println("JDBC 드라이버 로드 오류");
                    } catch (SQLException ex) {
                        System.out.println("DB 연결 오류");
                    }

                } else {
                    // Show an error message if ID or PW is empty
                    JOptionPane.showMessageDialog(
                            LoginGUI.this,
                            "아이디와 비밀번호를 모두 입력하세요"
                    );
                }
            }
        });

        // 회원가입 버튼 눌렀을 때
        buttonSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                JoinGUI joinGUI = new JoinGUI(server);
                joinGUI.setVisible(true);
            }
        });
    }


}


