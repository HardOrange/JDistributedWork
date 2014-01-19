import java.net.*;
public class ClientConnection extends Thread{
	private Socket ClientSocket;
	public ClientConnection(Socket clientSocket, int connectionNumber){
		ClientSocket=clientSocket;
	}

	public Socket getSocket(){
		return ClientSocket;
	}

}