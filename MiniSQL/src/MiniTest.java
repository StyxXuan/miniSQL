
import java.io.BufferedReader;

import MiniSQL.Interpreter;
import MiniSQL.Request;
import MiniSQL.Response;


public class MiniTest {
	public static void main(String[] args){
		String sql = "";
		boolean Loop = true;
		BufferedReader bufferedReader = null;
		while(Loop) {
			try {
				System.out.println("input ");
				String State = "";
				State = bufferedReader.readLine();
				sql += State;
				if(sql.contains(";")) {
					boolean check = Interpreter.checkSyntax(sql);
					if(!check)
						throw new Exception("SyntaxException");
					
					Request req = Interpreter.parse(sql);
					check = Interpreter.checkLexeme(req);
					if(!check)
						throw new Exception("LexemeException");
					
					Response resp = Interpreter.excute(req);
					resp.PrintInfor();
					sql = "";
				}
			}
			catch(Exception Exp) {
				System.out.println(Exp.getMessage());
			}
		}
		
	}
}
