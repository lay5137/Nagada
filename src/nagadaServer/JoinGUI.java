package nagadaServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JoinGUI extends JFrame {

    private JTextField textID;
    private JPasswordField textPW;
    private JTextField textName;
    private JTextField textGender;
    private JTextField textAge;
    private JTextField textPhone;
    private JTextField textAcc;
    private JTextField textBank;

    private JButton buttonCheck;
    private JButton buttonSignUp;
    private JButton buttonBack;

    private String confirmID = "";
    private Server server;

    Connection conn;
    String url = "jdbc:mysql://localhost:3306/nagada?serverTimezone=UTC";

    String databaseID = "root";
    String databasePW = "1234";

    Statement stmt;
    ResultSet result;

    public JoinGUI(Server server) {
        super("관리자 회원가입 화면");
        this.server = server;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // No layout manager (null layout)

        // Components
        JLabel labelSignup = new JLabel("회원가입", SwingConstants.CENTER); // Set text alignment to center
        labelSignup.setFont(new Font(labelSignup.getFont().getName(), labelSignup.getFont().getStyle(), 20)); // Set font size to 20
        labelSignup.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border
        addComponent(labelSignup, 30, 20, 360, 40);

        addLabeledField("ID", 30, 70);
        addLabeledField("PW", 30, 110);
        addLabeledField("이름", 30, 150);
        addLabeledField("성별", 30, 190);
        addLabeledField("나이", 30, 230);
        addLabeledField("전화번호", 30, 270);
        addLabeledField("계좌번호", 30, 310);
        textBank = addTextField(330, 310, 60, 30);

        buttonCheck = addButton("중복확인", 330, 70, 60, 30);
        buttonSignUp = addButton("회원가입", 30, 350, 175, 30);
        buttonBack = addButton("뒤로가기", 215, 350, 175, 30);

        // Set the size of the JFrame and make it visible
        setSize(435, 440);
        setLocationRelativeTo(null);
        setVisible(true);

        // Add ActionListeners
        addListeners();
    }


    private void addComponent(JComponent comp, int x, int y, int width, int height) {
        comp.setBounds(x, y, width, height);
        comp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(comp);
    }

    private JTextField addTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        addComponent(textField, x, y, width, height);
        return textField;
    }

    private void addLabeledField(String labelText, int x, int y) {
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        addComponent(label, x, y, 80, 30);
        JTextField textField = labelText.equals("PW") ? new JPasswordField() : new JTextField();
        addComponent(textField, x + 90, y, 200, 30);
        if (labelText.equals("PW")) {
            textPW = (JPasswordField) textField;
        } else {
            switch (labelText) {
                case "ID": textID = textField; break;
                case "이름": textName = textField; break;
                case "성별": textGender = textField; break;
                case "나이": textAge = textField; break;
                case "전화번호": textPhone = textField; break;
                case "계좌번호": textAcc = textField; break;
            }
        }
    }

    private JButton addButton(String buttonText, int x, int y, int width, int height) {
        JButton button = new JButton(buttonText);
        addComponent(button, x, y, width, height);
        return button;
    }



    private void addListeners() {

        // ID 중복확인 버튼 눌렀을 때
        buttonCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textID.getText();

                if (!id.isEmpty()) {
                    try {
                        // JDBC 드라이버 로드 및 데이터베이스 연결
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conn = DriverManager.getConnection(url, databaseID, databasePW);
                        System.out.println("DB연결완료");

                        // SQL 쿼리 실행
                        stmt = conn.createStatement();
                        String query = "SELECT COUNT(*) FROM user WHERE id = '" + id + "'";
                        result = stmt.executeQuery(query);

                        boolean idAvailable = true;
                        if (result.next() && result.getInt(1) > 0) {
                            idAvailable = false; // ID가 데이터베이스에 이미 존재함
                        }

                        // ID 사용 가능 여부에 따른 UI 피드백
                        if (idAvailable) {
                            JOptionPane.showMessageDialog(JoinGUI.this, "ID 사용 가능");
                            confirmID = id;
                        } else {
                            JOptionPane.showMessageDialog(JoinGUI.this, "ID 사용 불가");
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
                    JOptionPane.showMessageDialog(JoinGUI.this, "ID 입력하세요");
                }
            }
        });


        // 회원가입 버튼 눌렀을 때
        buttonSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textID.getText();
                char[] pw = textPW.getPassword();
                String name = textName.getText();
                String gender = textGender.getText();
                String age = textAge.getText();
                String phone = textPhone.getText();
                String acc = textAcc.getText();
                String bank = textBank.getText();

                if (!id.isEmpty() && pw.length > 0 && !name.isEmpty() && !gender.isEmpty() && !age.isEmpty() && !phone.isEmpty() && !acc.isEmpty() && !bank.isEmpty()) {

                    if (confirmID.equals(id)) {

                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            conn = DriverManager.getConnection(url, databaseID, databasePW);
                            System.out.println("DB연결완료");

                            String query = "INSERT INTO user (id, pw, name, gender, age, phone, acc, bank, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                                pstmt.setString(1, id);
                                pstmt.setString(2, new String(pw));
                                pstmt.setString(3, name);
                                pstmt.setString(4, gender);
                                pstmt.setString(5, age);
                                pstmt.setString(6, phone);
                                pstmt.setString(7, acc);
                                pstmt.setString(8, bank);
                                pstmt.setString(9, "관리자");
                                int result = pstmt.executeUpdate();

                                if (result > 0) {
                                    JOptionPane.showMessageDialog(JoinGUI.this, "회원가입이 완료되었습니다.");
                                    JoinGUI.this.dispose();
                                    new LoginGUI(server);
                                } else {
                                    JOptionPane.showMessageDialog(JoinGUI.this, "회원가입에 실패했습니다. 다시 시도해주세요.");
                                }
                            }
                        } catch (ClassNotFoundException ex) {
                            System.out.println("JDBC 드라이버 로드 오류");
                            JOptionPane.showMessageDialog(JoinGUI.this, "회원가입에 실패했습니다. 드라이버 로드 오류");
                        } catch (SQLException ex) {
                            System.out.println("DB연결오류");
                            JOptionPane.showMessageDialog(JoinGUI.this, "회원가입에 실패했습니다. 데이터베이스 오류");
                        } finally {
                            try {
                                if (conn != null) conn.close();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }

                    } else {
                        JOptionPane.showMessageDialog(JoinGUI.this, "중복확인을 하지 않은 아이디입니다.");
                    }

                } else {
                    JOptionPane.showMessageDialog(JoinGUI.this, "모든 칸을 채워주세요");
                }

            }
        });


        // 뒤로가기 버튼 눌렀을 때
        buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JoinGUI.this.dispose();
                new LoginGUI(server);
            }
        });
    }



}






