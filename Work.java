public class Work{
	private int Status=0; //Status indicator, 0 meaning no action, 1 meaning currently being worked on, and 2 meaning result is gathered;
	private String Result;
	private Runnable Target; //The Runnable class that the work will be designed in.
	public Work(Runnable target){
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
	public Runnable getRunnable(){
		return Target;
	}
}