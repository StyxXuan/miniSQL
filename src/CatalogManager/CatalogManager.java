package CatalogManager;
import java.util.List;

import BufferManager.BufferManager;
import RecordManager.FieldType;
import RecordManager.Table;
import RecordManager.TableRow;

public class CatalogManager {
	
	
	public static boolean tableExists(String table) {
		boolean Flag = false;
		Table t = BufferManager.tables.get(table);
		Flag = (t!=null) ? true:false;
		return Flag;
	}
	
	public static int fieldCount(String table) {
		Table t = BufferManager.tables.get(table);
		return t.RecordNum;
	}
	
	public static int indexCount(String table) {
		return 0;
	}
	
	@SuppressWarnings("null")
	public static List<String> fieldsOnTable(String table) {
		List<String> Fields = null;
		Table t = BufferManager.tables.get(table);
		TableRow row = t.Row;
		for(int i=0; i<row.attrinum; i++)
			Fields.add(row.attlist.get(i).attriName);
			
		return Fields;
	}
	
	public static List<String> indicesOnTable(String table) {
		return null;
	}
	
	public static int rowLength(String table) {
		return 0;
	}
	
	public static String pkOnTable(String table) {
		return null;
	}
	
	public static FieldType fxwieldType(String table, String field) {
		return null;
	}
	
	public static boolean isUnique(String table, String field) {
		Table t = BufferManager.tables.get(table);
		TableRow row = t.Row;
		int N = row.attrinum;
		for(int i=0; i<N; i++) {
			if(row.attlist.get(i).attriName == field)
				return row.attlist.get(i).isUnique;
		}
		return false;
	}
	
	public static boolean isPK(String table, String field) {
		return false;
	}
	
	public static boolean hasIndex(String table, String field) {
		Table t = BufferManager.tables.get(table);
		TableRow row = t.Row;
		int N = row.attrinum;
		for(int i=0; i<N; i++) {
			if(row.attlist.get(i).attriName == field)
				return row.attlist.get(i).hasIndex;
		}
		return false;
	}
	
	public static String indexName(String table, String field) {
		return null;
	}
	
	public static String tableIndexOn(String index) {
		return null;
	}
	
	public static String fieldIndexOn(String index) {
		return null;
	}
	
	public static boolean addTable() {
		return false;
	}

	public static boolean deleteTable(String table) {
		
		return (BufferManager.tables.remove(table) != null);
	}
	
	public static boolean addIndex() {
		return false;
	}
	
	public static boolean deleteIndex(String index) {
		return false;
	}
	
	public static boolean storeToFile() {
		return false;
	}
}
