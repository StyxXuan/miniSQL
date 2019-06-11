package MiniSQL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import RecordManager.*;
import IndexManager.*;
import BufferManager.*;

public class API{
	
	static public Response createTable(Request request) {
		boolean Aff = true;
		double Time = 0;
		List<Attribute> Atts = new ArrayList<Attribute>();
		for(int i=0; i<request.attriVec.size(); i++)
			Atts.add(request.attriVec.get(i));
		
		try {

			long startTime = System.currentTimeMillis();
			RecordManager.createTable(request.tablename, Atts);
			long endTime = System.currentTimeMillis();
			Time = endTime - startTime;
		} catch (IOException e) {
			Aff = false;
		}
		Response Res = new Response(Aff, Time);
		return Res;
	}
	
	static public Response dropTable(Request request) {
		boolean Aff = true;
		double Time = 0;
		long startTime = System.currentTimeMillis();
		RecordManager.dropTable(request.tablename);
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time);
		return Res;
	}
	
	static public Response createIndex(Request request) {
		return null;
	}
	
	static public Response dropIndex(Request request) {
		return null;
	}
	
	static public Response select(Request request) {
		boolean Aff = true;
		double Time = 0;
		long startTime = System.currentTimeMillis();
		if(request.condition.Attributes.size() == 1 && request.condition.Ops.get(0) == Condition.Operation.EQUAL){
			Table table = BufferManager.tables.get(request.tablename);
			Attribute Att = table.GetAttribute(request.condition.Attributes.get(0));
			int FileOff = IndexManager.select(table, Att, request.condition.Numbers.get(0));
		}else {
			
		}
		return null;
	}
	
	static public Response insert(Request request) {
		return null;
	}
	
	static public Response delete(Request request) {
		return null;
	}

}
