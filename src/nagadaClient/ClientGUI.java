package nagadaClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.Timer;



class MySchedulePanel extends JPanel {
    private Client client;
    private JPanel calendarPanel;
    private JComboBox<Integer> yearComboBox;
    private JComboBox<String> monthComboBox;
    private final int daysInWeek = 7;
    private final int maxWeeksInMonth = 6;

    public MySchedulePanel(Client client) {
        this.client = client;
        setLayout(new BorderLayout());

        add(createControlPanel(), BorderLayout.NORTH);
        calendarPanel = createCalendarPanel(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
        add(calendarPanel, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        // 년도 선택 ComboBox
        yearComboBox = new JComboBox<>();
        for (int i = 1900; i <= 2100; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR));
        controlPanel.add(yearComboBox);

        // 월 선택 ComboBox
        monthComboBox = new JComboBox<>(new String[]{"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"});
        monthComboBox.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        controlPanel.add(monthComboBox);

        // 날짜 선택에 대한 이벤트 리스너
        ActionListener dateSelectionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCalendar();
            }
        };

        yearComboBox.addActionListener(dateSelectionListener);
        monthComboBox.addActionListener(dateSelectionListener);

        return controlPanel;
    }

    private void updateCalendar() {
        int year = (int) yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex();

        remove(calendarPanel);
        calendarPanel = createCalendarPanel(year, month);
        add(calendarPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createCalendarPanel(int year, int month) {
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        calendarPanel.setBackground(Color.WHITE);

        // Header panel with day names
        JPanel headerPanel = new JPanel(new GridLayout(1, daysInWeek));
        headerPanel.setBackground(Color.WHITE);
        String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            headerPanel.add(dayLabel);
        }
        calendarPanel.add(headerPanel, BorderLayout.NORTH);

        // Days panel with day numbers
        JPanel daysPanel = new JPanel(new GridLayout(maxWeeksInMonth, daysInWeek));
        daysPanel.setBackground(Color.WHITE);
        Calendar calendar = new GregorianCalendar(year, month, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Calculate the previous month's days to display
        Calendar prevMonthCalendar = (Calendar) calendar.clone();
        prevMonthCalendar.add(Calendar.MONTH, -1);
        int prevMonthDays = prevMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int prevMonthStart = prevMonthDays - firstDayOfWeek + 2;

        // Previous month days (faded)
        for (int i = 1; i < firstDayOfWeek; i++) {
            int day = prevMonthStart + i - 1;
            daysPanel.add(createDayLabel(Integer.toString(day), true));
        }

        // Current month days
        for (int i = 1; i <= daysInMonth; i++) {
            JLabel dayLabel = createDayLabel(Integer.toString(i), false);
            daysPanel.add(dayLabel);
        }

        // Next month days (faded)
        int nextMonthDay = 1;
        for (int i = daysPanel.getComponentCount(); i < daysInWeek * maxWeeksInMonth; i++) {
            daysPanel.add(createDayLabel(Integer.toString(nextMonthDay++), true));
        }

        calendarPanel.add(daysPanel, BorderLayout.CENTER);
        return calendarPanel;
    }

    private JLabel createDayLabel(String text, boolean isFaded) {
        JLabel dayLabel = new JLabel(text, SwingConstants.CENTER);
        dayLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        if (isFaded) {
            dayLabel.setForeground(Color.LIGHT_GRAY);
        } else {
            dayLabel.setForeground(Color.BLACK);
        }
        dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return dayLabel;
    }


}





class ApplyPanel extends JPanel {	// 패널 업데이트 될때 신청 버튼이 초기화되서 다시 눌린다 해결해야함

    private String[] dateLabels = new String[7];
    private String[] simpleDateLabels = new String[7]; // 서버에 보낼 간단한 날짜 형식
    private String[] statusLabelsDay = new String[7]; // 주간 상태 배열
    private String[] statusLabelsNight = new String[7]; // 야간 상태 배열
    private boolean[] hasAppliedDay = new boolean[7];  // 주간 신청 여부
    private boolean[] hasAppliedNight = new boolean[7]; // 야간 신청 여부

    private JPanel dayTimePanel;
    private JPanel nightTimePanel;

    private final int spacing = 10; // 모든 요소들 사이의 간격
    private Client client;
    private Calendar calendar = Calendar.getInstance();;
    private Calendar lastUpdated = Calendar.getInstance();
    private Timer updateTimer;    // 타이머 객체 생성

    private CancelPanel cancelPanel; // ApplyPanel의 참조를 저장

    public ApplyPanel(Client client) {
        this.client = client;
        setLayout(null);

        updateDateLabels(); // 초기에 날짜 레이블 업데이트
        for(int i = 0; i < 7; i++) {
            statusLabelsDay[i] = " ";
            statusLabelsNight[i] = " ";
            hasAppliedDay[i] = false;
            hasAppliedNight[i] = false;
        }

        // 날짜 라벨 위치 계산
        int dateLabelY = spacing;
        JLabel yearLabel = createLabel(calendar.get(Calendar.YEAR) + "년", 30, (750 - 350) / 2, dateLabelY, 320, 60);
        add(yearLabel);

        // 주간 패널 위치 계산
        int dayTimePanelY = dateLabelY + 70 + spacing;
        dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        // 야간 패널 위치 계산
        int nightTimePanelY = dayTimePanelY + 300 + spacing;
        nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

        // 위에는 GUI 생성할 때, 한번만 호출되기 때문에 이 부분에서 계속 반복적으로 작업하며 업데이트 진행
        // 타이머 설정: 1초마다 체크
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

                // 여기에서 서버로부터 메시지를 확인하고 업데이트할 수 있도록 구현
                String messages = client.getReceivedMessage();
                String[] parts = messages.split("\\|");
                if (parts.length >= 16 && parts[0].equals("BROADCAST")) {
                    processServerMessage(messages); // 받은 메시지 처리
                    updatePanels();
                }

            }
        });
        updateTimer.start(); // 타이머 시작

    }

    public void setCancelPanel(CancelPanel cancelPanel) {
        this.cancelPanel = cancelPanel;
    }


    // 두 Calendar 인스턴스가 같은 날짜인지 확인하는 메소드
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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
            String status = (dayOrNightLabel.equals("주간")) ? statusLabelsDay[i] : statusLabelsNight[i];
            //System.out.print(status);	// 확인용
            JLabel statusLabel = createLabel(status, 14, xPosition, statusLabelY, labelWidth, elementHeight);
            panel.add(statusLabel);

            int buttonY = statusLabelY + elementHeight + spacing;
            JButton button = createButton("신청", xPosition, buttonY, buttonWidth, elementHeight, i, dayOrNightLabel);
            panel.add(button);
        }
    }

    private void updatePanels() {
        remove(dayTimePanel);
        remove(nightTimePanel);

        int dayTimePanelY = 90; // 예시 Y 좌표
        dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        int nightTimePanelY = 400; // 예시 Y 좌표
        nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

        revalidate();
        repaint();
    }


    // 브로드캐스트로 온 메시지로 인원이 얼마나 차 있는지 확인하는 상태 라벨 업데이트
    public void processServerMessage(String messages) {
        String[] parts = messages.split("\\|");
        if (parts.length >= 16 && parts[0].equals("BROADCAST")) {
            String todayDate = parts[1]; // 오늘 날짜
            if(todayDate.equals(simpleDateLabels[0])) {
                for (int i = 2; i < 9; i++) {	// 일주일 주간상태에 저장
                    statusLabelsDay[i-2] = parts[i];
                }

                for (int i = 9; i < 16; i++) {	// 일주일 야간상태에 저장
                    statusLabelsNight[i-9] = parts[i];
                }
            }
        }
    }


    // 날짜 라벨 업데이트
    private void updateDateLabels() {
        calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = new SimpleDateFormat("E").format(calendar.getTime());

            // UI에 표시될 날짜와 요일 (HTML 형식)
            String formattedDate = String.format("<html><div style='text-align: center;'>%02d/%02d<br/><br/>%s</div></html>", month, day, dayOfWeek);
            dateLabels[i] = formattedDate;

            // 서버에 보낼 간단한 날짜 형식
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

    private JButton createButton(String text, int x, int y, int width, int height, int index, String dayOrNight) {
        boolean hasApplied = (dayOrNight.equals("주간")) ? hasAppliedDay[index] : hasAppliedNight[index];
        String buttonText = hasApplied ? "신청완료" : "신청";
        JButton button = new JButton(buttonText);

        button.setBounds(x, y, width, height);
        button.setBackground(new Color(225, 225, 225));
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setActionCommand(dayOrNight);
        button.setEnabled(!hasApplied);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!hasApplied) {
                    String applyMessage = "APPLY|" + e.getActionCommand() + "|" + simpleDateLabels[index];
                    client.sendMessage(applyMessage);

                    Timer responseTimer = new Timer(500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            String response = client.getReceivedMessage();
                            if (response != null && response.equals("APPLY_SUCCESS")) {
                                button.setText("신청완료");
                                button.setEnabled(false);
                                cancelPanel.activateCancelButton(response, index, dayOrNight);

                                // 신청 상태 배열 업데이트
                                if(dayOrNight.equals("주간")) {
                                    hasAppliedDay[index] = true;
                                } else {
                                    hasAppliedNight[index] = true;
                                }
                                ((Timer) evt.getSource()).stop();
                            }
                        }
                    });
                    responseTimer.setRepeats(true);
                    responseTimer.start();
                }
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


    public void activateApplyButton(String response, int index, String dayOrNight) {
        if (response.equals("CANCEL_SUCCESS")) {
            if (dayOrNight.equals("주간")) {
                hasAppliedDay[index] = false;
            } else {
                hasAppliedNight[index] = false;
            }
            updatePanels(); // 업데이트된 상태로 패널을 다시 그립니다.
        }
    }


}





