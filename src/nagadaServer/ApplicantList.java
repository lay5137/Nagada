package nagadaServer;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class ApplicantList extends JFrame {

    public ApplicantList() {
        super("지원자 리스트");
        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        JPanel panel1 = new JPanel();
        JPanel panel3 = new JPanel();

        panel1.setLayout(new FlowLayout());
        panel3.setLayout(new BorderLayout());

        JLabel date = new JLabel("2023.10.2(월)", SwingConstants.CENTER);
        date.setFont(new Font(date.getFont().getName(), date.getFont().getStyle(), 20));
        date.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel RequiredPeople = new JLabel("필요인원", SwingConstants.CENTER);
        RequiredPeople.setFont(new Font(date.getFont().getName(), RequiredPeople.getFont().getStyle(), 20));
        RequiredPeople.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel space1 = new JLabel("       ");

        JTextField MinPeople = new JTextField();
        MinPeople.setPreferredSize(new Dimension(80, 30));

        JLabel wave = new JLabel("   ~");
        wave.setPreferredSize(new Dimension(30, 30));

        JTextField MaxPeople = new JTextField();
        MaxPeople.setPreferredSize(new Dimension(80, 30));

        panel1.add(date);
        panel1.add(space1);
        panel1.add(RequiredPeople);
        panel1.add(MinPeople);
        panel1.add(wave);
        panel1.add(MaxPeople);

        JLabel noticeBound = new JLabel("                                                                                최소인원                     최대인원");

        JTable table = new JTable(100, 8);
        JScrollPane sp = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        Dimension d = table.getPreferredSize();
        d.width = 200;
        d.height = 150;
        sp.setPreferredSize(d);

        JButton acceptBt = new JButton("확정");

        panel3.add(noticeBound, BorderLayout.NORTH);
        panel3.add(sp, BorderLayout.CENTER);
        panel3.add(acceptBt, BorderLayout.SOUTH);

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(panel3);

        add(panel1, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER); // Use the containerPanel here

        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
        new ApplicantList();
    }
}