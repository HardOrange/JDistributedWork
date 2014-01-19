import java.net.*;
import java.util.ArrayList;

public class WorkServer{
		ArrayList<ClientConnection> Sessions; //ArrayList Containing all the Active Client Connections
	public WorkServer(String[] args){
		Sessions = new ArrayList<ClientConnection>();
		new WorkServerConnectionThread().start();
	}

	public void newConnection(ClientConnection currConn){
		Sessions.add(currConn);
	}
	public void removeConnection(ClientConnection currConn){
		Sessions.remove(currConn);
	}

	public static void main(String[] args){
		new WorkServer(args);
	}
}


public class WorkServerConnectionThread extends Thread{
	private boolean stillAcceptingConnections=true;
	private WorkServer Superior;
	public WorkServerConnectionThread(WorkServer superior){
		Superior = superior;
	}
	public void run(){
		ServerSocket servSocket = new ServerSocket(3308);
		int currentSessionCount = 0;
		servSocket.setSoTimeout(200);
		while(stillAcceptingConnections){
			try{
				Socket currSocket = servSocket.accept();
				currentSessionCount++;
				ClientConnection currConn = new ClientConnection(currSocket, currentSessionCount);
				Superior.newConnection(currConn);
				currConn.start();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		servSocket.close();
	}

}