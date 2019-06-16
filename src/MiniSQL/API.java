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
			RecordManager.createTable(request.tablename, Atts);
			for (int i = 0; i < Atts.size(); i++)
			{
				if (Atts.get(i).isPrimary)
				{
					IndexManager.createIndex(request.tablename, Atts.get(i).attriName, request.tablename + "_" + Atts.get(i).attriName);
				}
			}
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
			BufferManager.indexs.remove(request.indexname);
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
			int FileOfs = IndexManager.select(table, Att, request.condition.Numbers.get(0));
			if (FileOfs >= 0)
			{
				Tuple tup = RecordManager.select(table, FileOfs);
				Datas.add(tup);
			}
			else
			{
				Aff = false;
			}
		}else {
			if (request.type == 3) {
				Datas = RecordManager.SelectAll(table);
			} else if (request.type == 4) {
				Datas = RecordManager.select(table, request.condition);
			}
		}
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time, Datas, table);
		//System.out.println("data size = " + Res.Tups.size());
		return Res;
	}
	
	static public Response insert(Request request) {
		boolean Aff = true;
		double Time = 0;
		Table table = BufferManager.tables.get(request.tablename);
		if(table == null) {
			System.out.println("Table not found");
			return  new Response(false, 0);
		}
		
		Tuple tup = new Tuple(1, request.insertValue);
		System.out.println("Now inserting ele");
		long startTime = System.currentTimeMillis();
		int i = 0;
		for (i = 0; i < table.Row.attrinum; i++)
		{
			if (table.Row.attlist.get(i).isPrimary || table.Row.attlist.get(i).isUnique)
			{
				Condition condition = new Condition(table.Row.attlist.get(i).attriName, request.insertValue.get(i),Condition.Operation.EQUAL);
				Vector<String> parses = new Vector<String>();
				parses.add(table.TableName);
				Request req = new Request(4, parses, condition, null, 0);
				Response response = API.select(req);
				if (response.Tups.size() > 0)
				{
					System.out.println("Duplicated value!");
					break;
				}
			}
		}
		if (i >= table.Row.attrinum)
		{
			RecordManager.insert(table, tup);
		}
		else
		{
			Aff = false;
		}
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
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
			for (int i = 0; i < table.Row.attrinum; i++)
			{
				if (table.Row.attlist.get(i).hasIndex)
				{
					IndexManager.deleteAll(table.TableName,table.Row.attlist.get(i).attriName);
				}
			}
		}
		table.RecordNum -= DeletedNum;
		long endTime = System.currentTimeMillis();
		Time = endTime - startTime;
		Response Res = new Response(Aff, Time);
		return Res;
	}
	
}
