package MiniSQL;
import java.util.List;


public interface RecordManager {
	boolean createTable(Table table);
	
	boolean dropTable(Table table);
	
	List<TableRow> select(Table table, List<Condition> conditions);
	
	Address insert(Table table, List<String> data);
	
	int delete(Table table, List<Condition> conditions);
	
	List<TableRow> select(List<Address> addr);
	
	int delete(List<Address> addr);
}
