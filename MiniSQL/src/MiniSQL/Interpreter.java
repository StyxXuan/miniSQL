package MiniSQL;

public interface Interpreter {
		
	boolean checkSyntax(String sql);
	
	Request parse(String sql);
	
	boolean checkLexeme(Request request);
	
	Response excute(Request request);
}
