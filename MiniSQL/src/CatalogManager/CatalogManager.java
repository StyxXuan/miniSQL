package CatalogManager;
import java.util.List;

import MiniSQL.FieldType;
import BufferManager.Block;
import BufferManager.BufferManager;

public class CatalogManager {
	
//	
//	public static boolean tableExists(String table) {
//		int N = Tables.size();
//		boolean Flag = false;
//		
//		for(int i=0; i<N; i++)
//			if(Tables.get(i).TableName == table)
//				Flag = true;
//		
//		return Flag;
//	}
//	
//	public static int fieldCount(String table) {
//		int i=0;
//		for(i=0; i<Tables.size(); i++) {
//			if(Tables.get(i).TableName.equals(table))
//				break;
//		}
//		Table t = Tables.get(i);
//		return t.RecordNum;
//	}
//	
//	public static int indexCount(String table) {
//		return 0;
//	}
//	
//	public static List<String> fieldsOnTable(String table) {
//		return null;
//	}
//	
//	public static List<String> indicesOnTable(String table) {
//		return null;
//	}
//	
//	public static int rowLength(String table) {
//		return 0;
//	}
//	
//	public static String pkOnTable(String table) {
//		return null;
//	}
//	
//	public static FieldType fieldType(String table, String field) {
//		return null;
//	}
//	
//	public static boolean isUnique(String table, String field) {
//		return false;
//	}
//	
//	public static boolean isPK(String table, String field) {
//		return false;
//	}
//	
//	public static boolean hasIndex(String table, String field) {
//		return false;
//	}
//	
//	public static String indexName(String table, String field) {
//		return null;
//	}
//	
//	public static String tableIndexOn(String index) {
//		return null;
//	}
//	
//	public static String fieldIndexOn(String index) {
//		return null;
//	}
//	
//	public static boolean addTable() {
//		return false;
//	}
//
//	public static boolean deleteTable(String table) {
//		return false;
//	}
//	
//	public static boolean addIndex() {
//		return false;
//	}
//	
//	public static boolean deleteIndex(String index) {
//		return false;
//	}
//	
//	public static boolean storeToFile() {
//		return false;
//	}
}
