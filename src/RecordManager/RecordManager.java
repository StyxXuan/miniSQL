package RecordManager;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.IOException;

import BufferManager.Block;
import BufferManager.BufferManager;
import IndexManager.IndexManager;

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
		File Cat = new File(BufferManager.catlogFileNameGet());
		if(!ft.exists())
			return false;
		
		BufferManager.RemoveBlockFromBuffer(table);
		BufferManager.tables.remove(table.TableName);
		
		return ft.delete() & Cat.delete();
	}
	
	public static boolean dropTable(String TableName) {
		BufferManager.RemoveBlockFromBuffer(BufferManager.tableFileNameGet(TableName));
		BufferManager.tables.remove(TableName);
		File ft = new File(BufferManager.tableFileNameGet(TableName));
		File Cat = new File(BufferManager.catlogFileNameGet());
		
		if(!ft.exists())
			return Cat.delete();
		
		return ft.delete() & Cat.delete();
	}

	public static int insert(Table table, Tuple tup) {
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size() + 4;
		int MaxTupNum = BufferManager.Max_Block / TupSize - 1;
		// Find the free space
		Block b = BufferManager.FindBlock(fileName, 0);
		int RowIndex = 0;
		int Valid = b.GetInt(RowIndex * TupSize);
		while(Valid != 0) {
			RowIndex++;
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
				Valid = b.GetInt(RowIndex * TupSize);
				continue;
			}
			Valid = b.GetInt(RowIndex * TupSize);
		}
		b.WriteInt(1, RowIndex * TupSize);
		b.isValid = false;
		b.WriteData(tup.GetBytes(table), TupSize - 4, RowIndex * TupSize + 4);
		b.isDirty = true;
		table.RecordNum++;
		for (int i = 0; i < table.Row.attrinum; i++)
		{
			if (table.Row.attlist.get(i).hasIndex)
			{
				boolean flag = IndexManager.insert(table.TableName, table.Row.attlist.get(i).attriName, tup.Data.get(i), RowIndex * TupSize);
				if (!flag)
				{
					System.out.println("Insert index failed while inserting record!");
				}
			}
		}
		return RowIndex * TupSize + b.fileOffset;
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
		int TupSize = table.Row.size() + 4;
		int MaxTupNum = BufferManager.Max_Block / TupSize - 1;
		int CountTup = 0, RowIndex = 0, AttIndex = 0;
		Block b = BufferManager.FindBlock(fileName, 0);
		Vector<Tuple> Res = new Vector<Tuple>();
		while(CountTup  < table.RecordNum) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			if(b.GetInt(RowIndex * TupSize) != 0) {
				CountTup++;
				Tuple mid = new Tuple();
				AttIndex = 4;
				for(int i=0; i<table.Row.attrinum; i++) {
					switch(table.Row.attlist.get(i).Type) {
					case FLOAT:
						mid.Data.add(Float.toString(b.GetFloat(AttIndex + RowIndex * TupSize)));
						AttIndex += 4;
						break;
					case INT:
						mid.Data.add(Integer.toString(b.GetInt(AttIndex + RowIndex * TupSize)));
						AttIndex += 4;
						break;
					case STRING:
						mid.Data.add(b.GetString(AttIndex + RowIndex * TupSize, table.Row.attlist.get(i).length));
						AttIndex += table.Row.attlist.get(i).length;
						break;
					default:
						break;
					}
				}
				if(condition.Satisfy(mid, table.Row))
					Res.add(mid);
			}
			RowIndex++;
		}
		return Res;
	}
	
	public static Vector<Tuple> SelectAll(Table table) {
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size() + 4;
		int MaxTupNum = BufferManager.Max_Block / TupSize - 1;
		int CountTup = 0;
		int RowIndex = 0;
		int AttIndex = 0;
		Block b = BufferManager.FindBlock(fileName, 0);
		Vector<Tuple> Res = new Vector<Tuple>();
		while(CountTup  < table.RecordNum) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			if(b.GetInt(RowIndex * TupSize) != 0) {
				CountTup++;
				AttIndex = 4;
				Tuple mid = new Tuple();
				for(int i=0; i<table.Row.attrinum; i++) {
					switch(table.Row.attlist.get(i).Type) {
					case FLOAT:
						mid.Data.add(Float.toString(b.GetFloat(AttIndex + RowIndex * TupSize)));
						AttIndex += 4;
						break;
					case INT:
						mid.Data.add(Integer.toString(b.GetInt(AttIndex + RowIndex * TupSize)));
						AttIndex += 4;
						break;
					case STRING:
						mid.Data.add(b.GetString(AttIndex + RowIndex * TupSize, table.Row.attlist.get(i).length));
						AttIndex += table.Row.attlist.get(i).length;
						break;
					default:
						break;
					}
				}
				Res.add(mid);
			}
			RowIndex++;
		}
		return Res;
	}

	public static Tuple select(Table table, int Offset){
		Block b = BufferManager.FindBlock(BufferManager.tableFileNameGet(table.TableName), Offset);
		Vector<String>Data = new Vector<String>();
		int AttIndex = 4;
		for(int i=0; i<table.Row.attrinum; i++) {
			switch(table.Row.attlist.get(i).Type) {
				case FLOAT:
					Data.add(Float.toString(b.GetFloat(AttIndex + (Offset % BufferManager.Max_Block))));
					AttIndex += 4;
					break;
				case INT:
					Data.add(Integer.toString(b.GetInt(AttIndex + (Offset % BufferManager.Max_Block))));
					AttIndex += 4;
					break;
				case STRING:
					Data.add(b.GetString(AttIndex + (Offset % BufferManager.Max_Block), table.Row.attlist.get(i).length));
					AttIndex += table.Row.attlist.get(i).length;
					break;
				default:
					break;
			}
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
		int TupSize = table.Row.size() + 4;
		int MaxTupNum = BufferManager.Max_Block / TupSize -1 ;
		int CountTup = 0;
		int CountDelete = 0;
		int RowIndex = 0;
		Block b = BufferManager.FindBlock(fileName, 0);
		int AttIndex;
		while(CountTup  < table.RecordNum) {
			if(RowIndex >= MaxTupNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			if(b.GetInt(RowIndex * TupSize) != 0) {
				CountTup++;
				Tuple mid = new Tuple();
				AttIndex = 4;
				
				for(int i=0; i<table.Row.attrinum; i++) {
					switch(table.Row.attlist.get(i).Type) {
					case FLOAT:
						mid.Data.add(Float.toString(b.GetFloat(AttIndex + RowIndex * TupSize)));
						AttIndex += 4;
						break;
					case INT:
						mid.Data.add(Integer.toString(b.GetInt(AttIndex + RowIndex * TupSize)));
						AttIndex += 4;
						break;
					case STRING:
						mid.Data.add(b.GetString(AttIndex + RowIndex * TupSize, table.Row.attlist.get(i).length));
						AttIndex += table.Row.attlist.get(i).length;
						break;
					default:
						break;
					}
				}
				if(condition.Satisfy(mid, table.Row)) {
					for(int i=0; i < table.Row.attrinum; i++) {
						if (table.Row.attlist.get(i).hasIndex)
						{
							IndexManager.delete(table.TableName, table.Row.attlist.get(i).attriName, mid.Data.get(i));
						}
					}
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

	public static int deleteAll(Table table) {
		String fileName = BufferManager.tableFileNameGet(table.TableName);
		int TupSize = table.Row.size() + 4;
		int MaxTupNum = BufferManager.Max_Block / TupSize - 1;
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
				b.WriteInt(0, (RowIndex * TupSize));
				b.isDirty = true;
				b.isValid = true;
				CountDelete++;
			}
			RowIndex++;
		}
		return CountDelete;
	}
	
}
