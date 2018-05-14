package charging;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class Charge {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	      try {
	         Class.forName("org.postgresql.Driver");
	         Connection c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/postgres",
	            "postgres", "rita80221");
	         PreparedStatement ps = c.prepareStatement("INSERT INTO category(catname) VALUES(?);");
	         ps.setString(1, "飲食");
	         ps.execute();
	         Statement st = c.createStatement();
	         String sql = "select * from category";
	         ResultSet rs = st.executeQuery(sql);
	         while (rs.next())
             {
                System.out.println(rs.getString("catID")+" "+rs.getString("catname")); //將結果用while印出
             }
              rs.close();
              st.close();
              c.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Opened database successfully");
	}

}
