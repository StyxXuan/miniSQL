package IndexManager;

import java.util.*;
import java.lang.String;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import BufferManager.*;
import RecordManager.*;

public class IndexManager {
    public static BPlusTree<int> iTree;
    public static BPlusTree<float> fTree;
    public static BPlusTree<String> sTree;
    public static String indexFileName;

    public void InitTree(Table table, Atrribute attribute)
    {
        //valid int + size + key + value
        int degree = (Block.Size - 8) / (attribute.length + Integer.SIZE / 8);
        switch (attribute.Type)
        {
            case FieldType.INT:
                iTree = new BPlusTree<>(fileName, attribute.length, degree);
                break;

            case FieldType.FLOAT:
                fTree = new BPlusTree<>(fileName, attribute.length, degree);
                break;

            case FieldType.STRING:
                sTree = new BPlusTree<>(fileName, attribute.length, degree);
                break;

            default: System.out.println("Invalid type!"); return false;
        }
    }
	public static boolean createIndex(Table table, Atrribute attribute) {
        //Create new index file
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        File fp = new File(indexFileName);
        if(fp.exists())
        {
            System.out.println("Index already exists!");
            return false;
        }
        fp.createNewFile();
        //Set up BPlusTree
        InitTree(attribute);
        //Insert key-value
        String fileName = BufferManager.tableFileNameGet(table.TableName);
        Block b = BufferManager.FindBlock(fileName, 0);
        int TupSize = table.Row.size();
        int MaxTupNum = BufferManager.Max_Block / TupSize;
        int CountTup = 0, RowIndex = 0, InitOffset = 0, offset = 0;
        for(int i=0; i < table.Row.attrinum; i++) {
            if (table.Row.attlist.get(i).attriName = attribute.attriName)
            {
                break;
            }
            switch(table.Row.attlist.get(i).Type) {
                case INT:
                case FLOAT: InitOffset += 4; break;
                case STRING: InitOffset += table.Row.attlist.get(i).length; break;
                default: break;
            }
        }
        offset = InitOffset;
        while(CountTup  < table.RecordNum) {
            if(RowIndex >= MaxTupNum) {
                b = BufferManager.GetNextBlock(b);
                RowIndex = 0;
            }
            if(b.GetInt(RowIndex * TupSize) != 0) {
                CountTup++;
                switch(attribute.Type) {
                    case INT:
                        iTree.insert(b.GetInt(RowIndex * TupSize + InitOffset), offset); break;
                    case FLOAT:
                        fTree.insert(b.GetFloat(RowIndex * TupSize + InitOffset), offset); break;
                    case STRING:
                        sTree.insert(b.GetString(RowIndex * TupSize + InitOffset, attribute.length), offset); break;
                    default:
                        break;
                }
                offset += TupSize;
            }
            RowIndex++;
        }
        //Write to file
        switch(attribute.Type) {
            case INT: iTree.writeToBuffer(indexFileName, attribute.Type); break;
            case FLOAT: fTree.writeToBuffer(indexFileName, attribute.Type); break;
            case STRING: sTree.writeToBuffer(indexFileName, attribute.Type); break;
            default: break;
        }
		return true;
	}

	public static boolean dropIndex(Table table, Atrribute attribute) {
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        BufferManager.RemoveBlockFromBuffer(indexFileName);  //Need to be implemented in Buffermanager.java
        File fp = new File(indexFileName);
        if (!fp.delete())
        {
            System.out.println("Failed to drop index!");
            return false;
        }
		return true;
	}

    //single-equivalent select
	public static int select(Table table, Atrribute attribute, String key) {
        int resAddress = -1;
        //Read BPlusTree from buffer
        InitTree(attribute);
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        switch(attribute.Type) {
            case INT:
                iTree.readFromBuffer(indexFileName, attribute.Type);
                resAddress = iTree.search(Integer.parseInt(key));
                break;

            case FLOAT:
                fTree.readFromBuffer(indexFileName, attribute.Type);
                resAddress = fTree.search(Float.parseFloat(key));
                break;

            case STRING:
                sTree.readFromBuffer(indexFileName, attribute.Type);
                resAddress = sTree.search(key);
                break;

            default: break;
        }
        return resAddress;
	}

	public static boolean insert(Table table, Atrribute attribute, String key, int addr) {
        InitTree(attribute);
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        switch(attribute.Type) {
            case INT:
                iTree.readFromBuffer(indexFileName, attribute.Type);
                iTree.insert(Integer.parseInt(key), addr);
                iTree,writeToBuffer(indexFileName, attribute.Type);
                break;

            case FLOAT:
                fTree.readFromBuffer(indexFileName, attribute.Type);
                fTree.insert(Float.parseFloat(key), addr);
                fTree,writeToBuffer(indexFileName, attribute.Type);
                break;

            case STRING:
                sTree.readFromBuffer(indexFileName, attribute.Type);
                sTree.insert(key, addr);
                sTree,writeToBuffer(indexFileName, attribute.Type);
                break;

            default: return false;
        }
		return true;
	}

	public static int delete(Table table, Atrribute attribute, String key) {
        InitTree(attribute);
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        switch(attribute.Type) {
            case INT:
                iTree.readFromBuffer(indexFileName, attribute.Type);
                iTree.delete(Integer.parseInt(key));
                iTree,writeToBuffer(indexFileName, attribute.Type);
                break;

            case FLOAT:
                fTree.readFromBuffer(indexFileName, attribute.Type);
                fTree.delete(Float.parseFloat(key));
                fTree,writeToBuffer(indexFileName, attribute.Type);
                break;

            case STRING:
                sTree.readFromBuffer(indexFileName, attribute.Type);
                sTree.delete(key);
                sTree,writeToBuffer(indexFileName, attribute.Type);
                break;

            default: return false;
        }
        return true;
	}
}
