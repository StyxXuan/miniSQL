package MiniSQL;
import java.util.List;


public interface IndexManager {
		
	boolean createIndex(String table, String field);

	boolean dropIndex(String index);
	
	List<Address> select(String index, List<Condition> conditions);
	
	boolean insert(String index, Object data, FieldType type, Address addr);
	
	List<Address> delete(String index, List<Condition> conditions);
	
}
