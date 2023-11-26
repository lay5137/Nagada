package nagadaServer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;


public class ApplicantList extends JFrame {

    public ApplicantList(List<Applicant> applicants, String dateLabel, String period) {
        super("지원자 관리 - " + dateLabel + " " + period);
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 변경: EXIT_ON_CLOSE -> DISPOSE_ON_CLOSE, 이 창만 닫기
        setLayout(new BorderLayout(10, 10));

        // 상단 패널 추가
        JPanel topPanel = createDatePanel(dateLabel, period);
        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 추가 (테이블 포함)
        JPanel tablePanel = createTablePanel(applicants);
        add(tablePanel, BorderLayout.CENTER);

        // 하단 패널 추가
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createDatePanel(String dateLabel, String period) {
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblDate = new JLabel("날짜: " + dateLabel + " (" + period + ")");
        lblDate.setFont(new Font(lblDate.getFont().getName(), Font.PLAIN, 20));
        leftPanel.add(lblDate);
        datePanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel requiredPeopleLabel = new JLabel("필요인원:");
        requiredPeopleLabel.setFont(new Font(requiredPeopleLabel.getFont().getName(), Font.PLAIN, 20));
        rightPanel.add(requiredPeopleLabel);

        JTextField minField = createHintTextField("최소", 4);
        minField.setPreferredSize(new Dimension(60, 30)); // 세로 크기 조절
        JTextField maxField = createHintTextField("최대", 4);
        maxField.setPreferredSize(new Dimension(60, 30)); // 세로 크기 조절
        rightPanel.add(minField);
        rightPanel.add(new JLabel("~"));
        rightPanel.add(maxField);

        JButton btnSave = new JButton("저장");
        btnSave.setFont(new Font(btnSave.getFont().getName(), Font.PLAIN, 14));
        btnSave.setPreferredSize(new Dimension(80, 30)); // 버튼 크기 조절
        rightPanel.add(btnSave);

        datePanel.add(rightPanel, BorderLayout.EAST);

        return datePanel;
    }

    private JPanel createTablePanel(List<Applicant> applicants) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "이름", "나이", "성별", "전화번호"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        table.setFillsViewportHeight(true);

        for (Applicant applicant : applicants) {
            model.addRow(new Object[]{applicant.getUserId(), applicant.getName(), applicant.getAge(), applicant.getGender(), applicant.getPhoneNumber()});
        }

        adjustColumnWidths(table); // Adjust column widths
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void adjustColumnWidths(JTable table) {
        TableColumn ageColumn = table.getColumnModel().getColumn(2); // 나이 column
        TableColumn genderColumn = table.getColumnModel().getColumn(3); // 성별 column
        TableColumn phoneColumn = table.getColumnModel().getColumn(4); // 전화번호 column

        ageColumn.setPreferredWidth(50); // 나이 열 너비 설정
        genderColumn.setPreferredWidth(50); // 성별 열 너비 설정
        phoneColumn.setPreferredWidth(150); // 전화번호 열 너비 늘리기
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 버튼 사이의 간격 조절

        JButton btnModify = new JButton("수정");
        btnModify.setFont(new Font(btnModify.getFont().getName(), Font.PLAIN, 20));
        btnModify.setPreferredSize(new Dimension(100, 30)); // 버튼 크기 조절

        JButton btnConfirm = new JButton("확정");
        btnConfirm.setFont(new Font(btnConfirm.getFont().getName(), Font.PLAIN, 20));
        btnConfirm.setPreferredSize(new Dimension(100, 30)); // 버튼 크기 조절

        bottomPanel.add(btnModify);
        bottomPanel.add(btnConfirm);

        return bottomPanel;
    }

    private JTextField createHintTextField(String hint, int columns) {
        JTextField textField = new JTextField(hint, columns);
        textField.setMaximumSize(new Dimension(200, 40));
        textField.setFont(new Font(textField.getFont().getName(), Font.PLAIN, 24));
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(hint)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(hint);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        return textField;
    }



}







