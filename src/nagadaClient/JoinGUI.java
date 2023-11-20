package nagadaClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinGUI extends JFrame {

    private Client client;

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

    public JoinGUI(Client client) {
        super("이용자 회원가입 화면");
        this.client = client;

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
                    // Send ID check request to the server
                    String idCheckMessage = "ID_CHECK|" + id;
                    client.sendMessage(idCheckMessage);

                    // 서버에서 응답 올때까지 좀만 기다리기
                    try {
                        Thread.sleep(500);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    // Check for ID availability
                    boolean isIDAvailable = waitForIDCheckResult();

                    if (isIDAvailable) {
                        JOptionPane.showMessageDialog(
                                JoinGUI.this,
                                "ID 사용 가능"
                        );
                        confirmID = id;
                    } else {
                        JOptionPane.showMessageDialog(
                                JoinGUI.this,
                                "ID 사용 불가"
                        );
                    }
                }
                else {
                    JOptionPane.showMessageDialog(
                            JoinGUI.this,
                            "ID 입력하세요"
                    );
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

                if(!id.isEmpty() && pw.length > 0 && !name.isEmpty() && !gender.isEmpty() && !age.isEmpty() && !phone.isEmpty() && !acc.isEmpty() && !bank.isEmpty()) {

                    if(confirmID.equals(textID.getText())) {
                        // Construct sign up message
                        String signupMessage = "SIGNUP|" + id + "|" + new String(pw) + "|" + name + "|" + gender + "|" +
                                age + "|" + phone + "|" + acc + "|" + bank;

                        // Send sign up message to the server
                        client.sendMessage(signupMessage);

                        // 서버에서 응답 올때까지 좀만 기다리기
                        try {
                            Thread.sleep(500);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        // Wait for the result from the server using SwingWorker
                        boolean isSignUpFinish = waitForSignUpResult();

                        if (isSignUpFinish) {
                            JOptionPane.showMessageDialog(
                                    JoinGUI.this,
                                    "회원가입이 완료되었습니다."
                            );
                            JoinGUI.this.dispose();
                            new LoginGUI(client);
                        } else {
                            JOptionPane.showMessageDialog(
                                    JoinGUI.this,
                                    "회원가입에 실패했습니다. 다시 시도해주세요."
                            );
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                JoinGUI.this,
                                "중복확인을 하지 않은 아이디입니다."
                        );
                    }

                } else {
                    JOptionPane.showMessageDialog(
                            JoinGUI.this,
                            "모든 칸을 채워주세요"
                    );
                }

            }
        });


        // 뒤로가기 버튼 눌렀을 때
        buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the sign up window and go back to the login window
                JoinGUI.this.dispose();
                new LoginGUI(client);
            }
        });
    }


    private boolean waitForIDCheckResult() {
        long startTime = System.currentTimeMillis();
        long timeout = 2000; // Set a timeout of 3 seconds

        // 3초동안 while문 돌며 서버로부터 답장 받음
        while (System.currentTimeMillis() - startTime < timeout) {
            if (client.getReceivedMessage() != null) {
                return client.getReceivedMessage().equals("ID_AVAILABLE");
            }
        }

        return false;
    }


    private boolean waitForSignUpResult() {
        long startTime = System.currentTimeMillis();
        long timeout = 2000; // Set a timeout of 3 seconds

        while (System.currentTimeMillis() - startTime < timeout) {
            if (client.getReceivedMessage() != null) {
                return client.getReceivedMessage().equals("SIGNUP_SUCCESS");
            }
        }

        return false;
    }


}






