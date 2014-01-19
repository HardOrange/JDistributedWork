import java.net.*;
import java.util.ArrayList;

public class WorkServer{
	public WorkServer(String[] args){
		try{
			ArrayList<ClientConnection> sessions = new ArrayList<ClientConnection>();
			ServerSocket servSocket = new ServerSocket(3308);
			int currentSessionCount = 0;
			for(;;currentSessionCount++){
				Socket currSocket = servSocket.accept();
				new ClientConnection(currSocket, currentSessionCount).start();
			}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		new WorkServer(args);
	}
}