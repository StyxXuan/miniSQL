package RecordManager;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.IOException;

import BufferManager.Block;
import BufferManager.BufferManager;

public class RecordManager {

	public static boolean createTable(String Name, Vector<Attribute> Atts) {
		
		return false;
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
	
	
	public static void insert(Table table, Tuple tup) {
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = tup.size();
		int MaxTupNum = BufferManager.Max_Block / TupSize;
		
		// Find the free space
		Block b = BufferManager.FindBlock(fileName, 0);
		int RowIndex = 0;
		int Valid = -1;
		while(Valid != 0) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			Valid = b.GetInt(RowIndex * TupSize);
			RowIndex++;
		}
		
		b.WriteInt(1, RowIndex * TupSize);
		b.WriteData(tup.GetBytes(), TupSize - 4, RowIndex * TupSize + 4);
		table.RecordNum++;
	}
	
	
	public static void insert(Table table, List<Tuple> tups)
	{
		int N = tups.size();
		for(int i=0; i<N; i++) {
			insert(table, tups.get(i));
		}
	}
	
	@SuppressWarnings("null")
	public static Vector<Tuple>select(Table table, Condition condition){
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size();
		int MaxTupNum = BufferManager.Max_Block / TupSize;
		int CountTup = 0;
		int RowIndex = 0;
		Vector<Tuple>SelectedTups = null;
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
	
	public static Vector<Tuple>select(String file, Vector<Integer>Offsets){
		return null;
	}
	
	public static Vector<Tuple> select(Table table, List<Condition> conditions)
	{
		Vector<Tuple> Selected = null;
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
