import java.net.*;
import java.io.*;

public class WorkClient{
	private Socket connection;
	private ObjectInputStream OIS;
	private ObjectOutputStream OOS;
	public WorkClient(){

	}
	public WorkClient(String address, int port){
		try{
			connection = new Socket(address, port);
			OOS = new ObjectOutputStream(connection.getOutputStream());
			OIS = new ObjectInputStream(connection.getInputStream());
			System.out.println("This is where the magic happens.");

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void terminateConnection(){
			OIS.close();
			OOS.close();
			connection.close();
	}
	public static void main(String[] args){
		new WorkClient(args[0], Integer.parseInt(args[1]));
	}
}
