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
		Table tb = BufferManager.tables.get(request.tablename);
		for (int i = 0; i < tb.Row.attrinum; i++)
		{
			if (tb.Row.attlist.get(i).hasIndex)
			{
				IndexManager.dropIndex(request.tablename + "_" + tb.Row.attlist.get(i).attriName);
			}

		}
		RecordManager.dropTable(request.tablename);
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time);
		return Res;
	}

	static public Response createIndex(Request request) {
		boolean Aff = true;
		double Time = 0;
		try
		{
			long startTime = System.currentTimeMillis();
			boolean flag = IndexManager.createIndex(request.tablename, request.attributename, request.indexname);
			long endTime = System.currentTimeMillis();
			Time = endTime - startTime;
			if (!flag)
			{
				throw new Exception();
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to create index!");
			Aff = false;
		}
		return new Response(Aff, Time);
	}

	static public Response dropIndex(Request request) {
		boolean Aff = true;
		double Time = 0;
		try
		{
			long startTime = System.currentTimeMillis();
			boolean flag = IndexManager.dropIndex(request.indexname);
			long endTime = System.currentTimeMillis();
			Time = endTime - startTime;
			if (!flag)
			{
				throw new Exception();
			}
		}
		catch (Exception e)
		{
			System.out.println("Index not exists!");
			Aff = false;
		}
		return new Response(Aff, Time);
	}
	
	static public Response select(Request request) {
		boolean Aff = true;
		double Time = 0;
		long startTime = System.currentTimeMillis();
		Table table = BufferManager.tables.get(request.tablename);
		Vector<Tuple> Datas = new Vector<Tuple>();
		System.out.println("now selecting");
		Attribute Att;
		if(request.condition.Attributes.size() == 1 && request.condition.Ops.get(0) == Condition.Operation.EQUAL &&
				(Att = table.GetAttribute(request.condition.Attributes.get(0))).hasIndex){
			int FileOff = IndexManager.select(table, Att, request.condition.Numbers.get(0));
			Tuple tup = RecordManager.select(table, FileOff * (table.Row.size() + 4));
			Datas.add(tup);
		}else {
			if (request.type == 3) {
				Datas = RecordManager.SelectAll(table);
			} else if (request.type == 4) {
				Datas = RecordManager.select(table, request.condition);
				//System.out.println(request.condition.Ops.elementAt(1));
				System.out.println("data size = " + Datas.size());
			}
		}
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time, Datas, table);
		System.out.println("data size = " + Res.Tups.size());
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
		int DeletedNum = 0;
		if(request.type == 9)
		{
			DeletedNum = RecordManager.delete(table, request.condition);
		}
		else
		{
			DeletedNum = RecordManager.deleteAll(table);
		}
		table.RecordNum -= DeletedNum;
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time);
		return Res;
	}
	
}
