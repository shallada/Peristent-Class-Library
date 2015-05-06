package persistent.collections;

import persistent.Persistable;

import java.nio.ByteBuffer;

public class BTree<E extends Persistable> {
	private final int NODE_CAPACITY;
	private long numOfNodes;
	private PersistentArray pa;
	private Node<E> root;

	public BTree(int n, PersistentArray pa) {
		this.NODE_CAPACITY = n+1;
		this.pa = pa;
		root = new Node(0,NODE_CAPACITY, true);
		//ByteBuffer buffer = pa.allocate();
		//root.Serialize(buffer);
		writeMetaData();
	}

	public void add(long key, E value) {
		Pair<E> pair = new Pair<E>(key, value);
		add(pair);
	}

	public void add(Pair p) {
		//Node n = insert(p)
		//If n is not null
			//Create new Node (newRoot)
			//Create Pair with the Key being the highest key in the root node and the value being a reference to root node (kv1)
			//Create Pair with the Key being the higest key in Node n and the value being a reference to Node n (kv2)
			//Add kv1 into newRoot
			//Add kv2 into newRoot
			//Allocate space in pa
			//Put the newRoot in pa
			//writeMetaData()
			//Set root to newRoot
	}

	private Node insert(Pair p) {
		//Create stack of longs (nodeStack)
		//Push the root node's index to the stack
		//create a Node variable and set it to null (splitNode)
		//while nodeStack is not empty
			//peek the nodeStack and get the node at that index from pa (currentNode)
			//if the previous iteration did not result in splitting nodes (if splitNode is not null)
				//Call nodeDown(currentNode, p) and store the result as a variable (nodeDown)
				//For the pair in currentNode that points to nodeDown, change the key to be the highest key in nodeDown. After splitting nodeDown, the previous highest key will no longer be there, so this is necessary
				//Insert new pair into currentNode with key being the highest key in splitNode and value being a reference to splitNode
				//paUpdate(currentNode)
				//set local reference to splitNode to null (splitNode = null)
				//pop from nodeStack
			//else if the pair has not been inserted yet
				//if currentNode is leaf
					//insert pair
					//pop from nodeStack
					//paUpdate(currentNode) 
				//else
					//nodeDown(currentNode, p) (nodeDown)
					//push nodeDown's index onto nodeStack
					//If p's key is greater than currentNode's highest key, set currentNode's highest key to be p's key
			//else
				//pop from nodeStack
			//if currentNode's number of pairs is equal to NODE_CAPACITY
				//split(currentNode) and set splitNode to the new node

		//return splitNode
		return null;
	}

	public void delete(Pair p) {
		//Create stack of longs (nodeStack)
		//Push the root node's index to the stack
		//while nodeStack is not empty
			//peek the nodeStack and get the node at that index from pa (currentNode)
			//if the current node is leaf
				//delete the pair if it exists
				//if currentNode is empty
					//delete node in pa
				//else
					//paUpdate(currentNode)
				//pop from stack
			//else
				//if the pair hasn't been deleted yet 
					//push the next node's index on to the stack
				//else (if p has been removed from a leaf node yet, i.e. we're recursing back up the stack)
					//if the current node is empty
						//delete it from pa
					//else if the pair's (p) key is equal to a key in the current node
						//set the current node's highest key to the next node's highest key 
					//pop from stack
	}

	private Node split(Node n) {
		//Create new node (newNode)
		//Remove the right half of n's pairs and put them in newNode
		//Allocate space in pa for newNode
		//Put newNode in pa
		//Rewrite n's data
		//Return newNode
		return null;
	}

	private Node nodeDown(Node n, Pair p) {
		//for pairs nPair in n
			//if p.key is less than nPair.key
				//return node at nPair.nextIndex
			//else if nPair is last pair in n
				//return node nPair.nextIndex
		return null;
	}

	private void paUpdate(Node n) {
		//ByteBuffer buffer = n.allocate()
		//n.serialize(buffer)
		//put buffer in pa at n's index
	}

	//Functions as a way to update the root index in the PA
	private void writeMetaData() {
		//Get MD ByteBuffer from pa
		//write root node's index to buffer
		//pa.persistMetaData()
	}

	public static <E> int getRecordSize(int numOfNode, E value) {
		//Node<E> example = new Node<E>(0, numOfNodes, true)
		//return example.getSize()
		return 0;
	}


 
