import java.net.*;
import java.io.*;

public class WorkClient{
	private Socket connection;
	private ObjectInputStream OIS;
	private ObjectOutputStream OOS;
	private Integer	CurrentWorkLoad = 0;
	private Integer MaxWorkLoad;


	public WorkClient(){

	}
	public WorkClient(String address, int port){
		try{
			connection = new Socket(address, port);
			OOS = new ObjectOutputStream(connection.getOutputStream());
			OIS = new ObjectInputStream(connection.getInputStream());
			MaxWorkLoad = Runtime.getRuntime().availableProcessors();
			OOS.writeObject(Integer.toString(MaxWorkLoad));
			ReportThread lastThreadReceived = (ReportThread) OIS.readObject();
			while(lastThreadReceived!=null){
				while(CurrentWorkLoad<MaxWorkLoad){
					lastThreadReceived.setWorkClient(this);
					lastThreadReceived.start();
					CurrentWorkLoad++;
					lastThreadReceived = (ReportThread) OIS.readObject();
				}
			}
			while(CurrentWorkLoad>0){
				//terrible Implementation
			}
			terminateConnection();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void terminateConnection(){
		try{
			OIS.close();
			OOS.close();
			connection.close();
		}catch(Exception e){

		}
	}

	public synchronized void uploadData(ReportThread reportThread){
		try{
			OOS.writeObject(reportThread);
			CurrentWorkLoad--;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		new WorkClient(args[0], Integer.parseInt(args[1]));
	}
}
