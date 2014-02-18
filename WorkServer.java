import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.*;
import org.apache.commons.io.FileUtils;

public class WorkServer implements Serializable{

	public final static Logger LOG = Logger.getLogger(WorkServer.class.getName());

	private static FileHandler fh;
	private ArrayList<ClientConnection> Sessions; //ArrayList Containing all the Active Client Connections
	private ArrayList<ReportThread> WorkQueue;
	private WorkServerCronThread CronThread;
	private WorkServerConnectionThread ConnThread;
	private String FileNameString;

	public WorkServer(String[] args){
		WorkQueue = new ArrayList<ReportThread>();
		FileNameString = args[0]+".class";
		try{
			for(int workNum=0; workNum<Integer.parseInt(args[1]); workNum++){
				WorkQueue.add((ReportThread) Class.forName(args[0]).getConstructor(new Class<?>[]{Class.forName("java.lang.Integer")}).newInstance(new Integer(workNum)));
			}
		}catch(Exception e){
			LOG.severe(e.toString());
		}

		LOG.info("Work Queue Created");
		Sessions = new ArrayList<ClientConnection>();
		ConnThread = new WorkServerConnectionThread(this);
		ConnThread.start();
		CronThread = new WorkServerCronThread(this);
		CronThread.start();
	}

	public synchronized ReportThread getWork(){
		for(ReportThread piece: WorkQueue){
			if(piece.getStatus()==0){
				piece.setStatus(1);
				return piece;
			}
		}
		ConnThread.setAcceptingConnections(false);
		return null;

	}

	public synchronized void reportThread(ReportThread repoThread){
		int sliceNum = repoThread.getSliceNum();
		for(ReportThread piece: WorkQueue){
			if(piece.getSliceNum()==sliceNum){
				WorkQueue.set(WorkQueue.indexOf(piece),repoThread);
				break;
			}
		}
	}

	public synchronized File getClassFile(){
		return new File(FileNameString);
	}

	public synchronized String getClassFileName(){
		return FileNameString;
	}

	public void newConnection(ClientConnection currConn){
		Sessions.add(currConn);
		LOG.info("New Connection Created");
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
		LOG.info("Use is 'java WorkServer Classfile Name Slices'");
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
			ServerSocket servFTPSocket = new ServerSocket(3309);
			int currentSessionCount = 0;
			servSocket.setSoTimeout(200);
			while(StillAcceptingConnections){
				try{
					Socket currSocket = servSocket.accept();
					Socket currFTPSocket = servFTPSocket.accept();
					Superior.LOG.info("Connection Negotiating");
					currentSessionCount++;
					ClientConnection currConn = new ClientConnection(currSocket, currFTPSocket, currentSessionCount, Superior);
					Superior.newConnection(currConn);
					currConn.start();
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
		Superior.LOG.info("Changed AcceptingConnections to "+StillAcceptingConnections);
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
				if (false) {
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
