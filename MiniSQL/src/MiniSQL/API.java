package MiniSQL;



public interface API {
	
	Response createTable(Request request);
	
	Response dropTable(Request request);
	
	Response createIndex(Request request);
	
	Response dropIndex(Request request);
	
	Response select(Request request);
	
	Response insert(Request request);
	
	Response delete(Request request);

}
