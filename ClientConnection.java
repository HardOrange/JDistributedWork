import java.net.*;
import java.io.*;

public class ClientConnection extends Thread{
	private Socket ClientSocket;
	private ObjectInputStream OIS;
	private ObjectOutputStream OOS;
	private WorkServer HomeServer;
	private Integer CurrentWorkLoad = 0;
	private Integer MaxWorkLoad;

	public ClientConnection(Socket clientSocket, int connectionNumber, WorkServer workServer){
		HomeServer = workServer;
		ClientSocket = clientSocket;
		try{
			OIS = new ObjectInputStream(ClientSocket.getInputStream());
			OOS = new ObjectOutputStream(ClientSocket.getOutputStream());
			MaxWorkLoad = Integer.parseInt((String)OIS.readObject());
			for(; CurrentWorkLoad < MaxWorkLoad; CurrentWorkLoad++){
				OOS.writeObject(HomeServer.getWork(this));
			}
			ReportThread lastWorkReceived = HomeServer.getWork(this);
			while(lastWorkReceived!=null){
				HomeServer.reportThread((ReportThread)OIS.readObject());
				lastWorkReceived = HomeServer.getWork(this);
				OOS.writeObject(lastWorkReceived);
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public Socket getSocket(){
		return ClientSocket;
	}

	public boolean isDead(){
		try{
			return ClientSocket.getInputStream().read()==-1;
		}catch(IOException e){
			//e.printStackTrace();
			return true;
		}
	}

	public void terminateConnection(){
		try{
			OIS.close();
			OOS.close();
			ClientSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}