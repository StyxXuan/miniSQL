package IndexManager;

import java.util.Vector;
import java.lang.String;
import javax.naming.directory.SearchResult;

enum NodeType
{
    LEAF, INTERNAL;
}

/*
 *Tree node class
 */
class TreeNode <T extends Comparable<T> >
{
    public final T initKey = null;
    public static int degree;
    public int numOfKeys;
    public NodeType nodeType;
    public TreeNode <T> parent;             //Parent node
    public TreeNode <T> nextLeafNode;       //Next leaf node
    public Vector <T> keys;                 //Search keys
    public Vector < TreeNode<T> > children; //Children nodes
    public Vector <Integer> values;         //Value:in-block address

    public TreeNode(NodeType nodeType)
    {
        numOfKeys = 0;
        this.nodeType = nodeType;
        parent = nextLeafNode = null;
        //An extra vector node for redundancy
        keys = new Vector<T>(degree + 1);
        children = new Vector < TreeNode<T> >(degree + 2);
        values = new Vector <Integer>(degree + 1);
        for (int i = 0; i <= degree; i++)
        {
            keys.add(initKey);
            values.add(0);
            children.add(null);
        }
    }

    
    /*
     *Search key in the tree node, and remian the index in "result"
     */
    public boolean searchKey(T key, Result<T> result)
    {
        if (numOfKeys <= 0 || key.compareTo(keys.get(0)) < 0)
        {
            result.index = 0;
        }
        else if (key.compareTo(keys.get(numOfKeys - 1)) > 0)
        {
            result.index  = numOfKeys;
        }
        else
        {
            //Binary search
            int start = 0, end = numOfKeys - 1, middle = 0;
            while (start <= end)
            {
                middle = (start + end) / 2;
                if (key.compareTo(keys.get(middle)) == 0)
                {
                    //Found
                    result.index = middle;
                    return true;
                }
                else if (key.compareTo(keys.get(middle)) < 0)
                {
                    end = middle - 1;
                }
                else
                {
                    start = middle + 1;
                }
            }
            //Not found, make index the a proper position
            result.index = start;
        }
        return false;
    }

    /*
     *Split the node into 2 sub-nodes
     *Return the new node
     */
    public TreeNode<T> splitNode(Result<T> result)
    {
        TreeNode<T> newNode = new TreeNode<T>(NodeType.LEAF);
        int midIndex = (degree - 1) / 2;  //(N-1) / 2
        if (nodeType == NodeType.LEAF)
        {
            result.key = keys.get(midIndex + 1);
            //Copy the keys and values in the right half to new node
            for (int i = midIndex + 1; i < degree; i++)
            {
                newNode.keys.setElementAt(keys.get(i), i - midIndex - 1);
                newNode.values.setElementAt(values.get(i), i - midIndex - 1);
                keys.setElementAt(null, i);
                values.setElementAt(null, i);
            }
            //Add pointers to next node and parent
            newNode.nextLeafNode = nextLeafNode;
            nextLeafNode = newNode;
            newNode.parent = parent;
            newNode.numOfKeys = degree - midIndex - 1;
            numOfKeys = midIndex + 1;
        }
        else if (nodeType == NodeType.INTERNAL)
        {
            newNode.nodeType = NodeType.INTERNAL;  //Split as a internal node
            result.key = keys.get(midIndex);
            //Copy the keys and children in the right half to new node
            for (int i = midIndex + 1; i < degree; i++)
            {
                newNode.keys.setElementAt(keys.get(i), i - midIndex - 1);
                newNode.children.setElementAt(children.get(i), i - midIndex - 1);
                children.get(i).parent = newNode;
                keys.setElementAt(null, i);
                children.setElementAt(null, i);
            }
            //Copy the last child
            newNode.children.setElementAt(children.get(degree), degree - midIndex - 1);
            children.get(degree).parent = newNode;
            children.setElementAt(null, degree);
            keys.setElementAt(null, midIndex);  //Keys[midIndex] is to be the key in parent node
            newNode.parent = parent;
            newNode.numOfKeys = degree - midIndex - 1;
            numOfKeys = midIndex;
        }
        return newNode;
    }
    /*
     *Add key-child pair to internal node
     */
    public int addKeyChild(T key, TreeNode<T> child)
    {
        int index = 0;
        Result<T> result = new Result<>();
        if (nodeType != NodeType.INTERNAL)
        {
            System.out.println("Add key into an non-internal node!");
            return -1;
        }
        if (numOfKeys == 0)
        {
            keys.setElementAt(key, 0);
            children.setElementAt(child, 1);
            child.parent = this;
            numOfKeys++;
            index = 0;
        }
        else
        {
            if (searchKey(key, result))
            {
                System.out.println("Already existing key!");
            }
            else
            {
                index = result.index;
                children.setElementAt(children.get(numOfKeys), numOfKeys + 1);
                for (int i = numOfKeys; i > index + 1; i--)
                {
                    keys.setElementAt(keys.get(i - 1), i);
                    children.setElementAt(children.get(i - 1), i);
                }
                keys.setElementAt(keys.get(index), index + 1);
                keys.setElementAt(key, index);
                children.setElementAt(child, index + 1);
                child.parent = this;
                numOfKeys++;
            }
        }
        return index;
    }

