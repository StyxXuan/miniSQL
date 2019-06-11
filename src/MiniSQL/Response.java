package MiniSQL;

import java.util.Vector;

import RecordManager.Tuple;

public class Response {
	private boolean Affected;
	private double time;
	public Vector<Tuple>Tups;
	public Response(boolean Aff, double Time) {
		this.Affected = Aff;
		this.time = Time;
		this.Tups = null;
	}
	
	public Response(boolean Aff, double Time, Vector<Tuple>Tups) {
		this.Affected = Aff;
		this.time = Time;
		this.Tups = Tups;
	}
	
	public void PrintInfor() {
		System.out.println("the request affected " + this.Affected);
		System.out.println("using time " + this.time);
	}
}
