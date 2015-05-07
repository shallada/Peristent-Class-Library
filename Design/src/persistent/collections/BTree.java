package persistent.collections;

import persistent.Persistable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class BTree<E extends Persistable> {
    private final int NODE_CAPACITY;
    private long numOfNodes;
    private PersistentArray pa;
    private BTreeNode<E> root;
    private E genericInstance;

    public BTree(int n, PersistentArray pa, E genericInstance) {
        this.NODE_CAPACITY = n + 1;
        this.pa = pa;
        this.genericInstance = genericInstance;

        root = new BTreeNode(0, NODE_CAPACITY, true);

        try {
            long index = pa.allocate();
            ByteBuffer buffer = pa.get(index);
            root.serialize(buffer);
        } catch(IOException e) {
            e.printStackTrace();
        }

        writeMetaData();
    }

    public void add(long key, E value) {
        BTreePair<E> pair = new BTreePair<E>(key, value);
        add(pair);
    }

    public void delete(BTreePair p) {

        boolean pairDeleted = false;

        Stack<Long> nodeStack = new Stack<Long>();
        nodeStack.push(root.getIndex());

        BTreeNode<E> currentNode = new BTreeNode<E>();

        while(!nodeStack.isEmpty()) {
            currentNode = this.getNodeFromPa(nodeStack.peek());

            if(currentNode.isLeaf()) {
                currentNode.remove(p);
                pairDeleted = true;

                if(currentNode.isEmpty()) {
                    try {
                        pa.delete(currentNode.getIndex());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    this.paUpdate(currentNode);
                }

                nodeStack.pop();
            }
            else {
                if(!pairDeleted) {
                    BTreePair<E> highPair = currentNode.getLowestPairGreaterThan(p.getKey());
                    BTreeNode<E> nodeDown = this.getNodeFromPa(highPair.getNextIndex());

                    nodeStack.push(nodeDown.getIndex());
                }
                else {

                    if(currentNode.isEmpty()) {
                        try {
                            pa.delete(currentNode.getIndex());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(currentNode.containsKey(p.getKey())) {
                        BTreePair<E> highPair = currentNode.getLowestPairGreaterThan(p.getKey());
                        BTreeNode<E> nodeDown = this.getNodeFromPa(highPair.getNextIndex());

                        highPair.setKey(nodeDown.getHighestKey());
                    }
                    nodeStack.pop();
                }
            }
        }
    }

    public E get(long key) {
        BTreeNode<E> node = getLeafNode(key);
        BTreePair<E> pair = node.getLowestPairGreaterThan(key);

        E value = null;

        if(pair != null && pair.getValue() != null) {
            value = pair.value;
        }

        return value;
    }

    public void update(long key, E value) {
        BTreeNode<E> node = this.getLeafNode(key);

        if(node != null) {
            BTreePair<E> pair = node.getLowestPairGreaterThan(key);
            pair.setValue(value);
            this.paUpdate(node);
        }
    }

    /*
    Helper methods
     */

    private void add(BTreePair p) {
        BTreeNode n = insert(p);

        if(n != null){
            BTreeNode newRoot = new BTreeNode(numOfNodes, NODE_CAPACITY, false);
            newRoot.put(0, new BTreePair<E>(root.getHighestKey(), null, root.getIndex()));
            newRoot.put(1, new BTreePair<E>(n.getHighestKey(), null, n.getIndex()));

            try {
                long index = pa.allocate();
                ByteBuffer buffer = pa.get(index);
                newRoot.serialize(buffer);
            } catch(IOException e) {
                e.printStackTrace();
            }

            root = newRoot;
            writeMetaData();
        }

    }

    private BTreeNode insert(BTreePair p) {
        boolean pairInserted = false;

        Stack<Long> nodeStack = new Stack<Long>();
        nodeStack.push(root.getIndex());

        BTreeNode<E> splitNode = null;
        BTreeNode<E> currentNode = new BTreeNode<E>();

        while(!nodeStack.isEmpty()) {
            currentNode = this.getNodeFromPa(nodeStack.peek());
            int numOfPairs = currentNode.getNumOfPairs();

            if(splitNode != null) {
                BTreePair<E> highPair = currentNode.getLowestPairGreaterThan(p.getKey());
                BTreeNode<E> nodeDown = this.getNodeFromPa(highPair.getNextIndex());

                highPair.setKey(nodeDown.getHighestKey()); //The node down has changed after the split and might have a new highest key

                BTreePair<E> insertPair = new BTreePair<E>(splitNode.getHighestKey(), null, splitNode.getIndex());
                int insertPoint = currentNode.getInsertPoint(insertPair.getKey());
                this.shiftAndInsert(currentNode, insertPoint, numOfPairs, insertPair);

                this.paUpdate(currentNode);

                nodeStack.pop();
                splitNode = null;
            }
            else if(!pairInserted) {
                if (currentNode.isLeaf()) {
                    int insertPoint = currentNode.getInsertPoint(p.getKey());
                    shiftAndInsert(currentNode, insertPoint, numOfPairs, p.clone());

                    this.paUpdate(currentNode);

                    nodeStack.pop();
                    pairInserted = true;
                }
                else {
                    BTreePair<E> highPair = currentNode.getLowestPairGreaterThan(p.getKey());
                    BTreeNode<E> nodeDown = this.getNodeFromPa(highPair.getNextIndex());
                    nodeStack.push(nodeDown.getIndex()); //Represents a recursive call, adding to the imaginary callstack

                    if (p.getKey() > highPair.getKey()) {
                        highPair.setKey(p.getKey()); //p's key is bigger, therefore we know it will be the new highest
                        this.paUpdate(currentNode); //currentNode's highest key has changed
                    }
                }
            }
            else {
                nodeStack.pop();
            }

            numOfPairs = currentNode.getNumOfPairs();
            if(numOfPairs >= NODE_CAPACITY)
                splitNode = split(currentNode);
        }

        return splitNode;
    }

    private BTreeNode<E> getLeafNode(long key) {
        BTreeNode<E> node = null;
        boolean leafReached = false;
        BTreeNode<E> currentNode = root;

        while(!leafReached) {
            if(currentNode.isLeaf()) {
                leafReached = true;
                if(currentNode.containsKey(key)) {
                    node = currentNode;
                }
            }
            else {
                BTreePair<E> highPair = currentNode.getLowestPairGreaterThan(key);
                currentNode = this.getNodeFromPa(highPair.getNextIndex());
            }
        }

        return node;
    }

    private void shiftAndInsert(BTreeNode<E> node, int insertPoint, int limit, BTreePair<E> p) {
        for (int i = limit; i > insertPoint; i--) {
            node.put(i, node.getPair(i - 1));
        }

        node.put(insertPoint, p);
    }

    private BTreeNode<E> split(BTreeNode n) {
        BTreeNode<E> newNode = new BTreeNode<E>(numOfNodes++, NODE_CAPACITY, false);
        int splitPoint = (int) Math.ceil(NODE_CAPACITY / 2);

        for(int i = splitPoint; i < NODE_CAPACITY; i++) {
            newNode.put((i - splitPoint), n.remove(i));
        }

        this.paUpdate(n);

        try {
            long index = pa.allocate();
            ByteBuffer buffer = pa.get(index);
            newNode.serialize(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newNode;
    }

    private BTreeNode<E> getNodeFromPa(long index) {
        BTreeNode<E> node = new BTreeNode<E>();

        try {
            ByteBuffer buffer = pa.get(index);
            node.load(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return node;
    }

    private void paUpdate(BTreeNode<E> n) {
        ByteBuffer buffer = n.allocate();
        n.serialize(buffer);

        try {
            pa.put(n.getIndex(), buffer);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //Functions as a way to update the root index in the PA
    private void writeMetaData() {
        try {
            ByteBuffer metaDataBuffer = pa.getMetadata();
            metaDataBuffer.putLong(root.getIndex());
            pa.persistMetadata();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //TODO
//    public static <E extends Persistable> int getRecordSize(int numOfNode, E value) {
//        BTreeNode<E> example = new BTreeNode<E>(0, numOfNode, true);
//        BinaryTree<E> binaryTree = new BinaryTree<E>();
//        //return example.getSize()
//        return 0;
//    }




    /*--------BTree Node Class---------*/
    private class BTreeNode<E extends Persistable> implements Persistable {
        private BTreePair<E>[] pairs;
        private long index;
        private int isLeaf; //Cannot serialize/load a boolean so using int to represent true/false

        public BTreeNode() {

        }

        public BTreeNode(long index, int width, boolean isLeaf) {
            this.index = index;
            pairs = new BTreePair[width];

            for(BTreePair<E> pair : pairs) {
                pair = new BTreePair<E>();
            }

            this.isLeaf = isLeaf ? 1 : 0;
        }

        public void put(int index, BTreePair<E> p) {
            pairs[index] = p;
		    if(p.hasNextIndex()){
		      isLeaf = 0;
            }
        }

        public BTreePair<E> remove(int index) {
            BTreePair<E> returnPair = pairs[index];
            pairs[index].setKey(0);
            pairs[index].setNextIndex(-1);
            pairs[index].setValue(null);
            return returnPair;
        }

        public BTreePair<E> remove(BTreePair<E> p) {
            int index = -1;
            boolean found = false;

            for(int i = 0; i < pairs.length && !found; i++) {
                if(pairs[i].getKey() == p.getKey()) {
                    index = i;
                    found = true;
                }
            }

            BTreePair<E> pair = null;

            if(index != -1) {
                pair = this.remove(index);
            }

            return pair;
        }

        public int getInsertPoint(long key) {
            int insertPoint;
            int nodeFilledSize = this.getNumOfPairs();
            boolean keepGoing = true;

            for (insertPoint = 0; insertPoint < nodeFilledSize && keepGoing; insertPoint++)
                if (key <= pairs[insertPoint].getKey())
                    keepGoing = false;
            if(!keepGoing)
                --insertPoint;

            return insertPoint;
        }

        public BTreePair<E> getLowestPairGreaterThan(long key) {
            int insertPoint = getInsertPoint(key);
            BTreePair<E> returnPair = pairs[insertPoint];
            if(returnPair == null)
                returnPair = pairs[insertPoint - 1];
            return returnPair;
        }

        public long getHighestKey() {
            long maxKey = 0;

            if(pairs[0].hasNextIndex() || pairs[0].getValue() != null) //pairs is empty if pairs[0] is null
            {
                maxKey = Long.MIN_VALUE;
                for (BTreePair<E> pair : pairs)
                {
                    if(pair.hasNextIndex() || pair.getValue() != null)
                    {
                        long key = pair.getKey();
                        if (key > maxKey)
                            maxKey = key;
                    }
                }
            }

            return maxKey;
        }

        public boolean containsKey(long key) {
            boolean containsKey = false;

            for(int i = 0; i < pairs.length && !containsKey; i++) {
                if(pairs[i].getKey() == key) {
                    containsKey = true;
                }
            }

            return containsKey;
        }

        @Override
        public int getSize() {
            int pairBytes = pairs[0].getSize() * pairs.length;
            return Long.BYTES + Integer.BYTES + pairBytes;
        }

        @Override
        public ByteBuffer allocate(){
            return ByteBuffer.allocate(getSize());
        }

        @Override
        public void serialize(ByteBuffer buffer) {
            buffer.putLong(index);
            buffer.putInt(isLeaf);

            for(BTreePair<E> pair : pairs) {
                pair.serialize(buffer);
            }

            buffer.flip();
        }

        @Override
        public void load(ByteBuffer buffer) {
            index = buffer.getLong();
            isLeaf = buffer.getInt();

            for(BTreePair<E> pair : pairs) {
                pair.load(buffer);
            }
        }

        public BTreePair<E> getPair(int index) {
            BTreePair<E> pair = new BTreePair<E>();

            if(index >= 0 && index < pairs.length) {
                pair = pairs[index];
            }

            return pair;
        }

        public Iterator<BTreePair<E>> getPairs() {
            return Arrays.asList(pairs).iterator();
        }

        public int getNumOfPairs() {
            int size = 0;
            for(BTreePair<E> p : pairs)
                if(p != null)
                    ++size;
            return size;
        }


        public void setPairs(BTreePair<E>[] pairs) {
            this.pairs = pairs;
        }

        public long getIndex() {
            return index;
        }

        public void setIndex(long index) {
            this.index = index;
        }

        public boolean isLeaf() {
            return isLeaf == 1;
        }

        public void setIsLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf ? 1 : 0;
        }

        public boolean isEmpty() {
            boolean isEmpty = true;

            for(int i = 0; i < pairs.length && isEmpty; i++) {
                if(pairs[i] != null && (pairs[i].hasNextIndex() != -1 || pairs[i].getValue() != null)) {
                    isEmpty = false;
                }
            }

            return isEmpty;
        }
    }




    /*---------BTree Node Pair Class --------*/
    private class BTreePair<E extends Persistable> implements Persistable {
        private long key;
        private long nextIndex;
        private E value;

        public BTreePair(){
            this.key = 0;
            this.value = null;
            this.nextIndex = -1;
        }

        public BTreePair(long key, E value, long nextIndex){
            this.key = key;
            this.value = value;
            this.nextIndex = nextIndex;
        }

        public int getSize() {
            int size = Long.BYTES * 2;
            size += genericInstance.getSize();
            return size;
        }

        public ByteBuffer allocate() {
            return ByteBuffer.allocate(this.getSize());
        }

        public void serialize(ByteBuffer buffer) {
            buffer.putLong(key);
            buffer.putLong(nextIndex);

            if(value != null) {
                value.serialize(buffer);
            }
            else {
                byte[] emptyValue = new byte[genericInstance.getSize()];
                buffer.put(emptyValue);
            }

//            buffer.flip();
        }

        public void load(ByteBuffer buffer) {
            this.key = buffer.getLong();
            this.nextIndex = buffer.getLong();

            if(value != null) {
                value.load(buffer);
            }
            else {
                value = null;

                //Moves the position forward manually because we didn't read in a value
                for(int i = 0; i < genericInstance.getSize(); i++) {
                    buffer.get();
                }
            }
        }

        public BTreePair<E> clone() {
            return new BTreePair<E>(key, value, nextIndex);
        }

        public boolean hasNextIndex() {
            return (nextIndex != -1);
        }

        public long getKey() {
            return key;
        }

        public void setKey(long key) {
            this.key = key;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public long getNextIndex() {
            return nextIndex;
        }

        public void setNextIndex(long nextIndex) {
            this.nextIndex = nextIndex;
        }
    }
}
