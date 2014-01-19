import java.net.*;
public class ClientConnection extends Thread{
	private Socket ClientSocket;
	public ClientConnection(Socket clientSocket, int connectionNumber){
		
	}

	public Socket getSocket(){
		return ClientSocket;
	}

}