package nagadaClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {

    private JTextField textID;
    private JPasswordField textPW;
    private JButton buttonLogin;
    private JButton buttonSignUp;

    private Client client;

    public LoginGUI(Client client) {
        super("이용자 로그인 화면");
        this.client = client;

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

                // Check if ID and PW are not empty
                if (!enteredID.isEmpty() && enteredPW.length > 0) {
                    // Construct a message to send to the server
                    String loginMessage = "LOGIN|" + enteredID + "|" + new String(enteredPW);
                    // 서버로 메시지 보냄
                    client.sendMessage(loginMessage);

                    // 서버에서 응답 올때까지 좀만 기다리기
                    try {
                        Thread.sleep(500);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    // Wait for the result from the server
                    boolean loginSuccess = waitForLoginResult();

                    if (loginSuccess) {
                        JOptionPane.showMessageDialog(
                                LoginGUI.this,
                                "로그인 성공"
                        );
                        setVisible(false);
                        ClientGUI clientGUI = new ClientGUI(client);
                        clientGUI.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(
                                LoginGUI.this,
                                "로그인 실패"
                        );
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
                JoinGUI joinGUI = new JoinGUI(client);
                joinGUI.setVisible(true);
            }
        });
    }

    // Wait for the login result from the server
    private boolean waitForLoginResult() {
        long startTime = System.currentTimeMillis();
        long timeout = 2000; // Set a timeout of 3 seconds

        while (System.currentTimeMillis() - startTime < timeout) {
            if (client.getReceivedMessage() != null) {
                // Check the received message and return the result
                return client.getReceivedMessage().equals("LOGIN_SUCCESS");
            }
        }

        // Timeout reached, return false
        return false;
    }

}



