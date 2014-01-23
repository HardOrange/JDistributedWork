import java.io.*;

public abstract class ReportThread extends Thread implements Serializable{
	int Status;
	String Result;
	int SliceNum;

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


	final void uploadResults(){
		setStatus(2);
	}


}