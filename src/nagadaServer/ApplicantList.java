package nagadaServer;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class ApplicantList extends JFrame {

    public ApplicantList(String date) {
        super("지원자 리스트");
        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        panel1.setLayout(new FlowLayout());
        panel2.setLayout(new FlowLayout());
        panel3.setLayout(new BorderLayout());

        JLabel dateLabel = new JLabel(date, SwingConstants.CENTER);
        dateLabel.setFont(new Font(dateLabel.getFont().getName(), dateLabel.getFont().getStyle(), 20));
        dateLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel requiredPeople = new JLabel("필요인원", SwingConstants.CENTER);
        requiredPeople.setFont(new Font(dateLabel.getFont().getName(), requiredPeople.getFont().getStyle(), 20));
        requiredPeople.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel space1 = new JLabel("       ");

        JTextField minPeople = new JTextField();
        minPeople.setPreferredSize(new Dimension(80, 30));
        minPeople.setText("최소인원");
        minPeople.setEnabled(false);

        JLabel wave = new JLabel("   ~");
        wave.setPreferredSize(new Dimension(30, 30));

        JTextField maxPeople = new JTextField();
        maxPeople.setPreferredSize(new Dimension(80, 30));
        maxPeople.setText("최대인원");
        maxPeople.setEnabled(false);

        panel1.add(dateLabel);
        panel1.add(space1);
        panel1.add(requiredPeople);
        panel1.add(minPeople);
        panel1.add(wave);
        panel1.add(maxPeople);

        JLabel space2 = new JLabel("                                                                                                       ");
        JButton confirmButton = new JButton("인원확정");
        confirmButton.setFont(new Font(dateLabel.getFont().getName(), dateLabel.getFont().getStyle(), 14));
        confirmButton.setBackground(new Color(225, 225, 225)); // 버튼 배경색 변경
        confirmButton.setForeground(Color.DARK_GRAY); // 버튼 글자색 변경
        confirmButton.setFocusPainted(false); // 버튼 포커스 테두리 제거
        confirmButton.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 버튼 경계선 색 변경
        confirmButton.setPreferredSize(new Dimension(80, 30)); // Set the preferred size
        panel2.add(space2);
        panel2.add(confirmButton);

        JTable table = new JTable(100, 8);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding to the JScrollPane
        table.setFillsViewportHeight(true);
        Dimension d = table.getPreferredSize();
        d.width = 200;
        d.height = 150;
        sp.setPreferredSize(d);

        JButton acceptBt = new JButton("확정");
        acceptBt.setFont(new Font(dateLabel.getFont().getName(), dateLabel.getFont().getStyle(), 20));
        acceptBt.setBackground(new Color(225, 225, 225)); // 버튼 배경색 변경
        acceptBt.setForeground(Color.DARK_GRAY); // 버튼 글자색 변경
        acceptBt.setFocusPainted(false); // 버튼 포커스 테두리 제거
        acceptBt.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 버튼 경계선 색 변경

        JButton modifyBt = new JButton("수정"); // Added "수정" button
        modifyBt.setFont(new Font(dateLabel.getFont().getName(), dateLabel.getFont().getStyle(), 20));
        modifyBt.setBackground(new Color(225, 225, 225)); // 버튼 배경색 변경
        modifyBt.setForeground(Color.DARK_GRAY); // 버튼 글자색 변경
        modifyBt.setFocusPainted(false); // 버튼 포커스 테두리 제거
        modifyBt.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 버튼 경계선 색 변경

        panel3.add(panel2, BorderLayout.NORTH);
        panel3.add(sp, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(acceptBt);
        buttonPanel.add(modifyBt);

        panel3.add(buttonPanel, BorderLayout.SOUTH);

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(panel3);

        add(panel1, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER); // Use the containerPanel here

        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
