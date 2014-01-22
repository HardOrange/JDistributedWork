public class Work{
	private int Status=0; //Status indicator, 0 meaning no action, 1 meaning currently being worked on, and 2 meaning result is gathered;
	private String Result;
	private Thread Target; //The Thread class that the work will be designed in.
	
	public Work(Thread target){
		Target= target;
	}

	public void setStatus(int status){
		Status = status;
	}

	public String getResult(){
		return Result;
	}

	public int getStatus(){
		return Status;
	}

	public Thread getThread(){
		return Target;
	}

	public void setResult(String result){
		Result = result;
	}
}