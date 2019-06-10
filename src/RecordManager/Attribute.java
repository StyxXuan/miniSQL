package RecordManager;

public class Attribute {
//	static int AttSize = 100;
	
	public FieldType Type;
	public String attriName;
	public int length, offset;
	public Boolean isPrimary = false, isUnique = false, hasIndex = false;
	
	public Attribute(FieldType Type, String attriName) {
		this.Type = Type;
		this.attriName = attriName;
		this.length = 4;
	}
	
	public Attribute(FieldType Type, String attriName, int length, int offset) {
		this.Type = Type;
		this.attriName = attriName;
		this.length = length;
		this.offset = offset;

	}
	
	public void SetPrimary() {
		this.isPrimary = true;
	}
	
	public void SetUnique() {
		this.isUnique = true;
	}
	
	public void SetIndex() {
		this.hasIndex = true;
	}
	
	
}