    public int addKeyValue(T key, int value)
    {
        int index = 0;
        Result<T> result = new Result<>();
        if (nodeType != NodeType.LEAF)
        {
            System.out.println("Add key-value into an non-leaf node!");
            return -1;
        }
        if (numOfKeys == 0)
        {
            keys.setElementAt(key, 0);
            values.setElementAt(value, 0);
            numOfKeys++;
            index = 0;
        }
        else
        {
            if (searchKey(key, result))
            {
                System.out.println("Already existing key!");
            }
            else
            {
                index = result.index;
                for (int i = numOfKeys; i > index; i--)
                {
                    keys.setElementAt(keys.get(i - 1), i);
                    values.setElementAt(values.get(i - 1), i);
                }
                keys.setElementAt(key, index);
                values.setElementAt(value, index);
                numOfKeys++;
            }
        }
        return index;
    }

    public boolean removeKey(int index)
    {
        if (index < 0 || index >= numOfKeys)
        {
            System.out.println("Index Overflow!");
            return false;
        }
        for (int i = index; i < numOfKeys - 1; i++)
        {
            keys.setElementAt(keys.get(i + 1), i);
        }
        keys.setElementAt(null, numOfKeys - 1);
        if (nodeType == NodeType.LEAF)
        {
            for (int i = index; i < numOfKeys - 1; i++)
            {
                values.setElementAt(values.get(i + 1), i);
            }
            values.setElementAt(null, numOfKeys - 1);
        }
        else
        {
            for (int i = index + 1; i < numOfKeys; i++)
            {
                children.setElementAt(children.get(i + 1), i);
            }
            children.setElementAt(null, numOfKeys);
        }
        numOfKeys--;
        return true;
    }
}

class Result <T extends Comparable<T>>
{
    TreeNode<T> resNode;
    int index;
    T key;
}

public class BPlusTree <T extends Comparable<T>>
{
    public int degree;
    public int keySize;
    public int height;
    public int numOfNodes;
    public String filePath;
    public TreeNode<T> root;
    public TreeNode<T> headLeafNode;

    public BPlusTree(String filePath, int keySize, int degree)
    {
        this.filePath = filePath;
        this.keySize = keySize;
        this.degree = TreeNode.degree = degree;
        numOfNodes = height = 0;
        root = headLeafNode = null;
    }

    public boolean findAtLeaf(TreeNode<T> node, T key, Result<T> result)
    {
        if (node == null)
        {
            return false;
        }
        if (node.searchKey(key, result))
        {
            if (node.nodeType == NodeType.LEAF)
            {
                result.resNode = node;
            }
            else
            {
                result.resNode = node.children.get(result.index + 1);
                while (result.resNode.nodeType != NodeType.LEAF)
                {
                    result.resNode = result.resNode.children.get(0);
                }
                result.index = 0;
            }
            return true;
        }
        else
        {
            if (node.nodeType == NodeType.LEAF)
            {
                result.resNode = node;
                result.index = 0;
                return false;
            }
            else
            {
                return findAtLeaf(node.children.get(result.index), key, result);
            }
        }
    }

