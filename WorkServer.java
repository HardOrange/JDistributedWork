import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.*;

public class WorkServer{

	public final static Logger LOG = Logger.getLogger(WorkServer.class.getName());

	private static FileHandler fh;
	private ArrayList<ClientConnection> Sessions; //ArrayList Containing all the Active Client Connections
	private ArrayList<Work> WorkQueue;
	public WorkServer(String[] args){
		Sessions = new ArrayList<ClientConnection>();
		new WorkServerConnectionThread(this).start();
		new WorkServerCronThread(this).start();
		WorkQueue = new ArrayList<Work>();
		try{
			for(int workNum=0; workNum<1000; workNum++){
				WorkQueue.add(new Work((Thread) Class.forName(args[0]).getConstructor(new Class<?>[]{Class.forName("java.lang.Integer")}).newInstance(new Integer(workNum))));
			}
		}catch(Exception e){
			LOG.severe(e.toString());
		}
	}
	public synchronized Work getWork(){
		for(Work piece: WorkQueue){
			if(piece.getStatus()==0){
				return piece;
			}
		}
		return null;
	}

	public void newConnection(ClientConnection currConn){
		Sessions.add(currConn);
	}

	public void removeConnection(ClientConnection currConn){
		Sessions.remove(currConn);
	}

	@SuppressWarnings("unchecked")
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
		} catch (Exception e) {
			LOG.severe(e.toString());
		}

		new WorkServer(args);
	}
}


class WorkServerConnectionThread extends Thread{
	private boolean StillAcceptingConnections=true;
	private WorkServer Superior;

	public WorkServerConnectionThread(WorkServer superior){
		Superior = superior;
	}

	public void run(){
		try{
			ServerSocket servSocket = new ServerSocket(3308);
			int currentSessionCount = 0;
			servSocket.setSoTimeout(200);
			while(StillAcceptingConnections){
				try{
					Socket currSocket = servSocket.accept();
					currentSessionCount++;
					ClientConnection currConn = new ClientConnection(currSocket, currentSessionCount);
					Superior.newConnection(currConn);
					currConn.start();
					Superior.LOG.info("New WorkClient Connected");
				}catch(SocketTimeoutException e){
					//Superior.LOG.severe(e.toString());
				}
				catch(Exception e){
					Superior.LOG.severe(e.toString());
				}
			}
			servSocket.close();
		}catch(Exception e){
			Superior.LOG.severe(e.toString());
		}

	}

	public void setAcceptingConnections(boolean setter){
		StillAcceptingConnections=setter;
		Superior.LOG.info("Changed CleanConnections to "+StillAcceptingConnections);
	}

}

class WorkServerCronThread extends Thread {
	private WorkServer Superior;
	private int Timeout;
	private boolean CleanConnections=true;

	public WorkServerCronThread(WorkServer superior) {
		Superior = superior;
		Timeout = 2000;
	}

	public WorkServerCronThread(WorkServer superior, int time) {
		Superior = superior;
		Timeout = time;
	}

	public void run() {

		while (CleanConnections) { //needs condition
			ArrayList<ClientConnection> conns = Superior.getList();
			for (ClientConnection curr : conns) {
				if (curr.isDead()) {
					curr.terminateConnection();
					Superior.removeConnection(curr);
					Superior.LOG.info("removing connection");
				}

			}
			try{
				this.sleep(Timeout);
			}catch(Exception e){
				Superior.LOG.severe(e.toString());
			}
		}
	}

	public void setCleanConnections(boolean setter){
		CleanConnections=setter;
		Superior.LOG.info("Changed CleanConnections to "+CleanConnections);
	}
}
