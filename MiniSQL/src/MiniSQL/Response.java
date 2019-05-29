package MiniSQL;

public class Response {
	private boolean Affected;
	private double time;
	public Response(boolean Aff, double Time) {
		this.Affected = Aff;
		this.time = Time;
	}
	
	public void PrintInfor() {
		System.out.println("the request affected " + this.Affected);
		System.out.println("using time " + this.time);
	}
}
