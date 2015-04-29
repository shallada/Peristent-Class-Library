package persistent.collections;

public class BTree<E> {
	private final int NODE_CAPACITY;
	private long numOfNodes;
	private PersistentArray pa;
	private Node root;

	public BTree(int n, PersistentArray pa) {
		//Set NODE_CAPACTIY to n + 1, the extra spot acts as a buffer
		//Set pa
		//Create and set root node
		//Allocate space in pa
		//Write root node to pa in allocated space
		//Set root index in pa
	}

	public void add(long key, E value) {
		//Call add with new KV-pair object
	}

	public void add(Pair p) {
		//Node n = insert(p)
		//If n is not null
			//Create new Node (newRoot)
			//Create KV-pair with the Key being the highest key in the root node and the value being a reference to root node (kv1)
			//Create KV-pair with the Key being the higest key in Node n and the value being a reference to Node n (kv2)
			//Add kv1 into newRoot
			//Add kv2 into newRoot
			//Allocate space in pa
			//Put the newRoot in pa
			//update the the pointer to the root in pa
			//Set root index to newRoot
	}

	private Node insert(Pair p) {
		//Create stack of longs (nodeStack)
		//Push the root node's index to the stack
		//create a Node variable and set it to null (splitNode)
		//while nodeStack is not empty
			//peek the nodeStack and get the node at that index from pa (currentNode)
			//if the previous iteration did not result in splitting nodes (if splitNode is not null)
				//Get the node one level down (nodeDown)
				//Reassign keys for pairs in currentNode
				//Insert new pair into currentNode with key being the highest key in splitNode and value being a reference to splitNode
				//Put currentNode in pa at currentNode's index
				//set local reference to splitNode to null (splitNode is null)
				//pop from nodeStack
			//else if the pair has not been inserted yet
				//if currentNode is leaf
					//insert pair
					//pop from nodeStack
					//Put currentNode in pa at currentNode's index
				//else
					//Get the node one level down (nodeDown)
					//push nodeDown's index onto nodeStack
					//If p's key is greater than currentNode's highest key, set currentNode's highest key to be p's key
			//else
				//pop from nodeStack
			//if currentNode's number of pairs is equal to NODE_CAPACITY
				//split the node and set splitNode to the new node

		//return splitNode
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
					//update node in pa
				//pop from stack
			//else
				//if the pair hasn't been deleted yet (if p has not been removed from a leaf node yet, i.e. we're recursing back up the stack)
					//push the next node's index on to the stack
				//else
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
	}
 }
