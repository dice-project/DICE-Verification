package it.polimi.dice.verification.json;

public class VerificationTask {
	
	private int current;
	private String state;
	private String status;
	private int total;
	
	public VerificationTask(int current, String state, String status, int total) {
		super();
		this.current = current;
		this.state = state;
		this.status = status;
		this.total = total;
	}

	
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	@Override
	public String toString() {
			return "\nCurrent: " + this.getCurrent() + 
				   "\nState: " + this.getState() +
				   "\nStatus: " + this.getStatus() +
				   "\nTotal: " + this.getTotal();
					
			
	}
	

}
