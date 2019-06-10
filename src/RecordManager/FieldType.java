package RecordManager;

public enum FieldType {
	INT, FLOAT, STRING, Empty;
	static public FieldType GetType(int i) {
		if(i == 0)
			return INT;
		
		if(i == 1)
			return FLOAT;
		
		if(i == 2)
			return STRING;
		
		return Empty;
	}
	static public int toInt(FieldType i) {
		if(i == INT)
			return 0;
		
		if(i == FLOAT)
			return 1;
		
		if(i == STRING)
			return 2;
		
		return -1;
	}
}

