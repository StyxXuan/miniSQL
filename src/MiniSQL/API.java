package MiniSQL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
			System.out.println(request.tablename);
			RecordManager.createTable(request.tablename, Atts);
			System.out.println(BufferManager.tables.size());
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
		Table table = BufferManager.tables.get(request.tablename);
		Vector<Tuple> Datas = new Vector<Tuple>();
		if(request.condition.Attributes.size() == 1 && request.condition.Ops.get(0) == Condition.Operation.EQUAL){
			Attribute Att = table.GetAttribute(request.condition.Attributes.get(0));
			int FileOff = IndexManager.select(table, Att, request.condition.Numbers.get(0));
			Tuple tup = RecordManager.select(table, FileOff);
			Datas.add(tup);
		}else {
			Datas.retainAll(RecordManager.select(table, request.condition));
		}
		long endTime = System.currentTimeMillis();
		Time =  endTime - startTime;
		Response Res = new Response(Aff, Time, Datas);
		return Res;
	}
	
	static public Response insert(Request request) {
		boolean Aff = true;
		double Time = 0;
		Table table = BufferManager.tables.get(request.tablename);
		System.out.println(request.tablename);
		System.out.println(BufferManager.tables.size());
		if(table == null) {
			System.out.println("table not found");
			return  new Response(false, 0);
		}
		
		Tuple tup = new Tuple(1, request.insertValue);
		System.out.println("Now inserting ele");
		RecordManager.insert(table, tup);
		Response Res = new Response(Aff, Time);
		return Res;
	}
	
	static public Response delete(Request request) {
		boolean Aff = true;
		double Time = 0;
		long startTime = System.currentTimeMillis();
		Table table = BufferManager.tables.get(request.tablename);
//		IndexManager.delete(table, attribute, key)
		int DeletedNum = RecordManager.delete(table, request.condition);
		table.RecordNum -= DeletedNum;
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time);
		return Res;
	}

}