    public int search(T key)
    {
        Result<T> result = new Result<>();
        if (findAtLeaf(root, key, result))
        {
            return result.resNode.values.get(result.index);
        }
        else
        {
            return -1;
        }
    }
 
    public boolean insert(T key, int value)
    {
        Result<T> result = new Result<>();
        if (root == null)
        {
            root = new TreeNode<>(NodeType.LEAF);
            root.keys.setElementAt(key, 0);
            root.values.setElementAt(value, 0);
            root.numOfKeys = 1;
            headLeafNode = root;
            height = numOfNodes = 1;
            return true;
        }
        if (findAtLeaf(root, key, result))
        {
            System.out.println("Duplicated key!");
            return false;
        }
        else
        {
            result.resNode.addKeyValue(key, value);
            if (result.resNode.numOfKeys == degree)
            {
                insertAdjust(result.resNode);
            }
            return true;
        }
    }

    public void insertAdjust(TreeNode<T> node)
    {
        Result<T> result = new Result<>();
        TreeNode<T> newNode = node.splitNode(result);
        numOfNodes++;
        if (node == root)
        {
            root = new TreeNode<>(NodeType.INTERNAL);
            root.children.setElementAt(node, 0);
            root.children.setElementAt(newNode, 1);
            root.keys.setElementAt(result.key, 0);
            node.parent = newNode.parent = root;
            root.numOfKeys++;
            height++;
            numOfNodes++;
        }
        else
        {
            TreeNode<T> parent = node.parent;
            parent.addKeyChild(result.key, newNode);
            if (parent.numOfKeys == degree)
            {
                insertAdjust(parent);
            }
        }
    }

    public void delete(T key)
    {
        Result<T> result = new Result<>();
        if (root == null)
        {
            System.out.println("Empty tree!");
            return;
        }
        if (findAtLeaf(root, key, result))
        {
            int index = result.index;
            if (index == 0 && result.resNode != headLeafNode)
            {
                TreeNode<T> parent = result.resNode.parent;
                int newIndex = -1;
                while (parent != null)
                {
                    parent.searchKey(key, result);
                    newIndex = result.index;
                    if (newIndex >= 0)
                    {
                        break;
                    }
                    parent = parent.parent;
                }
                parent.keys.setElementAt(result.resNode.keys.get(1), newIndex);
                result.resNode.removeKey(0);
            }
            else
            {
                result.resNode.removeKey(result.index);
            }
            deleteAdjust(result.resNode);
        }
        else
        {
            System.out.println("No such a key!");
        }
    }

