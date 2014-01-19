public class Work{
	private int Status=0;
	private String Result;
	private Runnable Target;
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