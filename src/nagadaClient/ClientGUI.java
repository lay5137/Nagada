package nagadaClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class MySchedulePanel extends JPanel {

    private Client client;

    public MySchedulePanel(Client client) {
        this.client = client;

    }


}




class ApplyPanel extends JPanel {

    private String[] dateLabels = new String[7];
    private String[] statusLabels = new String[7];

    private final int spacing = 10; // 모든 요소들 사이의 간격
    private Client client;
    private Calendar calendar;

    public ApplyPanel(Client client) {
        this.client = client;
        calendar = Calendar.getInstance();
        setLayout(null);

        updateDateLabels(); // Initialize date labels

        // 날짜 라벨 위치 계산
        int dateLabelY = spacing;
        JLabel yearLabel = createLabel(calendar.get(Calendar.YEAR) + "년", 30, (750 - 350) / 2, dateLabelY, 320, 60);
        add(yearLabel);

        // 주간 패널 위치 계산
        int dayTimePanelY = dateLabelY + 70 + spacing;
        JPanel dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        // 야간 패널 위치 계산
        int nightTimePanelY = dayTimePanelY + 300 + spacing;
        JPanel nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);
    }

    private void setupDayOrNightPanel(JPanel panel, String dayOrNightLabel) {
        // 주간/야간 라벨 위치 계산
        int labelDayYPosition = spacing;
        JLabel labelDay = createLabel(dayOrNightLabel, 30, 10, labelDayYPosition, 100, 40);
        panel.add(labelDay);

        int labelWidth = 90;
        int buttonWidth = 90;
        int startOffset = 10;

        int labelYPosition = labelDayYPosition + 40 + spacing;
        int elementHeight = 30;
        int dateLabelHeight = 270 - labelYPosition - (spacing * 3) - (elementHeight * 2);

        for (int i = 0; i < 7; i++) {
            int xPosition = startOffset + i * (labelWidth + spacing);

            JLabel dateLabel = createLabel(dateLabels[i], 14, xPosition, labelYPosition, labelWidth, dateLabelHeight);
            panel.add(dateLabel);

            int statusLabelY = labelYPosition + dateLabelHeight + spacing;
            JLabel statusLabel = createLabel("모름", 14, xPosition, statusLabelY, labelWidth, elementHeight);
            panel.add(statusLabel);
            statusLabels[i] = "모름";

            int buttonY = statusLabelY + elementHeight + spacing;
            JButton button = createButton("신청", xPosition, buttonY, buttonWidth, elementHeight, i, dayOrNightLabel);
            panel.add(button);
        }
    }

    private void updateDateLabels() {
        calendar = Calendar.getInstance(); // Reset to current date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd (E)");

        for (int i = 0; i < 7; i++) {
            dateLabels[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
    }

    private JLabel createLabel(String text, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, fontSize));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setBounds(x, y, width, height);
        return label;
    }

    private JButton createButton(String text, int x, int y, int width, int height, int index, String dayOrNight) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setActionCommand(dayOrNight); // 주간 또는 야간 태그 설정

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String applyMessage = "APPLY|" + e.getActionCommand() + "|" + dateLabels[index];
                client.sendMessage(applyMessage);
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



class CancelPanel extends JPanel {

    private String[] dateLabels = new String[7];
    private String[] statusLabels = new String[7];

    private final int spacing = 10; // 모든 요소들 사이의 간격
    private Client client;

    public CancelPanel(Client client) {
        this.client = client;
        setLayout(null);

        // 날짜 라벨 위치 계산
        int dateLabelY = spacing;
        JLabel dateLabel = createLabel("2023", 30, (750 - 350) / 2, dateLabelY, 320, 60);
        add(dateLabel);

        // 주간 패널 위치 계산
        int dayTimePanelY = dateLabelY + 70 + spacing;
        JPanel dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        // 야간 패널 위치 계산
        int nightTimePanelY = dayTimePanelY + 300 + spacing;
        JPanel nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);
    }

    private void setupDayOrNightPanel(JPanel panel, String dayOrNightLabel) {
        // 주간/야간 라벨 위치 계산
        int labelDayYPosition = spacing;
        JLabel labelDay = createLabel(dayOrNightLabel, 30, 10, labelDayYPosition, 100, 40);
        panel.add(labelDay);

        String[] daysWeek = {"월", "화", "수", "목", "금", "토", "일"};
        int labelWidth = 90;
        int buttonWidth = 90;
        int startOffset = 10;

        int labelYPosition = labelDayYPosition + 40 + spacing;
        int elementHeight = 30;
        int dateLabelHeight = 270 - labelYPosition - (spacing * 3) - (elementHeight * 2);

        for (int i = 0; i < 7; i++) {
            int xPosition = startOffset + i * (labelWidth + spacing);
            // 10에 월, 2+i가 일
            int month = 10;
            int day = 2;
            // 월 + i로 일요일까지 생성
            String labelText = String.format("<html><div style='text-align: center;'>%d / %d<br><br>%s</div></html>", month, day + i, daysWeek[i]);

            JLabel dateLabel = createLabel(labelText, 14, xPosition, labelYPosition, labelWidth, dateLabelHeight);
            panel.add(dateLabel);
            dateLabels[i] = month + "/" + (day+i);

            int statusLabelY = labelYPosition + dateLabelHeight + spacing;
            JLabel statusLabel = createLabel("공백", 14, xPosition, statusLabelY, labelWidth, elementHeight);
            panel.add(statusLabel);
            // 이부분에 서버로부터 메시지 받아서 업데이트
            statusLabels[i] = "공백";

            int buttonY = statusLabelY + elementHeight + spacing;
            JButton button = createButton("취소", xPosition, buttonY, buttonWidth, elementHeight, i, dayOrNightLabel);
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

    private JButton createButton(String text, int x, int y, int width, int height, int index, String dayOrNight) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setActionCommand(dayOrNight); // 주간 또는 야간 태그 설정

        // 신청버튼 눌렀을 떄
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String applyMessage = "Cancel|" + e.getActionCommand() + "|" + dateLabels[index];
                client.sendMessage(applyMessage);
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




// 화면 구성
public class ClientGUI extends JFrame {

    public ClientGUI(Client client) {
        JTabbedPane clientGUI = new JTabbedPane();

        MySchedulePanel mySchedulePanel = new MySchedulePanel(client);
        ApplyPanel applyPanel = new ApplyPanel(client);
        CancelPanel cancelPanel = new CancelPanel(client);

        clientGUI.addTab("나의일정", mySchedulePanel);
        clientGUI.addTab("지원하기", applyPanel);
        clientGUI.addTab("취소하기", cancelPanel);

        getContentPane().add(clientGUI);
        setTitle("이용자 화면");

        // Frame 설정
        setSize(750, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}


