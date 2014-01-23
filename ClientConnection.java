import java.net.*;
import java.io.*;
import java.util.logging.*;

public class ClientConnection extends Thread implements Serializable{
	private Socket ClientSocket;
	private ObjectInputStream OIS;
	private ObjectOutputStream OOS;
	private WorkServer HomeServer;
	private Integer CurrentWorkLoad = 0;
	private Integer MaxWorkLoad;

	public ClientConnection(Socket clientSocket, int connectionNumber, WorkServer workServer){
		HomeServer = workServer;
		ClientSocket = clientSocket;
		}

	public void run(){
		try{
			OIS = new ObjectInputStream(ClientSocket.getInputStream());
			OOS = new ObjectOutputStream(ClientSocket.getOutputStream());
			MaxWorkLoad = Integer.parseInt((String)OIS.readObject());
			for(; CurrentWorkLoad < MaxWorkLoad; CurrentWorkLoad++){
				OOS.writeObject(HomeServer.getWork());
			}
			ReportThread lastWorkReceived = HomeServer.getWork();
			while(lastWorkReceived instanceof ReportThread){
				//HomeServer.LOG.info(OIS.readObject().getClass().getName());
				//HomeServer.reportThread((ReportThread)OIS.readObject());
				CurrentWorkLoad--;
				lastWorkReceived = HomeServer.getWork();
				OOS.writeObject(lastWorkReceived);
				CurrentWorkLoad++;
			}
			while(CurrentWorkLoad>1){
				HomeServer.reportThread((ReportThread)OIS.readObject());
				CurrentWorkLoad--;
			}
		}catch(SocketException e){
			HomeServer.removeConnection(this);
			HomeServer.LOG.info("Client Disconnected and Removed");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Socket getSocket(){
		return ClientSocket;
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