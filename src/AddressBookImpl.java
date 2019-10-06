import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class AddressBookImpl extends UnicastRemoteObject implements AddressBook {

	//private Hashtable<String, ArrayList<String>> storePhone = new Hashtable<String, ArrayList<String>>();
	public AddressBookImpl() throws RemoteException {
	}
/*	public void connectToDB() {
		System.out.println("Testing SQLite Base.");
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);	

		} catch(Exception e) {
			System.out.println(e);
		}
		System.out.println("End of connecting testing.");
	}
	*/
	public void createTable() {
		//System.out.println("Testing SQLite Base.");
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);
			
			Statement st = con.createStatement();

			st.execute("create table contact(PHONE varchar(10) primary key not null, ID varchar(10), NAME varchar(60), EMAIL varchar(60) )");

			st.close();
			con.close();

		} catch(Exception e) {
			System.out.println(e);
		}

		
		System.out.println();
	}
	
	public void insertInDB(String phone, String id, String name, String mail) {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);
			
			
			Statement st = con.createStatement();
			String sql = "insert into contact (phone, id, name, email) values ('"+phone+"', '"+id+"', '"+name+"', '"+mail+"');";
			st.execute(sql);
				//st.execute("insert into contact values ('2310221525', '1a23s', 'John', 'John@gmail.com')");
                //st.execute("insert into contact values ('2310248531', '123Qa', 'Jan', 'Jan37@gmail.com')");

			String query = "select * from contact";
			ResultSet rs = st.executeQuery(query);

			rs.close();
			st.close();
			con.close();

		} catch(Exception e) {
			System.out.println(e);
		}

		System.out.println("--- End of inserting testing. ---");
		System.out.println();
	}
	
	public Hashtable<String, ArrayList<String>>  selectFromDB() throws RemoteException {
		Hashtable<String, ArrayList<String>> storePhone = new Hashtable<String, ArrayList<String>>();
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);

			Statement st = con.createStatement();
			
			String query = "select * from contact";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String phone = rs.getString("phone");
				String id = rs.getString("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				ArrayList<String> array = new ArrayList<String>();
				array.add(id);
				array.add(name);
				array.add(email);
				storePhone.put(phone, array);	
				
			}

			rs.close();
			st.close();
			con.close();

		} catch(Exception e) {
			System.out.println(e);
		}

		System.out.println("--- End of selecting all contacts testing. ---");
		System.out.println();
		return storePhone;
		
	}
	
	public ArrayList<String> selectContactFromDB(String phone) throws RemoteException {
		ArrayList<String> array = new ArrayList<String>();
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);
			
			Statement st = con.createStatement();
			
			String query = "select * from contact where phone ='"+phone+"' ";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				//ArrayList<String> array = new ArrayList<String>();
				array.add(phone);
				array.add(id);
				array.add(name);
				array.add(email);
				
			}
		}
		catch (Exception e) {
			System.out.println("----Exception e. AddressBookImpl/selectContactFromDB.----");
		}
		
		System.out.println("--- End of selecting a contact testing. ---");
		System.out.println();
		return array;
	}
	
	public void updateDB(String phone, String id, String name, String email) throws RemoteException {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);
			
			Statement st = con.createStatement();

			String sql = "update contact set id = '"+id+"',name='"+name+"',email = '"+email+"' where phone='"+phone+"' ";
			st.executeUpdate(sql);

			String query = "select * from contact";
			ResultSet rs = st.executeQuery(query);

			rs.close();
			st.close();
			con.close();

		} catch(Exception e) {
			System.out.println(e);
		}
		System.out.println("--- End of updating testing. ---");
		System.out.println();
	}
	

	
	public void deleteDB(String phone) throws RemoteException {
		try {
			Class.forName("org.sqlite.JDBC");
			
			String url = "jdbc:sqlite:test.db";
			Connection con = DriverManager.getConnection(url);
			
			Statement st = con.createStatement();
			
			String sql = "delete from contact where phone ='"+phone+"' ";
			st.executeUpdate(sql);
            			
			String query = "select * from contact";
			ResultSet rs = st.executeQuery(query);

			rs.close();
			st.close();
			con.close();

		} catch(Exception e) {
			System.out.println(e);
		}

		System.out.println("--- End of deleting testing. ---");
		System.out.println();
		
	}

}
