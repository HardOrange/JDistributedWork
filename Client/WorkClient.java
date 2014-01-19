import java.net.*;
public class WorkClient{
	private Socket connection;
	public WorkClient(){

	}
	public WorkClient(String address, int port){
		try{
			connection = new Socket(address, port);
		}catch(Exception e){
			
		}
	}
	public static void main(String[] args){
		new WorkClient(args[0], Integer.parseInt(args[1]));
	}
}