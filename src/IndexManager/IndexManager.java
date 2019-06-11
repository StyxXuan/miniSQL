 package IndexManager;

import java.lang.String;
import java.io.File;
import BufferManager.*;
import RecordManager.*;

public class IndexManager {
    public static BPlusTree<Integer> iTree;
    public static BPlusTree<Float> fTree;
    public static BPlusTree<String> sTree;
    public static String indexFileName;

    public static void InitTree(Attribute attribute)
    {
        //valid int + size + key + value
        int degree = (Block.Size - 8) / (attribute.length + Integer.SIZE / 8);
        switch (attribute.Type)
        {
            case INT:
                iTree = new BPlusTree<Integer>(indexFileName, attribute.length, degree, attribute.Type);
                break;

            case FLOAT:
                fTree = new BPlusTree<Float>(indexFileName, attribute.length, degree, attribute.Type);
                break;

            case STRING:
                sTree = new BPlusTree<String>(indexFileName, attribute.length, degree, attribute.Type);
                break;

            default: System.out.println("Invalid type!"); break;
        }
    }
	public static boolean createIndex(Table table, Attribute attribute) {
        //Create new index file
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        File fp = new File(indexFileName);
        try
        {
            if(fp.exists())
            {
                System.out.println("Index already exists!");
                return false;
            }
            else
            {
                fp.createNewFile();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //Set up BPlusTree
        InitTree(attribute);
        //Insert key-value
        String fileName = BufferManager.tableFileNameGet(table.TableName);
        Block b = BufferManager.FindBlock(fileName, 0);
        int TupSize = table.Row.size();
        int MaxTupNum = BufferManager.Max_Block / TupSize;
        int CountTup = 0, RowIndex = 0, InitOffset = 0, offset = 0, blockOfs = 0;
        for(int i=0; i < table.Row.attrinum; i++) {
            if (table.Row.attlist.get(i).attriName == attribute.attriName)
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
        while(CountTup  < table.RecordNum) {
            if(RowIndex >= MaxTupNum) {
                b = BufferManager.GetNextBlock(b);
                offset += RowIndex + 1;
                RowIndex = 0;
            }
            if(b.GetInt(RowIndex * TupSize) != 0) {
                blockOfs = RowIndex * TupSize + InitOffset + 4;
                switch(attribute.Type) {
                    case INT:
                        iTree.insert(b.GetInt(blockOfs), offset + RowIndex); break;
                    case FLOAT:
                        fTree.insert(b.GetFloat(blockOfs), offset + RowIndex); break;
                    case STRING:
                        sTree.insert(b.GetString(blockOfs, attribute.length), offset + RowIndex); break;
                    default:
                        break;
                }
                RowIndex++;
                CountTup++;
            }
        }
        //Write to file
        writeToBuffer(attribute.Type);
		return true;
	}

	public static boolean dropIndex(Table table, Attribute attribute) {
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
	public static int select(Table table, Attribute attribute, String key) {
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        int resAddress = -1;
        //Read BPlusTree from buffer
        InitTree(attribute);
        readFromBuffer(attribute.Type);
        switch(attribute.Type) {
            case INT:
                resAddress = iTree.search(Integer.parseInt(key));
                break;

            case FLOAT:
                resAddress = fTree.search(Float.parseFloat(key));
                break;

            case STRING:
                resAddress = sTree.search(key);
                break;

            default: break;
        }
        return resAddress;
	}

	public static boolean insert(Table table, Attribute attribute, String key, int addr) {
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        InitTree(attribute);
        readFromBuffer(attribute.Type);
        switch(attribute.Type) {
            case INT:
                iTree.insert(Integer.parseInt(key), addr);
                break;

            case FLOAT:
                fTree.insert(Float.parseFloat(key), addr);
                break;

            case STRING:
                sTree.insert(key, addr);
                break;

            default: return false;
        }
        writeToBuffer(attribute.Type);
		return true;
	}

	public static boolean delete(Table table, Attribute attribute, String key) {
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
        InitTree(attribute);
        readFromBuffer(attribute.Type);
        switch(attribute.Type) {
            case INT:
                iTree.delete(Integer.parseInt(key));
                break;

            case FLOAT:
                fTree.delete(Float.parseFloat(key));
                break;

            case STRING:
                sTree.delete(key);
                break;

            default: return false;
        }
        writeToBuffer(attribute.Type);
        return true;
	}

    public static void readFromBuffer(FieldType type)
    {
        Block indexBlock = BufferManager.FindBlock(indexFileName, 0);
        switch (type)
        {
            case INT:
                iTree.root = readIntFromBlock(indexBlock);
                break;

            case FLOAT:
                fTree.root = readFloatFromBlock(indexBlock);
                break;

            case STRING:
                sTree.root = readStringFromBlock(indexBlock);
                break;

            default: return;
        }
    }


    public static TreeNode<Integer> readIntFromBlock(Block indexBlock)
    {
        int address = 0, offset = 0;
        TreeNode<Integer> node = null, child = null;
        if (indexBlock.GetInt(0) == 0)  //leaf node
        {
            node = new TreeNode<Integer>(NodeType.LEAF);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                node.keys.setElementAt(indexBlock.GetInt(offset), i);
                address = indexBlock.GetInt(offset + iTree.keySize);
                node.values.setElementAt(address, i);
                offset += iTree.keySize + 4;
            }
            if (iTree.lastLeafNode == null)
            {
                iTree.lastLeafNode = iTree.headLeafNode = node;
            }
            else
            {
                iTree.lastLeafNode.nextLeafNode = node;
                iTree.lastLeafNode = node;
            }
        }
        else
        {
            node = new TreeNode<Integer>(NodeType.INTERNAL);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                node.keys.setElementAt(indexBlock.GetInt(offset), i);
                offset += iTree.keySize;
            }
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                child = readIntFromBlock(indexBlock);
                child.parent = node;
                node.children.setElementAt(child, i);
            }
        }
        iTree.numOfNodes++;
        return node;
    }

    public static TreeNode<Float> readFloatFromBlock(Block indexBlock)
    {
        int address = 0, offset = 0;
        TreeNode<Float> node = null, child = null;
        if (indexBlock.GetInt(0) == 0)  //leaf node
        {
            node = new TreeNode<Float>(NodeType.LEAF);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                node.keys.setElementAt(indexBlock.GetFloat(offset), i);
                address = indexBlock.GetInt(offset + fTree.keySize);
                node.values.setElementAt(address, i);
                offset += fTree.keySize + 4;
            }
            if (fTree.lastLeafNode == null)
            {
                fTree.lastLeafNode = fTree.headLeafNode = node;
            }
            else
            {
                fTree.lastLeafNode.nextLeafNode = node;
                fTree.lastLeafNode = node;
            }
        }
        else
        {
            node = new TreeNode<Float>(NodeType.INTERNAL);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                node.keys.setElementAt(indexBlock.GetFloat(offset), i);
                offset += fTree.keySize;
            }
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                child = readFloatFromBlock(indexBlock);
                child.parent = node;
                node.children.setElementAt(child, i);
            }
        }
        fTree.numOfNodes++;
        return node;
    }

    public static TreeNode<String> readStringFromBlock(Block indexBlock)
    {
        int address = 0, offset = 0;
        TreeNode<String> node = null, child = null;
        if (indexBlock.GetInt(0) == 0)  //leaf node
        {
            node = new TreeNode<String>(NodeType.LEAF);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                node.keys.setElementAt(indexBlock.GetString(offset, sTree.keySize), i);
                address = indexBlock.GetInt(offset + sTree.keySize);
                node.values.setElementAt(address, i);
                offset += sTree.keySize + 4;
            }
            if (sTree.lastLeafNode == null)
            {
                sTree.lastLeafNode = sTree.headLeafNode = node;
            }
            else
            {
                sTree.lastLeafNode.nextLeafNode = node;
                sTree.lastLeafNode = node;
            }
        }
        else
        {
            node = new TreeNode<String>(NodeType.INTERNAL);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                node.keys.setElementAt(indexBlock.GetString(offset, sTree.keySize), i);
                offset += sTree.keySize;
            }
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                child = readStringFromBlock(indexBlock);
                child.parent = node;
                node.children.setElementAt(child, i);
            }
        }
        sTree.numOfNodes++;
        return node;
    }

    public static void writeToBuffer(FieldType type)
    {
        Block indexBlock = BufferManager.FindBlock(indexFileName, 0);
        switch (type)
        {
            case INT:
                writeIntToBlock(iTree.root, indexBlock);
                break;

            case FLOAT:
                writeFloatToBlock(fTree.root, indexBlock);
                break;

            case STRING:
                writeStringToBlock(sTree.root, indexBlock);
                break;

            default: return;
        }

    }

    public static void writeIntToBlock(TreeNode<Integer> node, Block indexBlock)
    {
        int offset = 0;
        if (node == null)
        {
            return;
        }
        else if (node.nodeType == NodeType.LEAF)
        {
            indexBlock.WriteInt(0, offset);  //0 for leaf node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                indexBlock.WriteInt(node.keys.get(i), offset);
                indexBlock.WriteInt(node.values.get(i), offset + iTree.keySize);
                offset += iTree.keySize + Integer.SIZE / 8;
            }
        }
        else
        {
            indexBlock.WriteInt(1, offset);  //1 for internal node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                indexBlock.WriteInt(node.keys.get(i), offset);
                offset += iTree.keySize;
            }
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                writeIntToBlock(node.children.get(i), indexBlock);
            }
        }
    }

    public static void writeFloatToBlock(TreeNode<Float> node, Block indexBlock)
    {
        int offset = 0;
        if (node == null)
        {
            return;
        }
        else if (node.nodeType == NodeType.LEAF)
        {
            indexBlock.WriteInt(0, offset);  //0 for leaf node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                indexBlock.WriteFloat(node.keys.get(i), offset);
                indexBlock.WriteInt(node.values.get(i), offset + fTree.keySize);
                offset += fTree.keySize + Integer.SIZE / 8;
            }
        }
        else
        {
            indexBlock.WriteInt(1, offset);  //1 for internal node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                indexBlock.WriteFloat(node.keys.get(i), offset);
                offset += fTree.keySize;
            }
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                writeFloatToBlock(node.children.get(i), indexBlock);
            }
        }
    }

    public static void writeStringToBlock(TreeNode<String> node, Block indexBlock)
    {
        int offset = 0;
        if (node == null)
        {
            return;
        }
        else if (node.nodeType == NodeType.LEAF)
        {
            indexBlock.WriteInt(0, offset);  //0 for leaf node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                indexBlock.WriteString(node.keys.get(i), offset);
                indexBlock.WriteInt(node.values.get(i), offset + sTree.keySize);
                offset += sTree.keySize + Integer.SIZE / 8;
            }
        }
        else
        {
            indexBlock.WriteInt(1, offset);  //1 for internal node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                indexBlock.WriteString(node.keys.get(i), offset);
                offset += sTree.keySize;
            }
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                writeStringToBlock(node.children.get(i), indexBlock);
            }
        }
    }
}
