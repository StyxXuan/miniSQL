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
		String t_name = tableFileNameGet(table.TableName);
		
		File ft = new File(t_name);
		if(ft.exists())
		{
			return false;	
		}
		ft.createNewFile();
		Block b = BufferManager.FindBlock(t_name, 0);
		b.WriteInt(0, 0);
		
		return true;
	}
	
	public static boolean dropTable(Table table)
	{
		String t_name = tableFileNameGet(table.TableName);
		File ft = new File(t_name);
		if(!ft.exists())
			return false;	
		
		BufferManager.tables.remove(table.TableName);
		return ft.delete();
	}
	
	public static Vector<TableRow>selectSingleCondition(Table table, Condition condition){
		return null;
	}
	
	public static Vector<TableRow> select(Table table, List<Condition> conditions)
	{
		Vector<TableRow> Selected = null;
		int N = conditions.size();
		for(int i=0; i<N; i++) {
			Vector<TableRow> Mid = selectSingleCondition(table, conditions.get(i));
			if(Selected!=null)
				Selected.retainAll(Mid);
			else
				Selected = Mid;
		}
		
		return Selected;
	}
	
	public static void insertSingle(Table table, TableRow Row) {
		String fileName = tableFileNameGet(table.TableName);
		int RowSize = Row.RowSize();
		int MaxRowNum = BufferManager.Max_Block / (RowSize + 4);
		Block b = BufferManager.FindBlock(fileName, 0);
		int RowIndex = 0;
		int Valid = b.GetInt(RowIndex * RowSize);
		while(Valid != 0) {
			if(RowIndex >= MaxRowNum) {
				b = BufferManager.GetNextBlock(b);
				RowIndex = 0;
			}
			Valid = b.GetInt(RowIndex * RowSize);
		}
		
		b.WriteInt(1, RowIndex * RowSize);
		b.WriteData(Row.toByte(), RowSize, RowIndex * RowSize + 4);
	}
	
	public static void insert(Table table, List<TableRow> Rows)
	{
		int N = Rows.size();
		for(int i=0; i<N; i++) {
			insertSingle(table, Rows.get(i));
		}
	}
//	
//	public static int delete(Table table, List<Condition> conditions)
//	{
//		return 0;
//	}
//	
//	public static List<TableRow> select(List<Address> addr)
//	{
//		return null;
//	}
//	
//	public static int delete(List<Address> addr)
//	{
//		return 0;
//	}
	
	public static String tableFileNameGet(String filename)
	{
		return "TABLE_FILE" + filename;
	}
	
	public static String indexFileNameGet(String filename)
	{
		return "INDEX_FILE" + filename;
	}
}
