package MiniSQL;
import java.util.List;


public class CatalogManager {
	
	private List<Table> Tables;
	
	boolean tableExists(String table) {
		int N = Tables.size();
		boolean Flag = false;
		
		for(int i=0; i<N; i++)
			if(Tables.get(i).TableName == table)
				Flag = true;
		
		return Flag;
	}
	
	int fieldCount(String table) {
		int i=0;
		for(i=0; i<Tables.size(); i++) {
			if(Tables.get(i).TableName.equals(table))
				break;
		}
		Table t = Tables.get(i);
		return t.RecordNum;
	}
//	
//	int indexCount(String table);
	
//	List<String> fieldsOnTable(String table);
//	
//	List<String> indicesOnTable(String table);
	
//	int rowLength(String table);
	
//	String pkOnTable(String table);
	
//	FieldType fieldType(String table, String field);
	
//	boolean isUnique(String table, String field);
	
//	boolean isPK(String table, String field);
	
//	boolean hasIndex(String table, String field);
	
//	String indexName(String table, String field);
	
//	String tableIndexOn(String index);
	
//	String fieldIndexOn(String index);
	
//	boolean addTable();
//
//	boolean deleteTable(String table);
//	
//	boolean addIndex();
//	
//	boolean deleteIndex(String index);
//	
//	boolean storeToFile();
}
