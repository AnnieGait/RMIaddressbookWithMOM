import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

public interface AddressBook extends Remote {
	//public void connectToDB() throws RemoteException;
	public void createTable() throws RemoteException;
	public void insertInDB(String phone, String id, String name, String mail) throws RemoteException;
	public Hashtable<String, ArrayList<String>>  selectFromDB() throws RemoteException;
	public void updateDB(String phone, String id, String name, String email) throws RemoteException;
	public void deleteDB(String phone) throws RemoteException;
	public ArrayList<String> selectContactFromDB(String phone) throws RemoteException;

}
