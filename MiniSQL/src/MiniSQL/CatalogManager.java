package MiniSQL;
import java.util.List;


public interface CatalogManager {
	
	boolean tableExists(String table);
	
	int fieldCount(String table);
	
	int indexCount(String table);
	
	List<String> fieldsOnTable(String table);
	
	List<String> indicesOnTable(String table);
	
	int rowLength(String table);
	
	String pkOnTable(String table);
	
	FieldType fieldType(String table, String field);
	
	boolean isUnique(String table, String field);
	
	boolean isPK(String table, String field);
	
	boolean hasIndex(String table, String field);
	
	String indexName(String table, String field);
	
	String tableIndexOn(String index);
	
	String fieldIndexOn(String index);
	
	boolean addTable();

	boolean deleteTable(String table);
	
	boolean addIndex();
	
	boolean deleteIndex(String index);
	
	boolean storeToFile();
}
