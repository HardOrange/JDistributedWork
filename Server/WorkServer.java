import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;

public class WorkServer{
	
	public final static Logger LOG = Logger.getLogger(WorkServer.class.getName());
	
	private static FileHandler fh;
	private ArrayList<ClientConnection> Sessions; //ArrayList Containing all the Active Client Connections
	
	public WorkServer(String[] args){
		Sessions = new ArrayList<ClientConnection>();
		new WorkServerConnectionThread(this).start();
	}

	public void newConnection(ClientConnection currConn){
		Sessions.add(currConn);
	}
	public void removeConnection(ClientConnection currConn){
		Sessions.remove(currConn);
	}
	
	public ArrayList<ClientConnection> getList() {
		return (ArrayList<ClientConnection>) Sessions.clone();
	}

	public static void main(String[] args){
		
		try {
			LOG.setLevel(Level.ALL);
			fh = new FileHandler("WorkServer.log");  
			LOG.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
			LOG.info("LOG INITALIZED"); 
		} catch (Exception e) {}
		
		new WorkServer(args);
		
		
	
	}

}


class WorkServerConnectionThread extends Thread{
	private boolean stillAcceptingConnections=true;
	private WorkServer Superior;
	public WorkServerConnectionThread(WorkServer superior){
		Superior = superior;
	}
	public void run(){
		try{
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
					Superior.LOG.info("New WorkClient Connected");
				}
				catch(Exception e){
					//e.printStackTrace();
				}
			}
			servSocket.close();
		}catch(Exception e){
			//e.printStackTrace();
		}

	}

}

class WorkServerCronThread extends Thread {
	private WorkServer Superior;
	private int Timeout;

	public WorkServerCronThread(WorkServer superior) {
		Superior = superior;
		Timeout = 500;
	}

	public WorkServerCronThread(WorkServer superior, int time) {
		Superior = superior;
		Timeout = time;
	}


	public void run() {

		while (true) { //needs condition
			ArrayList<ClientConnection> conns = Superior.getList();
			for (ClientConnection curr : conns) {
				if (curr.getSocket().isClosed()) {
					Superior.removeConnection(curr);
					Superior.LOG.info("removing connection");
				}

			}
		}
	}
}
