//Node implementation

class node {
	int label = -1;
	boolean isLeaf = false;
	int targetAttribute = -1;
	node parent;
	node left;
	node right;
	int leftIndices[];
	int rightIndices[];
}

//Subrahmanyam Oruganti