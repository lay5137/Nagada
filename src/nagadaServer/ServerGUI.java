package nagadaServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.List;


public class ServerGUI extends JFrame {

    private String[] dateLabels = new String[7];
    private String[] simpleDateLabels = new String[7];
    private String[] applicantNumLabelsDay = new String[7];
    private String[] applicantNumLabelsNight = new String[7];

    private final int spacing = 10; // 모든 요소들 사이의 간격
    private Server server;

    private JPanel dayTimePanel;
    private JPanel nightTimePanel;

    private Calendar lastUpdated = Calendar.getInstance();
    private Timer updateTimer;    // 타이머 객체 생성

    public ServerGUI(Server server) {
        super("관리자 화면");
        this.server = server;
        setLayout(null);

        updateDateLabels(); // 초기에 날짜 레이블 업데이트
        for(int i = 0; i < 7; i++) {
            applicantNumLabelsDay[i] = "주간" + i;
            applicantNumLabelsNight[i] = "야간" + i;
        }

        // 날짜 라벨 위치 계산
        int dateLabelY = spacing + 20;
        JLabel dateLabel = createLabel("2023", 30, (750 - 350) / 2, dateLabelY, 320, 60);
        add(dateLabel);

        // 주간 패널 위치 계산
        int dayTimePanelY = dateLabelY + 80 + spacing;
        dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        // 야간 패널 위치 계산
        int nightTimePanelY = dayTimePanelY + 300 + spacing;
        nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

        setSize(745, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        updateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 새로운 날이 시작되면 날짜 라벨 업데이트
                Calendar now = Calendar.getInstance();
                // 마지막 업데이트 날짜와 현재 날짜가 다른지 확인
                if (!isSameDay(lastUpdated, now)) {
                    updateDateLabels();
                    updatePanels();
                    lastUpdated = now; // 마지막 업데이트 날짜 갱신
                }
            }
        });
        updateTimer.start(); // 타이머 시작

    }

    private void setupDayOrNightPanel(JPanel panel, String dayOrNightLabel) {

        // 주간/야간 라벨 위치 계산
        int labelDayYPosition = spacing;
        JLabel labelDay = createLabel(dayOrNightLabel, 30, 10, labelDayYPosition, 100, 40);
        panel.add(labelDay);

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

            String applicantNum = (dayOrNightLabel.equals("주간")) ? applicantNumLabelsDay[i] : applicantNumLabelsNight[i];
            JLabel applicantNumLabel = createLabel(applicantNum, 14, xPosition, applicantNumLabelY, labelWidth, elementHeight);
            panel.add(applicantNumLabel);

            JLabel dateLabel = createLabel(dateLabels[i], 14, xPosition, dateLabelY, labelWidth, dateLabelHeight);
            panel.add(dateLabel);

            // 버튼 위치 계산 (dateLabel 아래)
            int buttonY = dateLabelY + dateLabelHeight + spacing;
            JButton button = createButton("상세보기", xPosition, buttonY, labelWidth, elementHeight, i, dayOrNightLabel);
            panel.add(button);
        }
    }


    public void updatePanels() {
        remove(dayTimePanel);
        remove(nightTimePanel);

        int dayTimePanelY = 120; // 예시 Y 좌표
        dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        int nightTimePanelY = 430; // 예시 Y 좌표
        nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

        revalidate();
        repaint();
    }


    // 두 Calendar 인스턴스가 같은 날짜인지 확인하는 메소드
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }


    private void updateDateLabels() {
        Calendar calendar = Calendar.getInstance(); // Reset to current date

        for (int i = 0; i < 7; i++) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = new SimpleDateFormat("E").format(calendar.getTime());

            // UI에 표시될 날짜와 요일 (HTML 형식)
            String formattedDate = String.format("<html><div style='text-align: center;'>%02d/%02d<br/><br/>%s</div></html>", month, day, dayOfWeek);
            dateLabels[i] = formattedDate;
            simpleDateLabels[i] = String.format("%04d/%02d/%02d", year, month, day);

            calendar.add(Calendar.DATE, 1);
        }
    }

    private JLabel createLabel(String text, int fontSize, int x, int y, int width, int height) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, fontSize));
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 경계선 색 변경
        label.setBackground(Color.WHITE); // 배경색 변경
        label.setOpaque(true); // JLabel 배경색 표시
        label.setForeground(Color.DARK_GRAY); // 글자색 변경
        label.setBounds(x, y, width, height);
        return label;
    }

    private JButton createButton(String text, int x, int y, int width, int height, int index, String period) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(225, 225, 225)); // 버튼 배경색 변경
        button.setForeground(Color.DARK_GRAY); // 버튼 글자색 변경
        button.setFocusPainted(false); // 버튼 포커스 테두리 제거
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 버튼 경계선 색 변경

        // 상세보기 버튼을 눌렀을 떄
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.showApplicantDetails(simpleDateLabels[index], period);
            }
        });

        return button;
    }

    private JPanel createPanel(int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 패널 경계선 색 변경
        panel.setBackground(Color.WHITE); // 패널 배경색 변경
        panel.setBounds(x, y, width, height);
        return panel;
    }



    // 날짜에 맞게 지원자 수를 업데이트하는 메소드
    public void updateApplicantNumbers(Map<String, Integer> dayApplicantCounts, Map<String, Integer> nightApplicantCounts) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for (int i = 0; i < 7; i++) {
            String formattedDate = sdf.format(calendar.getTime());
            int dayCount = dayApplicantCounts.getOrDefault(formattedDate, 0);
            int nightCount = nightApplicantCounts.getOrDefault(formattedDate, 0);

            applicantNumLabelsDay[i] = dayCount + "명";
            applicantNumLabelsNight[i] = nightCount + "명";

            calendar.add(Calendar.DATE, 1);
        }
    }


    public void updateApplicantTable(List<Applicant> applicants, String date, String period) {
        // 새로운 ApplicantList 창을 생성하고, 지원자 정보로 채웁니다.
        new ApplicantList(applicants, date, period).setVisible(true);
    }


}


