package MiniSQL;

import java.util.Vector;

import BufferManager.BufferManager;
import RecordManager.Condition;
import RecordManager.Condition.Operation;
import RecordManager.Attribute;
import RecordManager.FieldType;
import CatalogManager.*;

public class Interpreter{
	static int index = 0;
	/*
	 * create table student (sno char(8), sname char(16) unique, sage int, sgender char (1),primary key ( sno ));
	 * insert into student values('12345678', '1234567890123456', 10, '1');
	 * drop table student;
	 * delete from student where sno = '12345678';
	 * select * from student;
	 * select * from student where sno = '12345678';
	 * select * from student where sno = '12345678' and sage = 5;
	 */

/*	public static void main(String[] args)
	{
		System.out.println("here");
		String sql = "select * from student;";
		String sql2 = "insert into student values('12345678', '1234567890123456', 10, '1');";
		Request r = parse(sql);
		System.out.println(r.tablename);
		Request r2 = parse(sql2);
		System.out.println(r2.tablename + " " + r2.insertValue.elementAt(0));
	}*/
	
	static public Request parse(String sql) {
		Vector<String> parses = new Vector<String>();
		String word;
		word = getWord(sql);
		//create table or index
		if(word.equals("create"))
		{
			word = getWord(sql);
			if(word.equals("table"))
			{
				String primarykey = "";
				String tablename = "";
				word = getWord(sql);
				if(!word.isEmpty())
				{
					tablename = word;
				}
				else
				{
					System.out.println("Syntax Error: no table name!");
					index = 0;
					return null;
				}
				word = getWord(sql);
				if(!word.equals("("))
				{
					System.out.println("Error int syntax");
					index = 0;
					return null;
				}
				else
				{
					int len = 0; //length of string
					word = getWord(sql);
					Vector<Attribute> attriVec = new Vector<Attribute>();
					while(!word.equals("primary") && !word.equals(")"))
					{
						String attributename = word;
						FieldType type = FieldType.Empty;
						boolean isUnique = false;
						word = getWord(sql);
						if(word.equals("int"))
						{
							type = FieldType.INT;
						}
						else if(word.equals("float"))
						{
							type = FieldType.FLOAT;
						}
						else if(word.equals("char"))
						{
							type = FieldType.STRING;
							word = getWord(sql);
							if(!word.equals("("))
							{
								index = 0;
								return null;
							}
							word = getWord(sql);
							len = Integer.parseInt(word);
							word = getWord(sql); 
							if(!word.equals(")"))
							{
								index = 0;
								return null;
								//Error
							}
						}
						else
						{
							System.out.println("Syntax Error: unkonwn data type");
							index = 0;
							return null;
							//Error
						}
						word = getWord(sql);
						if(word.equals("unique"))
						{
							isUnique = true;
							word = getWord(sql);
						}
						Attribute attri;
						if(type == FieldType.STRING)
							attri = new Attribute(type, attributename, len, 0);
						else
							attri = new Attribute(type, attributename);
						if(isUnique)
							attri.SetUnique();
						attriVec.add(attri);
						if(!word.equals(","))
						{
							if(!word.equals(")"))
							{
								System.out.println("Syntax Error: invalid ues of ','!");
								index = 0;
								return null;
							}
						}
						word = getWord(sql);
					}
					int primarylocation = -1;
					if(word.equals("primary"))
					{
						word = getWord(sql);
						if(!word.equals("key"))
						{
							System.out.println("Error in syntax");
							index = 0;
							return null;
							//Error
						}
						else
						{
							word = getWord(sql);
							if(!word.equals("("))
							{
								System.out.println("Error in syntax");
								index = 0;
								return null;
								//Error
							}
							else
							{
								word = getWord(sql);
								primarykey = word;
								int i;
								for(i = 0; i < attriVec.size(); i++)
								{
									if(primarykey.equals(attriVec.get(i).attriName))
									{
										attriVec.get(i).SetPrimary();
										attriVec.get(i).SetUnique();
										break;
									}
								}
								if(i == attriVec.size())
								{
									System.out.println("Syntax Error: primaryKey doesn't exist in attriubtes!");
									index = 0; 
									return null;
									//Error
								}
								primarylocation = i;
								word = getWord(sql);
								if(!word.equals(")"))
								{
									System.out.println("Error in syntax");
									index = 0;
									return null;
									//Error
								}
							}
						}
					}
					else
					{
						//Error
						System.out.println("Error in syntax");
						index = 0;
						return null;
					}
					index = 0;
					parses.add(tablename);
					parses.add(primarykey);
					Request r = new Request(1, parses, null,  attriVec, primarylocation);
					return r;	
				}
			}
			else if(word.equals("index"))
			{
				String indexname = "";
				String attributename = "";
				String tablename = "";
				if(!(word = getWord(sql)).isEmpty())
					indexname = word;
				else
				{
					//Error
					System.out.println("Error in syntax");
					index = 0;
					return null;
				}
				
				if(!(word = getWord(sql)).equals("on"))
				{
					System.out.println("Error in syntax");
					index = 0;
					return null;
				}
				word = getWord(sql);
				tablename = word;
				if(!(word = getWord(sql)).equals("("))
				{
					System.out.println("Error in syntax");
					index = 0;
					return null;
				}
				word = getWord(sql);
				attributename = word;
				if(!(word = getWord(sql)).equals(")"))
				{
					System.out.println("Error in syntax");
					index = 0;
					return null;
				}
				index = 0;
				parses.add(indexname);
				parses.add(tablename);
				parses.add(attributename);
				Request r = new Request(2, parses, null, null, 0);
				return r;
			}
			else
			{
				index = 0;
				return null;
				//Error
			}
			
		}
		//select from
		else if(word.equals("select"))
		{
			String tablename = "";
			if(!(word = getWord(sql)).equals("*"))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
			}
			if(!(word = getWord(sql)).equals("from"))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
			}
			word = getWord(sql);
			tablename = word;
			parses.add(tablename);
			Condition condition = null;
			if((word = getWord(sql)).isEmpty())
			{
				index = 0;
				Request r = new Request(3, parses, null, null, 0);
				return r;
			}
			else if(word.equals("where"))
			{
				Vector<String> attributename = new Vector<String>();
				Vector<String> toCompare = new Vector<String>();
				Vector<Operation> ops = new Vector<Operation>();
				Vector<String> conjunction = new Vector<String>();
				word = getWord(sql);
				while(!word.isEmpty())
				{
					attributename.addElement(word);
					word = getWord(sql);
					switch(word)
					{
					case "=":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.EQUAL);
						break;
					case "<>":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.NOT_EQUAL);
						break;
					case "<":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.LESS);
						break;
					case ">":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.MORE);
						break;
					case "<=":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.LESS_EQUAL);
						break;
					case ">=":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.MORE_EQUAL);
						break;
					default:
						//Error
						index = 0;
						return null;
					}
					word = getWord(sql);
					if(word.equals("and") || word.equals("or"))
					{
						conjunction.addElement(word);
						word = getWord(sql);
					}
				}
				condition = new Condition(attributename, toCompare, ops, conjunction);
				Request r = new Request(4, parses, condition, null, 0);
				index = 0;
				return r;
			}
			index = 0;

		}
		//drop table or index
		else if(word.equals("drop"))
		{
			word = getWord(sql);
			if(word.equals("table"))
			{
				String tablename = "";
				word = getWord(sql);
				if(!(word.isEmpty()))
				{
					index = 0;
					tablename = word;
					parses.addElement(tablename);
					Request r = new Request(5, parses, null, null, 0);
					return r;
				}
				else
				{
					System.out.println("Syntax Error: no table name!");
					index = 0;
					return null;
					//Error
				}
				
			}
			else if(word.equals("index"))
			{
				String indexname = "";
				word = getWord(sql);
				if(!word.isEmpty())
				{
					index = 0;
					indexname = word;
					parses.addElement(indexname);
					Request r = new Request(6, parses, null, null, 0);
					return r;
				}
				else
				{
					System.out.println("Error in syntax");
					index = 0;
					return null;
					//Error
				}
			}
			else
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
		}
		//insert into
		else if(word.equals("insert"))
		{
			int count = 0;
			String tablename = "";
			String insertvalue = "";
			word = getWord(sql);
			if(!word.equals("into"))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			if(!word.isEmpty())
			{
				tablename = word;
				parses.addElement(tablename);
			}
			word = getWord(sql);
			if(!word.equals("values"))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			if(!word.equals("("))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			while(!word.isEmpty() && !word.equals(")"))
			{
				insertvalue = word;
				parses.addElement(insertvalue);
				count++;
				if((word = getWord(sql)).equals(","))
					word = getWord(sql);
			}
			if(!word.equals(")"))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
			index = 0;
			Request r = new Request(7, parses, null,null, count);
			return r;
		}
		//delete from
		else if(word.equals("delete"))
		{
			String tablename = "";
			word = getWord(sql);
			if(!word.equals("from"))
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			if(!word.isEmpty())
			{
				tablename = word;
				parses.addElement(tablename);
			}
			else
			{
				System.out.println("Error in syntax");
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			if(word.isEmpty())
			{
				index = 0;
				Request r = new Request(8, parses, null, null, 0);
				return r;
			}
			else if(word.equals("where"))
			{
				Vector<String> attributename = new Vector<String>();
				Vector<String> toCompare = new Vector<String>();
				Vector<Operation> ops = new Vector<Operation>();
				Vector<String> conjunction = new Vector<String>();
				word = getWord(sql);
				while(!word.isEmpty())
				{
					attributename.addElement(word);
					word = getWord(sql);
					switch(word)
					{
					case "=":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.EQUAL);
						break;
					case "<>":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.NOT_EQUAL);
						break;
					case "<":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.LESS);
						break;
					case ">":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.MORE);
						break;
					case "<=":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.LESS_EQUAL);
						break;
					case ">=":
						toCompare.addElement(getWord(sql));
						ops.addElement(Operation.MORE_EQUAL);
						break;
					default:
						//Error
						System.out.println("Error in syntax");
						index = 0;
						return null;
					}
					word = getWord(sql);
					if(word.equals("and") || word.equals("or"))
					{
						conjunction.addElement(word);
						word = getWord(sql);
					}
				}
				index = 0;
				Condition condition = new Condition(attributename, toCompare, ops, conjunction);
				Request r = new Request(9, parses, condition, null, 0);
				return r;
			}
			index = 0;
		}
		//exit sql
		else if(word.equals("exit"))
		{
			index = 0;
			Request r = new Request(10, null, null,null, 0);
			return r;
		}
		// file
		else if(word.equals("execfile"))
		{
			index = 0;
			String filename = getWord(sql);
			parses.addElement(filename);
			Request r = new Request(11, parses, null, null, 0);
			return r;
		}
		else
		{
			System.out.println("Error in syntax");
			index = 0;
			return null;
			//Error	
		}
		index = 0;
		return null;
	}
	
	static public Response excute(Request request) 
	{
		double startTime = System.currentTimeMillis();
		switch(request.type)
		{
		case 1:
			if(CatalogManager.tableExists(request.tablename)) {
				System.out.println("table already exsist");
				return new Response(false, 0);
			}
			System.out.println("Now Creating the table");
			return API.createTable(request);
		case 2:
			return API.createIndex(request);
		case 3:
		case 4:
			return API.select(request);

		case 5:
			return API.dropTable(request);
		case 6:
			return API.dropIndex(request);
		case 7:
			System.out.println("Now inserting");
			return API.insert(request);
		case 8:
		case 9:
			return API.delete(request);
		case 10:
			//Exit
			break;
		case 11:
			//Execute file
			break;
		}
		double endTime = System.currentTimeMillis();
		Response re = new Response(true, endTime - startTime);
		return re;
	}
	
	static String getWord(String sql)
	{
		int i;
		String word = "";
		int idx1, idx2;
		while((sql.charAt(index) == ' ' || sql.charAt(index) == '\t' || 
				sql.charAt(index) == 10) && sql.charAt(index) != 0)
		{
			index++;
		}
		idx1 = index;
		if(sql.charAt(index) == '(' || sql.charAt(index) == ',' ||
				sql.charAt(index) == ')')
		{
			index++;
			idx2 = index;
			word = sql.substring(idx1, idx2);
			return word;
		}
		else if(sql.charAt(index) == 39)
		{
			index++;
			while(sql.charAt(index) != 39 && sql.charAt(index) != 0)
				index++;
			if(sql.charAt(index) == 39)
			{
				idx1++;
				idx2 = index;
				index++;
				word = sql.substring(idx1, idx2);
				return word;
			}
			else
			{
				word = "";
				return word;
			}
		}
		else
		{
			while(sql.charAt(index) != ' ' && sql.charAt(index) != '(' &&
					sql.charAt(index) != 10 && sql.charAt(index) != 0 &&
					sql.charAt(index) != ')' && sql.charAt(index) != ',' && sql.charAt(index) != ';')
				index++;
			idx2 = index;
			if(idx1 != idx2)
				word = sql.substring(idx1, idx2);
			else
				word = "";
			return word;
		}
	}
}
