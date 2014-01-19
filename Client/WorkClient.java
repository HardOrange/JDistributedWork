import java.net.*;
public class WorkClient{
	private Socket connection;
	public WorkClient(){

	}
	public WorkClient(String address, int port){
		try{
			connection = new Socket(address, port);

			System.out.println("This is where the magic happens.");

			connection.close();
		}catch(Exception e){

		}
	}
	public static void main(String[] args){
		new WorkClient(args[0], Integer.parseInt(args[1]));
	}
}
