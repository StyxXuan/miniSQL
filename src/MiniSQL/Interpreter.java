package MiniSQL;

import java.util.Vector;
import RecordManager.Condition;
import RecordManager.Condition.Operation;
import RecordManager.Attribute;
import RecordManager.FieldType;

public class Interpreter{
	static int index = 0;
	
	public Interpreter(String sql)
	{
		Request r = parse(sql);
		excute(r);
	}
	
	public static Request parse(String sql) {
		Vector<String> parses = null;
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
					//Error
					index = 0;
					return null;
				}
				word = getWord(sql);
				if(word != "(")
				{
					//Error
					index = 0;
					return null;
				}
				else
				{
					int len = 100; //length of string
					word = getWord(sql);
					Vector<Attribute> attriVec = null;
					while(!word.equals("primary") && !word.equals(")"))
					{
						String attributename = word;
						FieldType type;
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
						Attribute attri = new Attribute(type, attributename, len, 0);
						if(isUnique)
							attri.SetUnique();
						attriVec.add(attri);
						if(!word.equals(","))
						{
							if(!word.equals(")"))
							{
								//Error
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
								index = 0; 
								return null;
								//Error
							}
							primarylocation = i;
							word = getWord(sql);
							if(!word.equals(")"))
							{
								index = 0;
								return null;
								//Error
							}
						}
					}
					else
					{
						//Error
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
					index = 0;
					return null;
				}
				
				if(!(word = getWord(sql)).equals("on"))
				{
					index = 0;
					return null;
				}
				word = getWord(sql);
				tablename = word;
				if(!(word = getWord(sql)).equals("("))
				{
					index = 0;
					return null;
				}
				word = getWord(sql);
				attributename = word;
				if(!(word = getWord(sql)).equals(")"))
				{
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
				index = 0;
				return null;
			}
			if(!(word = getWord(sql)).equals("from"))
			{
				index = 0;
				return null;
			}
			word = getWord(sql);
			tablename = word;
			parses.add(tablename);
			Condition condition = null;
			if((word = getWord(sql)).isEmpty())
			{
				Request r = new Request(3, parses, null, null, 0);
				return r;
			}
			else if(word.equals("where"))
			{
				Vector<String> attributename = null;
				Vector<String> toCompare = null;
				Vector<Operation> ops = null;
				Vector<String> conjunction = null;
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
					index = 0;
					return null;
					//Error
				}
				
			}
			else if(word.equals("index"))
			{
				String indexname = "";
				String tablename = "";
				String attriname = "";
				word = getWord(sql);
				if(!word.isEmpty())
				{
					index = 0;
					indexname = word;
					parses.addElement(indexname);
					word = getWord(sql);
					if(word.equals("on"))
					{
						word = getWord(sql);
						if(!word.isEmpty())
						{
							tablename = word;
							parses.addElement(tablename);
						}
						word = getWord(sql);
						if(word.equals("("))
						{
							word = getWord(sql);
							if(!word.isEmpty())
							{
								attriname = word;
								parses.addElement(attriname);
							}	
						}
						word = getWord(sql);
						if(!word.equals(")"))
						{
							index = 0;
							return null;
							//Error
						}
					}
					Request r = new Request(6, parses, null, null, 0);
					return r;
				}
				else
				{
					index = 0;
					return null;
					//Error
				}
			}
			else
			{
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
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			if(!word.equals("("))
			{
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
				index = 0;
				//Error
			}
			word = getWord(sql);
			if(!word.isEmpty())
			{
				parses.addElement(tablename);
				tablename = word;
			}
			else
			{
				index = 0;
				return null;
				//Error
			}
			word = getWord(sql);
			if(word.isEmpty())
			{
				Request r = new Request(8, parses, null, null, 0);
				return r;
			}
			else if(word.equals("where"))
			{
				Vector<String> attributename = null;
				Vector<String> toCompare = null;
				Vector<Operation> ops = null;
				Vector<String> conjunction = null;
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
			index = 0;
			return null;
			//Error	
		}
		index = 0;
		return null;
	}
	
	public static void excute(Request request) 
	{
		switch(request.type)
		{
		case 1:
			API.createTable(request);
			break;
		case 2:
			API.createIndex(request);
			break;
		case 3:
		case 4:
			API.select(request);
			break;
		case 5:
			API.dropTable(request);
			break;
		case 6:
			API.dropIndex(request);
			break;
		case 7:
			API.insert(request);
			break;
		case 8:
		case 9:
			API.delete(request);
			break;
		case 10:
			//Exit
			break;
		case 11:
			//Excute file
			break;
		}
	}
	
	static String getWord(String sql)
	{
		sql = sql.toLowerCase();
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
