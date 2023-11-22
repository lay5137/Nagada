import java.sql.*;

public class JDBCTest {
    public static void main(String[] args) {
        Connection conn;
        String url = "jdbc:mysql://localhost:3306/Nagada?serverTimezone=UTC";

        String id = "root";
        String pw = "1234";

        Statement stmt;
        ResultSet result;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, id, pw);
            System.out.println("DB연결완료");

            stmt = conn.createStatement();

            result = stmt.executeQuery("select * from Client");

            while(result.next()) {
                int hakbun = result.getInt(1);
                String name = result.getString("name");
                String dept = result.getString("dept");
                int score = result.getInt(4);
                int grade = result.getInt("grade");
                System.out.println(hakbun+" "+name+" "+dept+" "+score+" "+grade);
            }
            result.close();
            stmt.close();
            conn.close();

        } catch(ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch(SQLException e) {
            System.out.println("DB 연결 오류");
        }
    }
}
