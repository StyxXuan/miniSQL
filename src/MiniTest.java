import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import BufferManager.*;

import MiniSQL.*;
import MiniSQL.Interpreter;

public class MiniTest {

	public static void main(String[] args) throws IOException{
		String sql = "";
		boolean Loop = true;
		while(Loop) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			try {
				BufferManager.Init();
				String State = "";
				State = bufferedReader.readLine();
				
				sql += State;
				if(sql.equals("quit")){
					BufferManager.FlushAll();
					BufferManager.SaveTables();
					break;
				}
				if(sql.contains(";")) {
					Interpreter Inter = new Interpreter(sql);
					Response Res = Inter.excute(Inter.parse(sql));
					
					Res.PrintInfor();
					sql = "";
				}
			}
			catch(Exception Exp) {
				if(Exp.getMessage() == null) 
					System.out.println("error");
				
				else
					System.out.println(Exp.getMessage());
				sql = "";
			}
		}
	}
}
