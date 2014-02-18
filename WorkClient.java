import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;

public class WorkClient implements Serializable{
	private Socket connection;
	private ObjectInputStream OIS;
	private ObjectOutputStream OOS;
	private Integer	CurrentWorkLoad = 0;
	private Integer MaxWorkLoad;
	private ArrayList<ReportThread> WorkLine;

	public WorkClient(){

	}
	public WorkClient(String address, int port){
		try{
			connection = new Socket(address, port);
			WorkLine = new ArrayList<ReportThread>();
			OIS = new ObjectInputStream(connection.getInputStream());
			System.out.println("Made InputStream");
			File classFile = new File((String)(OIS.readObject()));
			System.out.println("Got Name of File, and in Memory Made File");
			//classFile.createNewFile();
			System.out.println("Made new file on Local Storage");
			FileUtils.copyInputStreamToFile(connection.getInputStream(), classFile);
			System.out.println("Successful Copy of ClassFile");
			OOS = new ObjectOutputStream(connection.getOutputStream());
			OIS = new ObjectInputStream(connection.getInputStream());
			MaxWorkLoad = Runtime.getRuntime().availableProcessors();
			OOS.writeObject(Integer.toString(MaxWorkLoad));
			ReportThread lastThreadReceived = (ReportThread) OIS.readObject();
			while((lastThreadReceived!=null)||(CurrentWorkLoad>0)){
				while((CurrentWorkLoad<MaxWorkLoad-1) && (lastThreadReceived!=null)){
					WorkLine.add(lastThreadReceived);
					//lastThreadReceived.setWorkClient(this);
					lastThreadReceived.start();
					CurrentWorkLoad++;
					lastThreadReceived = (ReportThread) OIS.readObject();
				}
				for(ReportThread work: WorkLine){
					if(work.getStatus()==2){
						uploadData(work);
						work.setStatus(3);
						break;
					}
				}
			}
			terminateConnection();
		}catch(ConnectException e){
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void terminateConnection(){
		try{
			OIS.close();
			OOS.close();
			connection.close();
		}catch(Exception e){

		}
	}

	public synchronized void uploadData(ReportThread reportThread){
		try{
			OOS.writeObject(reportThread);
			CurrentWorkLoad--;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		new WorkClient(args[0], Integer.parseInt(args[1]));
	}
}
