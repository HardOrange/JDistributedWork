import java.io.*;

public abstract class ReportThread extends Thread implements Serializable{
	int Status;
	String Result;
	int SliceNum;
	//WorkClient Worker;
	//ClientConnection Client;

	int getStatus(){
		return Status;
	}

	void setStatus(int status){
		Status = status;
	}

	String getResult(){
		return Result;
	}

	final int getSliceNum(){
		return SliceNum;
	}

	//final void setWorkClient(WorkClient worker){
	//	Worker = worker;
	//}

	final void uploadResults(){
		setStatus(2);
		//Worker.uploadData(this);
	}

	//final void setClient(ClientConnection client){
	//	Client = client;
	//}



}