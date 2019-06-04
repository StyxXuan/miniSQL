package RecordManager;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
		return true;
	}
	
	public static boolean dropTable(Table table)
	{
		String t_name = tableFileNameGet(table.TableName);
		
		return false;
	}
	
	public static List<TableRow> select(Table table, List<Condition> conditions)
	{
		List<TableRow> Seleted;
		
		return null;
	}
	
//	public static Address insert(Table table, List<String> data)
//	{
//		return null;
//	}
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
