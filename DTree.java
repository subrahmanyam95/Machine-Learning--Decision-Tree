//Program by Subrahmanyam Oruganti

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class DTree {
	private static int count = 0;

	private static double calculateLog(double fraction) {
		return Math.log10(fraction) / Math.log10(2);
	}

	private static node findAttributeAndConstructNode(node root, int[][] values, int[] isDone, int features,
			int[] indexList,int h) {
		int i = 0;
		@SuppressWarnings("unused")
		int j = 0;
		int k = 0;
		double maxheuristic = 0;
		int maxLeftIndex[] = null;
		int maxRightIndex[] = null;
		int maxIndex = -1;
		for (; i < features; i++) {
			if (isDone[i] == 0) {
				double negatives = 0;
				double positives = 0;
				double left = 0;
				double right = 0;
				double leftEntrophy = 0,leftVI=0,rightVI=0;
				double rightEntrophy = 0;
				int[] leftIndex = new int[values.length];
				int[] rightIndex = new int[values.length];
				double entrophy = 0,varianceImpurity=0;
				double rightPositives = 0;
				double heuristic = 0;
				double rightNegatives = 0, leftPositives = 0, leftNegatives = 0;
				for (k = 0; k < indexList.length; k++) {
					if (values[indexList[k]][features] == 1) {
						positives++;
					} else {
						negatives++;
					}
					if (values[indexList[k]][i] == 1) {
						rightIndex[(int) right++] = indexList[k];
						if (values[indexList[k]][features] == 1) {
							rightPositives++;
						} else {
							rightNegatives++;
						}

					} else {
						leftIndex[(int) left++] = indexList[k];
						if (values[indexList[k]][features] == 1) {
							leftPositives++;
						} else {
							leftNegatives++;
						}

					}

				}
				if(h==1)
				{
				entrophy = (-1 * calculateLog(positives / indexList.length) * ((positives / indexList.length)))
						+ (-1 * calculateLog(negatives / indexList.length) * (negatives / indexList.length));
				leftEntrophy = (-1 * calculateLog(leftPositives / (leftPositives + leftNegatives))
						* (leftPositives / (leftPositives + leftNegatives)))
						+ (-1 * calculateLog(leftNegatives / (leftPositives + leftNegatives))
								* (leftNegatives / (leftPositives + leftNegatives)));
				rightEntrophy = (-1 * calculateLog(rightPositives / (rightPositives + rightNegatives))
						* (rightPositives / (rightPositives + rightNegatives)))
						+ (-1 * calculateLog(rightNegatives / (rightPositives + rightNegatives))
								* (rightNegatives / (rightPositives + rightNegatives)));
				if (Double.compare(Double.NaN, entrophy) == 0) {
					entrophy = 0;
				}
				if (Double.compare(Double.NaN, leftEntrophy) == 0) {
					leftEntrophy = 0;
				}
				if (Double.compare(Double.NaN, rightEntrophy) == 0) {
					rightEntrophy = 0;
				}
				heuristic = entrophy
						- ((left / (left + right) * leftEntrophy) + (right / (left + right) * rightEntrophy));
				}
				else if(h==0)
				{
					varianceImpurity = (positives / indexList.length)*(negatives / indexList.length);
					leftVI = (leftPositives / (leftPositives + leftNegatives))*(leftNegatives / (leftPositives + leftNegatives));
					rightVI = (rightPositives / (rightPositives + rightNegatives))*(rightNegatives / (rightPositives + rightNegatives));
					if (Double.compare(Double.NaN, varianceImpurity) == 0) {
						varianceImpurity = 0;
					}
					if (Double.compare(Double.NaN, leftVI) == 0) {
						leftVI = 0;
					}
					if (Double.compare(Double.NaN, rightVI) == 0) {
						rightVI = 0;
					}
					heuristic = varianceImpurity - ((left / (left + right) * leftVI) + (right / (left + right) * rightVI));
				}
				
				if (heuristic >= maxheuristic) {
					maxheuristic = heuristic;
					maxIndex = i;
					int leftTempArray[] = new int[(int) left];
					for (int index = 0; index < left; index++) {
						leftTempArray[index] = leftIndex[index];
					}
					int rightTempArray[] = new int[(int) right];
					for (int index = 0; index < right; index++) {
						rightTempArray[index] = rightIndex[index];
					}
					maxLeftIndex = leftTempArray;
					maxRightIndex = rightTempArray;

				}
			}
		}
		root.targetAttribute = maxIndex;
		root.leftIndices = maxLeftIndex;
		root.rightIndices = maxRightIndex;
		return root;
	}

	
	public static boolean isAllArePositive(int[] indexList, int[][] values, int features) {
		boolean oneOnly = true;
		for (int i : indexList) {
			if (values[i][features] == 0)
				oneOnly = false;
		}
		return oneOnly;

	}

	
	public static boolean isAllAreNegative(int[] indexList, int[][] values, int features) {
		boolean zeroOnly = true;
		for (int i : indexList) {
			if (values[i][features] == 1)
				zeroOnly = false;
		}
		return zeroOnly;

	}

	
	public static int findMaxValue(node root, int[][] values, int features) {
		int noOfOnes = 0;
		int noOfZeroes = 0;
		if (root.parent == null) {
			int i = 0;
			for (i = 0; i < values.length; i++) {
				if (values[i][features] == 1) {
					noOfOnes++;
				} else {
					noOfZeroes++;
				}
			}
		} else {
			for (int i : root.parent.leftIndices) {
				if (values[i][features] == 1) {
					noOfOnes++;
				} else {
					noOfZeroes++;
				}
			}

			for (int i : root.parent.rightIndices) {
				if (values[i][features] == 1) {
					noOfOnes++;
				} else {
					noOfZeroes++;
				}
			}
		}
		return noOfZeroes > noOfOnes ? 0 : 1;

	}

	
	public static boolean allAttributesProcessed(int[] isDone) {
		boolean allDone = true;
		for (int i : isDone) {
			if (i == 0)
				allDone = false;
		}
		return allDone;
	}

	
	public static node constructDecisionTree(node root, int[][] values, int[] isDone, int features, int[] indexList,
			node parent,int h) {
		if (root == null) {
			root = new node();
			if (indexList == null || indexList.length == 0) {
				root.label = findMaxValue(root, values, features);
				root.isLeaf = true;
				return root;
			}
			if (isAllArePositive(indexList, values, features)) {
				root.label = 1;
				root.isLeaf = true;
				return root;
			}
			if (isAllAreNegative(indexList, values, features)) {
				root.label = 0;
				root.isLeaf = true;
				return root;
			}
			if (features == 1 || allAttributesProcessed(isDone)) {
				root.label = findMaxValue(root, values, features);
				root.isLeaf = true;
				return root;
			}
		}
		root = findAttributeAndConstructNode(root, values, isDone, features, indexList,h);
		root.parent = parent;
		if (root.targetAttribute != -1)
			isDone[root.targetAttribute] = 1;
		int leftIsDone[] = new int[isDone.length];
		int rightIsDone[] = new int[isDone.length];
		for (int j = 0; j < isDone.length; j++) {
			leftIsDone[j] = isDone[j];
			rightIsDone[j] = isDone[j];

		}

		root.left = constructDecisionTree(root.left, values, leftIsDone, features, root.leftIndices, root,h);
		root.right = constructDecisionTree(root.right, values, rightIsDone, features, root.rightIndices, root,h);
		return root;
	}

	public static void printTree(node tree) {
		if (tree != null) {
			System.out.println("tree.targetAttribute " + tree.targetAttribute);
			System.out.println("tree.label " + tree.label);
			System.out.println("tree.isLeaf " + tree.isLeaf);
			if (tree.leftIndices != null) {
				System.out.println("tree.leftIndices ");
				for (int i : tree.leftIndices) {
					System.out.print(i + " ");
				}
			}
			if (tree.rightIndices != null) {
				System.out.println("\ntree.rightIndices ");
				for (int i : tree.rightIndices) {
					System.out.print(i + " ");
				}
			}
			System.out.println();
			printTree(tree.left);
			printTree(tree.right);
		}
	}

	
	public static node createCopy(node root) {
		if (root == null)
			return root;

		node temp = new node();
		temp.label = root.label;
		temp.isLeaf = root.isLeaf;
		temp.leftIndices = root.leftIndices;
		temp.rightIndices = root.rightIndices;
		temp.targetAttribute = root.targetAttribute;
		temp.parent = root.parent;
		temp.left = createCopy(root.left); // cloning left child
		temp.right = createCopy(root.right); // cloning right child
		return temp;
	}

	
	public static node postPruneAlgorithm(String pathName, int L, int K, node root, int[][] values, int features) {
		node postPrunedTree = new node();
		int i = 0;
		postPrunedTree = root;
		double maxAccuracy = measureAccuracyOverValidationSet(pathName, root);
		System.out.println("Accuracy of orginal tree is " + maxAccuracy);
		for (i = 0; i < L; i++) {
			node newRoot = createCopy(root);
			Random randomNumbers = new Random();
			int M = 1 + randomNumbers.nextInt(K);
			for (int j = 1; j <= M; j++) {
                count =0;
				int noOfNonLeafNodes = findNumberOfNonLeafNodes(newRoot);
				if (noOfNonLeafNodes == 0)
					break;
				node nodeArray[] = new node[noOfNonLeafNodes];
				buildArray(newRoot, nodeArray);
				int P = randomNumbers.nextInt(noOfNonLeafNodes);
				nodeArray[P] = createLeafNodeWithMajorityEle(nodeArray[P], values, features);
				//System.out.println("number of non-leaf: "+noOfNonLeafNodes);
			}
			double accuracy = measureAccuracyOverValidationSet(pathName, newRoot);

			if (accuracy > maxAccuracy) {
				postPrunedTree = newRoot;
				maxAccuracy = accuracy;
			}
			//System.out.println("number of non-leaf: "+noOfNonLeafNodes);
			//System.out.println("accuracy at each stage of pruning: "+ accuracy);
		}
		System.out.println("the Accurancy of pruned tree is  " + maxAccuracy);
		return postPrunedTree;
	}

	
	private static double measureAccuracyOverValidationSet(String pathName, node newRoot) {
		int[][] validationSet = constructValidationSet(pathName);
		double count = 0;
		for (int i = 1; i < validationSet.length; i++) {
			count += isCorrectlyClassified(validationSet[i], newRoot);
		}
		return count / validationSet.length;
	}

	
	private static int isCorrectlyClassified(int[] setValues, node newRoot) {
		int index = newRoot.targetAttribute;
		int correctlyClassified = 0;
		node testingNode = newRoot;
		while (testingNode.label == -1) {
			if (setValues[index] == 1) {
				testingNode = testingNode.right;
			} else {
				testingNode = testingNode.left;
			}
			if (testingNode.label == 1 || testingNode.label == 0) {
				if (setValues[setValues.length - 1] == testingNode.label) {
					correctlyClassified = 1;
					break;
				} else {
					break;
				}
			}
			index = testingNode.targetAttribute;
		}
		return correctlyClassified;
	}

	
	private static int[][] constructValidationSet(String pathName) {
		int[] featuresAndLength = findFeaturesAndLength(pathName);
		String csvFile = pathName;
		int[][] validationSet = new int[featuresAndLength[1]][featuresAndLength[0]];
		BufferedReader bufferedReader = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			bufferedReader = new BufferedReader(new FileReader(csvFile));
			int i = 0;
			int count = 0;
			while ((line = bufferedReader.readLine()) != null) {
				String[] lineParameters = line.split(cvsSplitBy);
				int j = 0;
				if (count == 0) {
					count++;
					continue;
				} else {
					for (String lineParameter : lineParameters) {
						validationSet[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return validationSet;
	}

	
	private static int findMaxValueAtGivenNode(node root, int[][] values, int features) {
		int noOfOnes = 0;
		int noOfZeroes = 0;
		if (root.leftIndices != null) {
			for (int i : root.leftIndices) {
				if (values[i][features] == 1) {
					noOfOnes++;
				} else {
					noOfZeroes++;
				}
			}
		}

		if (root.rightIndices != null) {
			for (int i : root.rightIndices) {
				if (values[i][features] == 1) {
					noOfOnes++;
				} else {
					noOfZeroes++;
				}
			}
		}
		return noOfZeroes > noOfOnes ? 0 : 1;
	}

	
	private static node createLeafNodeWithMajorityEle(node node, int[][] values, int features) {
		node.isLeaf = true;
		node.label = findMaxValueAtGivenNode(node, values, features);
		node.left = null;
		node.right = null;
		return node;
	}

	
	private static void buildArray(node root, node[] nodeArray) {
		if (root == null || root.isLeaf) {
			return;
		}
		nodeArray[count++] = root;
		if (root.left != null) {
			buildArray(root.left, nodeArray);
		}
		if (root.right != null) {
			buildArray(root.right, nodeArray);
		}
	}

	
	private static int findNumberOfNonLeafNodes(node root) {
		if (root == null || root.isLeaf)
			return 0;
		else
			return (1 + findNumberOfNonLeafNodes(root.left) + findNumberOfNonLeafNodes(root.right));
	}

	public static void main(String[] args) {
		if (args.length != 6) {
			System.out.println("Not enough command line arguments Existing..");
			return;
		}

		int L = Integer.parseInt(args[0]);
		int K = Integer.parseInt(args[1]);
		int[] featuresAndLength = findFeaturesAndLength(args[2]);
		int[][] values = new int[featuresAndLength[1]][featuresAndLength[0]];
		String[] featureNames = new String[featuresAndLength[0]];
		//int[] isDone = new int[featuresAndLength[0]];
		//int[] indexList = new int[values.length];
		int h;
		//loadActualValues(args[2], values, featureNames, isDone, indexList, featuresAndLength[0]);
		node root[] = new node[2];
		node pruneTree[] = new node[2];
		for(h=1;h>=0;h--)
		{
			int[] isDone = new int[featuresAndLength[0]];
			int[] indexList = new int[values.length];
			loadActualValues(args[2], values, featureNames, isDone, indexList, featuresAndLength[0]);
			if(h==0)
				System.out.println("Decision tree using Variance Impurity heuristic:");
			else
				System.out.println("Decision tree using Information Gain heuristic:");
			root[h] = constructDecisionTree(null, values, isDone, featuresAndLength[0] - 1, indexList, null,h);
			pruneTree[h] = postPruneAlgorithm(args[3], L, K, root[h], values, featuresAndLength[0] - 1);
			System.out.println("The Accuracy over Tesing data for decision Tree " + calculateAccuracyOverTestData(args[4], root[h]));
			System.out.println("The Accuracy over Tesing data for Pruned Tree " + calculateAccuracyOverTestData(args[4], pruneTree[h]));
			if (args[5].equalsIgnoreCase("yes"))
			{
				System.out.println("BEFORE PRUNING:");
				printTree(root[h], 0, featureNames);
				System.out.println("AFTER PRUNING:");
				printTree(pruneTree[h], 0, featureNames);
			}
		}
		//for(int q=0;q<featuresAndLength[0];q++)
		//System.out.println("isdone: "+isDone[q]);
	}

	private static int[] findFeaturesAndLength(String csvFile) {
		BufferedReader bufferedReader = null;
		String line = "";
		String cvsSplitBy = ",";
		@SuppressWarnings("unused")
		int features = 0;
		int count = 0;
		int[] featuresAndLength = new int[2];
		try {

			bufferedReader = new BufferedReader(new FileReader(csvFile));
			while ((line = bufferedReader.readLine()) != null) {
				if (count == 0) {
					String[] country = line.split(cvsSplitBy);
					featuresAndLength[0] = country.length;
				}
				count++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		featuresAndLength[1] = count;
		return featuresAndLength;
	}

	
	private static void loadActualValues(String pathName, int[][] values, String[] featureNames, int[] isDone,
			int[] indexList, int features) {
		String csvFile = pathName;
		BufferedReader bufferedReader = null;
		String line = "";
		String cvsSplitBy = ",";
		for (int k = 0; k < features; k++) {
			isDone[k] = 0;
		}
		int k = 0;
		for (k = 0; k < values.length; k++) {
			indexList[k] = k;
		}
		try {

			bufferedReader = new BufferedReader(new FileReader(csvFile));
			int i = 0;
			while ((line = bufferedReader.readLine()) != null) {
				String[] lineParameters = line.split(cvsSplitBy);
				int j = 0;
				if (i == 0) {
					for (String lineParameter : lineParameters) {
						featureNames[j++] = lineParameter;
					}
				}

				else {

					for (String lineParameter : lineParameters) {
						values[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private static void printTree(node root, int printLines, String[] featureNames) {
		int printLinesForThisLoop = printLines;
		if (root.isLeaf) {
			System.out.println(" " + root.label);
			return;
		}
		for (int i = 0; i < printLinesForThisLoop; i++) {
			System.out.print("| ");
		}
		if (root.left != null && root.left.isLeaf && root.targetAttribute !=-1)
			System.out.print(featureNames[root.targetAttribute] + "= 0 :");
		else
			if(root.targetAttribute !=-1)
			System.out.println(featureNames[root.targetAttribute] + "= 0 :");

		printLines++;
		printTree(root.left, printLines, featureNames);
		for (int i = 0; i < printLinesForThisLoop; i++) {
			System.out.print("| ");
		}
		if (root.right != null && root.right.isLeaf&& root.targetAttribute !=-1)
			System.out.print(featureNames[root.targetAttribute] + "= 1 :");
		else
			if(root.targetAttribute !=-1)
			System.out.println(featureNames[root.targetAttribute] + "= 1 :");
		printTree(root.right, printLines, featureNames);
	}

	
	private static double calculateAccuracyOverTestData(String pathName, node root) {
		double accuracy = 0;
		int[][] testingData = loadTestingData(pathName);
		for (int i = 0; i < testingData.length; i++) {
			accuracy += isCorrectlyClassified(testingData[i], root);
		}
		return accuracy / testingData.length;

	}

	
	private static int[][] loadTestingData(String pathName) {
		int[] featuresAndLength = findFeaturesAndLength(pathName);
		String csvFile = pathName;
		int[][] validationSet = new int[featuresAndLength[1]][featuresAndLength[0]];
		BufferedReader bufferReader = null;
		String line = "";
		String cvsSplitBy = ",";
		try {

			bufferReader = new BufferedReader(new FileReader(csvFile));
			int i = 0;
			int count = 0;
			while ((line = bufferReader.readLine()) != null) {
				String[] lineParameters = line.split(cvsSplitBy);
				int j = 0;
				if (count == 0) {
					count++;
					continue;
				}

				else {

					for (String lineParameter : lineParameters) {
						validationSet[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return validationSet;
	}
}


// Subrahmanyam Oruganti