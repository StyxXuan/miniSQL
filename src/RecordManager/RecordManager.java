package RecordManager;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.IOException;

import BufferManager.Block;
import BufferManager.BufferManager;

public class RecordManager {

	public static boolean createTable(String Name, List<Attribute> Atts) throws IOException {
		TableRow NewRow = new TableRow(Atts, Atts.size());
		Table NewTable = new Table(Name, NewRow);
		String t_name = BufferManager.tableFileNameGet(NewTable.TableName);
		File ft = new File(t_name);
		if(ft.exists())
		{
			return false;	
		}
		ft.createNewFile();
		Block b = BufferManager.FindBlock(t_name, 0);
		b.WriteInt(0, 0);
		b.isDirty = true;
		BufferManager.tables.put(Name, NewTable);
		return true;
	}
	
	public static boolean createTable(Table table) throws IOException
	{
		String t_name = BufferManager.tableFileNameGet(table.TableName);
		
		File ft = new File(t_name);
		if(ft.exists())
		{
			return false;	
		}
		ft.createNewFile();
		Block b = BufferManager.FindBlock(t_name, 0);
		b.WriteInt(0, 0);
		b.isDirty = true;
		BufferManager.tables.put(table.TableName, table);
		return true;
	}
	
	public static boolean dropTable(Table table)
	{
		String t_name = BufferManager.tableFileNameGet(table.TableName);
		File ft = new File(t_name);
		if(!ft.exists())
			return false;
		
		BufferManager.RemoveBlockFromBuffer(table);
		BufferManager.tables.remove(table.TableName);
		return ft.delete();
	}
	
	public static boolean dropTable(String TableName) {
		File ft = new File(BufferManager.tableFileNameGet(TableName));
		if(!ft.exists())
			return false;
		
		BufferManager.RemoveBlockFromBuffer(BufferManager.tableFileNameGet(TableName));
		BufferManager.tables.remove(BufferManager.tableFileNameGet(TableName));
		return ft.delete();
	}
	
	public static void insert(Table table, Tuple tup) {
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size() + 4;
		int MaxTupNum = BufferManager.Max_Block / TupSize;
		
		// Find the free space
		Block b = BufferManager.FindBlock(fileName, 0);
		int RowIndex = 0;
		int Valid = b.GetInt(RowIndex * TupSize);
		while(Valid != 0) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			RowIndex++;
			Valid = b.GetInt(RowIndex * TupSize);
		}
		
		b.WriteInt(1, RowIndex * TupSize);
		System.out.println("b.read" + b.GetInt(RowIndex * TupSize));
		b.isValid = true;
		b.WriteData(tup.GetBytes(table), TupSize - 4, RowIndex * TupSize + 4);
		System.out.println("b.read" + b.GetInt(RowIndex * TupSize + 4));
		b.isDirty = true;
		table.RecordNum++;
	}
	
	
	public static void insert(Table table, List<Tuple> tups)
	{
		int N = tups.size();
		for(int i=0; i<N; i++) {
			insert(table, tups.get(i));
		}
	}
	

	public static Vector<Tuple>select(Table table, Condition condition){
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size();
		int MaxTupNum = BufferManager.Max_Block / TupSize;
		int CountTup = 0;
		int RowIndex = 0;
		Vector<Tuple>SelectedTups = new Vector<Tuple>();
		Block b = BufferManager.FindBlock(fileName, 0);
		
		while(CountTup  < table.RecordNum) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			if(b.GetInt(RowIndex * TupSize) != 0) {
				CountTup++;
				Tuple mid = new Tuple();
				int AttIndex = 0;
				for(int i=0; i<table.Row.attrinum; i++) {
					switch(table.Row.attlist.get(i).Type) {
					case FLOAT:
						mid.Data.add(Float.toString(b.GetFloat(AttIndex)));
						AttIndex += 4;
						break;
					case INT:
						mid.Data.add(Integer.toString(b.GetInt(AttIndex)));
						AttIndex += 4;
						break;
					case STRING:
						mid.Data.add(b.GetString(AttIndex, table.Row.attlist.get(i).length));
						AttIndex += table.Row.attlist.get(i).length;
						break;
					default:
						break;
					}
				}
				
				if(condition.Satisfy(mid, table.Row)) {
					SelectedTups.add(mid);
				}
			}
			RowIndex++;
		}
		
		
		return SelectedTups;
	}
	

	public static Tuple select(Table table, int Offset){
		Block b = BufferManager.FindBlock(BufferManager.tableFileNameGet(table.TableName), Offset);
		Vector<String>Data = new Vector<String>();
		List<Attribute> atts = table.Row.attlist;
		for(int i=0; i<atts.size(); i++) {
			String Att = b.GetString(Offset, atts.get(i).length);
			Data.addElement(Att);
		}
		Tuple Selected = new Tuple(1, Data);
		return Selected;
	}
	
	public static Vector<Tuple>select(Table table, Vector<Integer>Offsets){
		Vector<Tuple>Res = new Vector<Tuple>();
		
		for(int i=0; i<Offsets.size(); i++) {
			Res.addElement(select(table, Offsets.get(i)));
		}
		return Res;
	}
	
	public static Vector<Tuple> select(Table table, List<Condition> conditions)
	{
		Vector<Tuple> Selected = new Vector<Tuple>();
		int N = conditions.size();
		for(int i=0; i<N; i++) {
			Vector<Tuple> Mid = select(table, conditions.get(i));
			if(Selected!=null)
				Selected.retainAll(Mid);
			else
				Selected = Mid;
		}
		
		return Selected;
	}
	
	public static int delete(Table table, Condition condition) {
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size();
		int MaxTupNum = BufferManager.Max_Block / TupSize;
		int CountTup = 0;
		int CountDelete = 0;
		int RowIndex = 0;
		Block b = BufferManager.FindBlock(fileName, 0);
		
		while(CountTup  < table.RecordNum) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			if(b.GetInt(RowIndex * TupSize) != 0) {
				CountTup++;
				Tuple mid = new Tuple();
				int AttIndex = 4;
				for(int i=0; i<table.Row.attrinum; i++) {
					switch(table.Row.attlist.get(i).Type) {
					case FLOAT:
						mid.Data.add(Float.toString(b.GetFloat(AttIndex)));
						AttIndex += 4;
						break;
					case INT:
						mid.Data.add(Integer.toString(b.GetInt(AttIndex)));
						AttIndex += 4;
						break;
					case STRING:
						mid.Data.add(b.GetString(AttIndex, table.Row.attlist.get(i).length));
						AttIndex += table.Row.attlist.get(i).length;
						break;
					default:
						break;
					}
				}
				
				if(condition.Satisfy(mid, table.Row)) {
					
					b.WriteInt(0, (RowIndex * TupSize));
					CountDelete++;
				}
			}
			RowIndex++;
		}
		return CountDelete;
	}
	
	public static int delete(Table table, List<Condition> conditions)
	{
		int Sum = 0;
		int N = conditions.size();
		for(int i=0; i<N; i++)
			Sum += delete(table, conditions.get(i));
		
		return Sum;
	}

	
	
}