    public void deleteAdjust(TreeNode<T> node)
    {
        int midIndex = (degree - 1) / 2, index = 0;
        TreeNode<T> parent = node.parent, sibling = null;
        Result<T> result = new Result<>();
        if (node == root)
        {
            if (node.numOfKeys > 0)
            {
                return;
            }
            else if (node.nodeType == NodeType.LEAF)
            {
                node = null;
                root = headLeafNode = null;
                height = numOfNodes = 0;
            }
            else
            {
                root = node.children.get(0);
                root.parent = node = null;
                height--;
                numOfNodes--;
            }
        }
        else if (node.nodeType == NodeType.LEAF && node.numOfKeys <= midIndex)
        {
            parent.searchKey(node.keys.get(0), result);
            index = result.index;
            if (parent.children.get(0) != node && index + 1 == parent.numOfKeys)
            {
                sibling = parent.children.get(index);  //left sibling
                if (sibling.numOfKeys > midIndex + 1)
                {
                    node.addKeyValue(sibling.keys.get(sibling.numOfKeys - 1), sibling.values.get(sibling.numOfKeys - 1));
                    sibling.removeKey(sibling.numOfKeys - 1);
                    parent.keys.setElementAt(node.keys.get(0), index);
                }
                else
                {
                    for (int i = 0; i < node.numOfKeys; i++)
                    {
                        sibling.keys.setElementAt(node.keys.get(i), i + sibling.numOfKeys);
                        sibling.values.setElementAt(node.values.get(i), i + sibling.numOfKeys);
                    }
                    sibling.numOfKeys += node.numOfKeys;
                    sibling.nextLeafNode = node.nextLeafNode;
                    parent.removeKey(index);
                    numOfNodes--;
                    node = null;
                    deleteAdjust(parent);
                }
            }
            else
            {
                if (parent.children.get(0) == node)
                {
                    sibling = parent.children.get(1);
                }
                else
                {
                    sibling = parent.children.get(index + 2);  //right sibling [including the case of most left node]
                }
                if (sibling.numOfKeys > midIndex + 1)
                {
                    node.addKeyValue(sibling.keys.get(0), sibling.values.get(0));
                    sibling.removeKey(0);
                    if (parent.children.get(0) == node)
                    {
                        parent.keys.setElementAt(sibling.keys.get(0), 0);
                    }
                    else
                    {
                        parent.keys.setElementAt(sibling.keys.get(0), index + 1);
                    }
                }
                else
                {
                    for (int i = 0; i < sibling.numOfKeys; i++)
                    {
                        node.keys.setElementAt(sibling.keys.get(i), i + node.numOfKeys);
                        node.values.setElementAt(sibling.values.get(i), i + node.numOfKeys);
                    }
                    node.numOfKeys += sibling.numOfKeys;
                    node.nextLeafNode = sibling.nextLeafNode;
                    if (parent.children.get(0) == node)
                    {
                        parent.removeKey(0);
                    }
                    else
                    {
                        parent.removeKey(index + 1);
                    }
                    node = null;
                    numOfNodes--;
                    deleteAdjust(parent);
                }
            }
        }
        else if (node.nodeType == NodeType.INTERNAL && node.numOfKeys < midIndex)
        {
            parent.searchKey(mostLeftKey(node), result);
            index = result.index;
            if (parent.children.get(0) != node && index + 1 == parent.numOfKeys)
            {
                sibling = parent.children.get(index);
                if (sibling.numOfKeys > midIndex)
                {
                    node.addKeyChild(parent.keys.get(index), sibling.children.get(sibling.numOfKeys));
                    sibling.children.get(sibling.numOfKeys).parent = node;
                    sibling.removeKey(sibling.numOfKeys);
                    parent.keys.setElementAt(mostLeftKey(node), index);
                }
                else
                {
                    sibling.keys.setElementAt(parent.keys.get(index), sibling.numOfKeys);
                    sibling.numOfKeys++;
                    parent.removeKey(index);
                    for (int i = 0; i < node.numOfKeys; i++)
                    {
                        sibling.keys.setElementAt(node.keys.get(i), i + sibling.numOfKeys);
                        sibling.children.setElementAt(node.children.get(i), i + sibling.numOfKeys);
                        node.children.get(i).parent = sibling;
                    }
                    sibling.numOfKeys += node.numOfKeys;
                    sibling.children.setElementAt(node.children.get(node.numOfKeys), sibling.numOfKeys);
                    node.children.get(node.numOfKeys).parent = sibling;
                    node = null;
                    numOfNodes--;
                    deleteAdjust(parent);
                }
            }
            else
            {
                if (parent.children.get(0) == node)
                {
                    sibling = parent.children.get(1);
                }
                else
                {
                    sibling = parent.children.get(index + 2);  //right sibling [including the case of most left node]
                }
                if (sibling.numOfKeys > midIndex)
                {
                    node.addKeyChild(parent.keys.get(index + 1), sibling.children.get(0));
                    sibling.children.get(0).parent = node;
                    sibling.removeKey(0);
                    if (parent.children.get(0) == node)
                    {
                        parent.keys.setElementAt(mostLeftKey(sibling), 0);
                    }
                    else
                    {
                        parent.keys.setElementAt(mostLeftKey(sibling), index + 1);
                    }
                }
                else
                {
                    if (parent.children.get(0) == node)
                    {
                        parent.removeKey(0);
                        node.keys.setElementAt(parent.keys.get(0), node.numOfKeys);
                    }
                    else
                    {
                        parent.removeKey(index + 1);
                        node.keys.setElementAt(parent.keys.get(index + 1), node.numOfKeys);
                    }
                    node.numOfKeys++;
                    for (int i = 0; i < sibling.numOfKeys; i++)
                    {
                        node.keys.setElementAt(sibling.keys.get(i), i + node.numOfKeys);
                        node.children.setElementAt(sibling.children.get(i), i + node.numOfKeys);
                        sibling.children.get(i).parent = node;
                    }
                    node.numOfKeys += sibling.numOfKeys;
                    node.children.setElementAt(sibling.children.get(sibling.numOfKeys), node.numOfKeys);
                    sibling.children.get(sibling.numOfKeys).parent = node;
                    sibling = null;
                    numOfNodes--;
                    deleteAdjust(parent);
                }
            }
        }
    }

