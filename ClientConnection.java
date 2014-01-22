import java.net.*;
import java.io.*;

public class ClientConnection extends Thread{
	private Socket ClientSocket;
	private ObjectInputStream OIS;
	private ObjectOutputStream OOS;
	private Integer WorkLimit;
	public ClientConnection(Socket clientSocket, int connectionNumber){
		ClientSocket = clientSocket;
		try{
			OIS = new ObjectInputStream(ClientSocket.getInputStream());
			OOS = new ObjectOutputStream(ClientSocket.getOutputStream());
			WorkLimit = Integer.parseInt((String)OIS.readObject());
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