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
        int resAddress = -1;
        //Read BPlusTree from buffer
        InitTree(attribute);
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
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
        InitTree(attribute);
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
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
        InitTree(attribute);
        indexFileName = BufferManager.indexFileNameGet(table.TableName + "_" + attribute.attriName);
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
        /*
        Block indexBlock = BufferManager.FindBlock(indexFileName, 0);
        lastLeafNode = null;
        root = readFromBlock(indexBlock);
        */
    }

    public static void writeToBuffer(FieldType type)
    {
        /*
        Block indexBlock = BufferManager.FindBlock(this.fileName, 0);
        lastLeafNode = null;
        root = readFromBlock(indexBlock);
        */
    }
    /*
    private <T extends Comparable<T> > TreeNode<T> readFromBlock(Block indexBlock)
    {

        TreeNode<T> node = null, child = null;
        node = readNode(indexBlock);
        if (node.nodeType == NodeType.LEAF)//leaf node
        {
            if (lastLeafNode == null)
            {
                lastLeafNode = headLeafNode = node;
            }
            else
            {
                lastLeafNode.nextLeafNode = node;
                lastLeafNode = node;
            }
        }
        else
        {
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                child = readFromBlock(indexBlock);
                child.parent = node;
                node.children.setElementAt(child, i);
            }
        }
        return node;
    }


    public <T extends Comparable<T> >  TreeNode<T> readNode(Block indexBlock)
    {
        int address = 0, offset = 0;
        TreeNode<T> node = null, child = null;
        int iKey;
        float fKey;
        String sKey;
        if (indexBlock.GetInt(0) == 0)  //leaf node
        {
            node = new TreeNode<T>(NodeType.LEAF);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                switch (keyType)
                {
                    case INT:
                        iKey = indexBlock.GetInt(offset);
                        node.setKeyAt(iKey, i);
                        node.keys.setElementAt(iKey, i);
                        break;
                    case FLOAT:
                        fKey = indexBlock.GetFloat(offset);
                        break;
                    case STRING:
                        sKey = indexBlock.GetString(offset, keySize);
                        break;
                    default:
                        System.out.println("Read error!");
                        return null;
                }

                address = indexBlock.GetInt(offset + keySize);
                node.values.setElementAt(address, i);
                offset += keySize + 4;
            }
        }
        else
        {
            node = new TreeNode<T>(NodeType.INTERNAL);
            node.numOfKeys = indexBlock.GetInt(4);
            offset += 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                switch (keyType)
                {
                    case INT:
                        key = indexBlock.GetInt(offset);
                        break;
                    case FLOAT:
                        key = indexBlock.GetFloat(offset);
                        break;
                    case STRING:
                        key = indexBlock.GetString(offset);
                        break;
                    default:
                        System.out.println("Read error!");
                        return;
                }
                node.keys.setElementAt(key, i);
                offset += keySize;
            }
        }
        numOfNodes++;
        return node;
    }

    private void writeToBuffer()
    {
        Block indexBlock = BufferManager.FindBlock(this.fileName, 0);
        writeToBlock(root, indexBlock);
    }

    private void writeToBlock(TreeNode<T> node, Block indexBlock)
    {
        writeNode(node, indexBlock);
        if (node.nodeType == NodeType.INTERNAL)
        {
            for (int i = 0; i <= node.numOfKeys; i++)
            {
                indexBlock = BufferManager.GetNextBlock(indexBlock);
                writeToBlock(node.children.get(i), indexBlock);
            }
        }
    }

    private void writeNode(TreeNode<T> node, Block indexBlock)
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
                switch (keyType)
                {
                    case INT:
                        indexBlock.WriteInt((int)node.keys.get(i), offset);
                        break;
                    case FLOAT:
                        indexBlock.WriteFloat((float)node.keys.get(i), offset);
                        break;
                    case STRING:
                        indexBlock.WriteString((String)node.keys.get(i), offset);
                        break;
                    default:
                        System.out.println("Write error!");
                        return;
                }
                indexBlock.WriteInt(node.values.get(i), offset + keySize);
                offset += keySize + Integer.SIZE / 8;
            }
        }
        else
        {
            indexBlock.WriteInt(1, offset);  //1 for internal node
            indexBlock.WriteInt(node.numOfKeys, offset);  //size
            offset += 2 * Integer.SIZE / 8;
            for (int i = 0; i < node.numOfKeys; i++)
            {
                switch (keyType)
                {
                    case INT:
                        indexBlock.WriteInt((int)node.keys.get(i), offset);
                        break;
                    case FLOAT:
                        indexBlock.WriteFloat((float)node.keys.get(i), offset);
                        break;
                    case STRING:
                        indexBlock.WriteString((String)node.keys.get(i), offset);
                        break;
                    default:
                        System.out.println("Write error!");
                        return;
                }
                offset += keySize;
            }
        }
    }
    */
}
