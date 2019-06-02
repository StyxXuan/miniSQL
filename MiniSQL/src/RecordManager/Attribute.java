package RecordManager;

public class Attribute {
	public FieldType Type;
	public String attriName;
	public int length, offset;
	public Boolean isPrimary, isUnique, hasIndex;
	public Attribute(FieldType Type, String attriName, int length, int offset) {
		this.Type = Type;
		this.attriName = attriName;
		this.length = length;
		this.offset = offset;
		this.isUnique = false;
		this.isUnique = false;
		this.hasIndex = false;
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