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
	
	
//	import java.util.Vector;
//	//功能描述：用于存储Table中的一条记录
//	//实现原理：用一个String的Vector以字符串格式一个一个地存储每个属性对应的值。
//	public class tuple {
//		public Vector<String> units;
//		public tuple(Vector<String> units){
//			this.units=units;
//		}
//		public tuple(){units = new Vector<String>();}
//		public String getString(){
//			StringBuffer sb = new StringBuffer();
//			for(int i=0;i<units.size();i++){
//				sb.append("\t"+units.get(i));
//			}
//			return sb.toString();
//		}
//	}
	
	public static void insertSingle(Table table, Tuple tup) {
		String fileName = tableFileNameGet(table.TableName);
		int TupSize = tup.size();
		int MaxTupNum = BufferManager.Max_Block / TupSize;
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
		b.WriteData(tup.data, TupSize - 4, RowIndex * TupSize + 4);
	}
	
	public static void insert(Table table, List<Tuple> tups)
	{
		int N = tups.size();
		for(int i=0; i<N; i++) {
			insertSingle(table, tups.get(i));
		}
	}
	
	public static Vector<Tuple>selectSingleCondition(Table table, Condition condition){
		return null;
	}
	
	public static Vector<Tuple> select(Table table, List<Condition> conditions)
	{
		Vector<Tuple> Selected = null;
		int N = conditions.size();
		for(int i=0; i<N; i++) {
			Vector<Tuple> Mid = selectSingleCondition(table, conditions.get(i));
			if(Selected!=null)
				Selected.retainAll(Mid);
			else
				Selected = Mid;
		}
		
		return Selected;
	}
	
	public static int deleteSingleCondition(Table table, Condition condition) {
		return 0;
	}
	
	public static int delete(Table table, List<Condition> conditions)
	{
		int Sum = 0;
		int N = conditions.size();
		for(int i=0; i<N; i++)
			Sum += deleteSingleCondition(table, conditions.get(i));
		
		return Sum;
	}

	
	public static String tableFileNameGet(String filename)
	{
		return "TABLE_FILE" + filename;
	}
	
	public static String indexFileNameGet(String filename)
	{
		return "INDEX_FILE" + filename;
	}
}
