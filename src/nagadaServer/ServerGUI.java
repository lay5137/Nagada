package nagadaServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ServerGUI extends JFrame {

    private String[] dateLabels = new String[7];
    private String[] applicantNumLabels = new String[7];

    private final int spacing = 10; // 모든 요소들 사이의 간격

    public ServerGUI() {
        super("관리자 화면");
        setLayout(null);

        // 날짜 라벨 위치 계산
        int dateLabelY = spacing + 20;
        JLabel dateLabel = createLabel("2023", 30, (750 - 350) / 2, dateLabelY, 320, 60);
        add(dateLabel);

        // 주간 패널 위치 계산
        int dayTimePanelY = dateLabelY + 80 + spacing;
        JPanel dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        // 야간 패널 위치 계산
        int nightTimePanelY = dayTimePanelY + 300 + spacing;
        JPanel nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

        setSize(745, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupDayOrNightPanel(JPanel panel, String dayOrNightLabel) {

        // 주간/야간 라벨 위치 계산
        int labelDayYPosition = spacing;
        JLabel labelDay = createLabel(dayOrNightLabel, 30, 10, labelDayYPosition, 100, 40);
        panel.add(labelDay);

        String[] daysWeek = {"월", "화", "수", "목", "금", "토", "일"};
        int labelWidth = 90; // 가로 길이
        int elementHeight = 30; // 세로 길이
        int startOffset = 10;

        // dateLabel의 세로 길이 계산
        int dateLabelHeight = 240 - (labelDayYPosition + labelDay.getHeight() + spacing * 2) - elementHeight - spacing * 2;
        int labelYPosition = labelDayYPosition + labelDay.getHeight() + spacing;

        // applicantNumLabel의 y 좌표 (원래 dateLabel이 있던 위치)
        int applicantNumLabelY = labelYPosition;
        // dateLabel의 y 좌표 (원래 applicantNumLabel이 있던 위치)
        int dateLabelY = applicantNumLabelY + elementHeight + spacing;

        for (int i = 0; i < 7; i++) {
            int xPosition = startOffset + i * (labelWidth + spacing);

            JLabel applicantNumLabel = createLabel("0명", 14, xPosition, applicantNumLabelY, labelWidth, elementHeight);
            panel.add(applicantNumLabel);
            applicantNumLabels[i] = "0명";

            int month = 10;
            int day = 2 + i;

            // dateLabel과 applicantNumLabel의 위치를 서로 바꾸기
            String dateLabelText = String.format("<html><div style='text-align: center;'>%d / %d<br><br>%s</div></html>", month, day, daysWeek[i]);
            JLabel dateLabel = createLabel(dateLabelText, 14, xPosition, dateLabelY, labelWidth, dateLabelHeight);
            panel.add(dateLabel);
            dateLabels[i] = month + "/" + day;

            // 버튼 위치 계산 (dateLabel 아래)
            int buttonY = dateLabelY + dateLabelHeight + spacing;
            JButton button = createButton("상세보기", xPosition, buttonY, labelWidth, elementHeight, i);
            panel.add(button);
        }
    }

    private JLabel createLabel(String text, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, fontSize));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setBounds(x, y, width, height);
        return label;
    }

    private JButton createButton(String text, int x, int y, int width, int height, int index) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼 클릭시 행동 정의
            }
        });
        return button;
    }

    private JPanel createPanel(int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBounds(x, y, width, height);
        return panel;
    }


}