class CancelPanel extends JPanel {

    private String[] dateLabels = new String[7];
    private String[] simpleDateLabels = new String[7]; // 서버에 보낼 간단한 날짜 형식
    private String[] myStatusLabelsDay = new String[7]; // 주간 상태 배열
    private String[] myStatusLabelsNight = new String[7]; // 야간 상태 배열
    private boolean[] hasAppliedDay = new boolean[7];  // 주간 취소 여부
    private boolean[] hasAppliedNight = new boolean[7]; // 야간 취소 여부

    private final int spacing = 10; // 모든 요소들 사이의 간격
    private Client client;
    private Calendar calendar;

    private JPanel dayTimePanel;
    private JPanel nightTimePanel;
    private Calendar lastUpdated = Calendar.getInstance();
    private Timer updateTimer;    // 타이머 객체 생성

    private ApplyPanel applyPanel; // ApplyPanel의 참조를 저장

    public CancelPanel(Client client, ApplyPanel applyPanel) {
        this.client = client;
        this.applyPanel = applyPanel;
        calendar = Calendar.getInstance(); // 오늘 날짜 설정
        setLayout(null);

        updateDateLabels(); // 초기에 날짜 레이블 업데이트
        for(int i = 0; i < 7; i++) {
            myStatusLabelsDay[i] = " ";
            myStatusLabelsNight[i] = " ";
            hasAppliedDay[i] = false;
            hasAppliedNight[i] = false;
        }

        // 날짜 라벨 위치 계산
        int dateLabelY = spacing;
        JLabel dateLabel = createLabel(calendar.get(Calendar.YEAR) + "년", 30, (750 - 350) / 2, dateLabelY, 320, 60);
        add(dateLabel);

        // 주간 패널 위치 계산
        int dayTimePanelY = dateLabelY + 70 + spacing;
        dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        // 야간 패널 위치 계산
        int nightTimePanelY = dayTimePanelY + 300 + spacing;
        nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

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

                // 여기에서 서버로부터 메시지를 확인하고 업데이트할 수 있도록 구현
                String messages = client.getReceivedMessage();
                String[] parts = messages.split("\\|");
                if (parts.length >= 16 && parts[0].equals("PERSONAL")) {
                    processServerMessage(messages); // 받은 메시지 처리
                    updatePanels();
                }

            }
        });
        updateTimer.start(); // 타이머 시작
    }

    // 두 Calendar 인스턴스가 같은 날짜인지 확인하는 메소드
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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
            String status = (dayOrNightLabel.equals("주간")) ? myStatusLabelsDay[i] : myStatusLabelsNight[i];
            JLabel statusLabel = createLabel(status, 14, xPosition, statusLabelY, labelWidth, elementHeight);
            panel.add(statusLabel);

            int buttonY = statusLabelY + elementHeight + spacing;
            JButton button = createButton("취소", xPosition, buttonY, buttonWidth, elementHeight, i, dayOrNightLabel);
            panel.add(button);
        }
    }


    private void updatePanels() {
        remove(dayTimePanel);
        remove(nightTimePanel);

        int dayTimePanelY = 90; // 예시 Y 좌표
        dayTimePanel = createPanel(10, dayTimePanelY, 710, 270);
        setupDayOrNightPanel(dayTimePanel, "주간");
        add(dayTimePanel);

        int nightTimePanelY = 400; // 예시 Y 좌표
        nightTimePanel = createPanel(10, nightTimePanelY, 710, 270);
        setupDayOrNightPanel(nightTimePanel, "야간");
        add(nightTimePanel);

        revalidate();
        repaint();
    }

    // 브로드캐스트로 온 메시지로 인원이 얼마나 차 있는지 확인하는 상태 라벨 업데이트
    public void processServerMessage(String messages) {
        String[] parts = messages.split("\\|");
        if (parts.length >= 16 && parts[0].equals("PERSONAL")) {
            String todayDate = parts[1]; // 오늘 날짜
            if(todayDate.equals(simpleDateLabels[0])) {
                for (int i = 2; i < 9; i++) {	// 일주일 신청했던 날짜(주간)의 나의 상태 정보
                    myStatusLabelsDay[i-2] = parts[i];
                }

                for (int i = 9; i < 16; i++) {	// 일주일 신청했던 날짜(야간)의 나의 상태 정보
                    myStatusLabelsNight[i-9] = parts[i];
                }
            }
        }
    }

    private void updateDateLabels() {
        calendar = Calendar.getInstance(); // Reset to current date

        for (int i = 0; i < 7; i++) {
            int year = calendar.get(Calendar.YEAR); // 현재 calendar의 년도
            int month = calendar.get(Calendar.MONTH) + 1; // 1월 = 0, 따라서 +1
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = new SimpleDateFormat("E").format(calendar.getTime());

            // UI에 표시될 날짜와 요일 (HTML 형식)
            String formattedDate = String.format("<html><div style='text-align: center;'>%02d/%02d<br/><br/>%s</div></html>", month, day, dayOfWeek);
            dateLabels[i] = formattedDate;

            // 서버에 보낼 간단한 날짜 형식
            simpleDateLabels[i] = String.format("%04d/%02d/%02d", year, month, day);

            calendar.add(Calendar.DATE, 1); // 날짜를 하루 증가시킴
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

    private JButton createButton(String text, int x, int y, int width, int height, int index, String dayOrNight) {
        boolean hasApplied = (dayOrNight.equals("주간")) ? hasAppliedDay[index] : hasAppliedNight[index];

        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(225, 225, 225)); // 버튼 배경색 변경
        button.setForeground(Color.DARK_GRAY); // 버튼 글자색 변경
        button.setFocusPainted(false); // 버튼 포커스 테두리 제거
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 버튼 경계선 색 변경
        button.setActionCommand(dayOrNight); // 주간 또는 야간 태그 설정
        button.setEnabled(hasApplied); // 버튼 활성화 상태 설정

        // 신청버튼 눌렀을 떄
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cancelMessage = "CANCEL|" + e.getActionCommand() + "|" + simpleDateLabels[index];
                client.sendMessage(cancelMessage);

                Timer responseTimer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        String response = client.getReceivedMessage();
                        if (response != null && response.equals("CANCEL_SUCCESS")) {
                            button.setEnabled(false);
                            applyPanel.activateApplyButton(response, index, dayOrNight);

                            // 다시 비활성화 상태로 바꿔주기
                            if(dayOrNight.equals("주간")) {
                                hasAppliedDay[index] = false;
                            } else {
                                hasAppliedNight[index] = false;
                            }
                            ((Timer) evt.getSource()).stop();
                        }
                    }
                });
                responseTimer.setRepeats(true);
                responseTimer.start();
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


    public void activateCancelButton(String response, int index, String dayOrNight) {
        if (response.equals("APPLY_SUCCESS")) {
            if (dayOrNight.equals("주간")) {
                hasAppliedDay[index] = true;
            } else {
                hasAppliedNight[index] = true;
            }
            updatePanels();
        }
    }


}




// 화면 구성
public class ClientGUI extends JFrame {

    public ClientGUI(Client client) {
        JTabbedPane clientGUI = new JTabbedPane();

        MySchedulePanel mySchedulePanel = new MySchedulePanel(client);
        ApplyPanel applyPanel = new ApplyPanel(client);
        CancelPanel cancelPanel = new CancelPanel(client, applyPanel);
        applyPanel.setCancelPanel(cancelPanel);

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



