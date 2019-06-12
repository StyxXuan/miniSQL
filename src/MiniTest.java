import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import BufferManager.*;

import MiniSQL.*;

public class MiniTest {

	public static void main(String[] args) throws IOException{
		String sql = "";
		boolean Loop = true;
		BufferManager.Init();
		while(Loop) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			try {
				String State = "";
				State = bufferedReader.readLine();
				
				sql += State;
				if(sql.equals("quit")){
					BufferManager.FlushAll();
					BufferManager.SaveTables();
					break;
				}
				else if(sql.contains(";")) {
					Request Res = Interpreter.parse(sql);
					Response Respon = Interpreter.excute(Res);
//					if(Respon.Tups.size() != 0)
//						Respon.PrintInfo(Respon.table);
					
					Respon.PrintInfor();
					sql = "";
				}
			}
			catch(Exception Exp) {
				if(Exp.getMessage() == null) 
					System.out.println("error Exp");
				
				else
					System.out.println(Exp.getMessage());
				
				sql = "";
			}
		}
	}
}
