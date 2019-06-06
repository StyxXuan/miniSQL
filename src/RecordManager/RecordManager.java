package RecordManager;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.IOException;

import BufferManager.Block;
import BufferManager.BufferManager;

public class RecordManager {

	
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
		b.WriteData(tup.GetBytes(), TupSize - 4, RowIndex * TupSize + 4);
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
		
		Block b = BufferManager.FindBlock(fileName, 0);
//		while() {
//			
//		}
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
		return 0;
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