    public T mostLeftKey(TreeNode<T> node)
    {
        while (node.nodeType != NodeType.LEAF)
        {
            node = node.children.get(0);
        }
        return node.keys.get(0);
    }

    /*
    public void printLeaf()
    {
        TreeNode<T> node = headLeafNode;
        System.out.print("Leaf:");
        while (node != null)
        {
            for (int i = 0; i < node.numOfKeys; i++)
            {
                System.out.print(" " + node.keys.get(i));
            }
            System.out.print(",");
            node = node.nextLeafNode;
        }
        System.out.println();
    }
    */

    public void printTree()
    {
        Vector< TreeNode<T> > queue = new Vector<>(degree); //Queue for node to be displayed
        int level = 0;
        TreeNode<T> node, headNode = root;
        queue.add(root);
        System.out.print("Height: " + height + ", numOfNodes: " + numOfNodes);
        while (!queue.isEmpty())
        {
            node = queue.get(0);
            if (node == headNode)
            {
                System.out.print("\nLevel_" + (level++) + ": ");
                headNode = headNode.children.get(0);  //The head node in the next level
            }
            //Print the current node
            System.out.print("[");
            for (int i = 0; i < node.numOfKeys; i++)
            {
                System.out.print(node.keys.get(i) + ",");
            }
            System.out.print("]");
            //Add children of internal node
            if (node.nodeType == NodeType.INTERNAL)
            {
                for (int i = 0; i <= node.numOfKeys; i++)
                {
                    queue.add(node.children.get(i));
                }
            }
            //Remove the front node in the queue
            queue.removeElementAt(0);
        }
        System.out.println("\n-----------------------------------");
    }

    public void read()
    {
    }

    public void write()
    {
    }

    public static void main(String [] args)
    {
        TreeNode.degree = 4;
        BPlusTree<String> tree = new BPlusTree<>("", 10, 4);
        tree.insert("Gold", 6);
        tree.printTree();

        tree.insert("Katz", 7);
        tree.printTree();

        tree.insert("Kim", 8);
        tree.printTree();

        tree.insert("Mozart", 9);
        tree.printTree();

        tree.insert("Singh", 10);
        tree.printTree();

        tree.insert("Srinivasan", 11);
        tree.printTree();
        
        tree.insert("Wu", 12);
        tree.printTree();

        tree.insert("Brandt", 1);
        tree.printTree();

        tree.insert("Califieri", 2);
        tree.printTree();

        tree.insert("Crick", 3);
        tree.printTree();

        tree.insert("Einstein", 4);
        tree.printTree();

        tree.insert("Elsaid", 5);
        tree.printTree();

        tree.delete("Srinivasan");
        tree.printTree();
        
        tree.delete("Gold");
        tree.printTree();

        tree.delete("Brandt");
        tree.printTree();
    }
}
