
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class find_route {

	static List<List<String>> graphList;
	static List<Node> fringe;
	static HashSet<String> closeSet;
	static HashMap<String, Integer> heuristicList;

	private int nodesGenerated = 0;
	private int nodesExpanded = 0;
	private int maxNodesInMemory = 0;


	public class Node {

		private String nodeName;
		private int gn; // g(n) distance to its parent node
		private int d; // depth or serves as f(n) in Informed Graph Search
		private Node parent; // parent of this node

		public Node(int gn, int d, Node parent, String nodeName) {
			this.nodeName = nodeName;
			this.gn = gn;
			this.d = d;
			this.parent = parent;
		}

		public String getNodeName() {
			return nodeName;
		}

		public int getGn() {
			return gn;
		}

		public int getD() {
			return d;
		}

		public Node getParent() {
			return parent;
		}

		@Override
		public String toString() {
			return "Node [nodeName=" + nodeName + ", gn=" + gn + ", d=" + d + ", parent=" + parent + "]";
		}

	}


	public class NodeComparatorForInformed implements Comparator<Node> {

		@Override
		public int compare(Node o1, Node o2) {

			if (o1.getD() == o2.getD())
				return 0;
			else if (o1.getD() > o2.getD())
				return 1;
			else
				return -1;

		}
	}
	

	public class NodeComparatorForUninformed implements Comparator<Node> {

		@Override
		public int compare(Node o1, Node o2) {

			if (o1.getGn() == o2.getGn())
				return 0;
			else if (o1.getGn() > o2.getGn())
				return 1;
			else
				return -1;

		}
	}


	/**
	 * @param initialState
	 * Method to add initial state to the fringe
	 */
	public void insertFirstNodeToFringe(String initialState) {
		fringe.add(new Node(0, 0, null, initialState));
		maxNodesInMemory++;
	}

	/**
	 * Method to find the successors of a node in Uninformed Graph search
	 * @param n node of which successors is to be found
	 * @return the list of successors
	 */
	public List<Node> successorFunctionForUninformed(Node n) {
		List<Node> result = new ArrayList<Node>();
		for (List<String> x : graphList) {
			if (x.get(0).equals(n.getNodeName())) {
				result.add(new Node(Integer.parseInt(x.get(2)) + n.getGn(), n.getD() + 1, n, x.get(1)));
			} else if (x.get(1).equals(n.getNodeName())) {
				result.add(new Node(Integer.parseInt(x.get(2)) + n.getGn(), n.getD() + 1, n, x.get(0)));
			}
		}
		nodesGenerated += result.size();
		return result;
	}

	/**
	 * Method to find the successors of a node in Informed Graph search
	 * @param n node of which successors is to be found
	 * @return the list of successors
	 */
	public List<Node> successorFunctionForInformed(Node n) {
		List<Node> result = new ArrayList<Node>();
		for (List<String> x : graphList) {
			if (x.get(0).equals(n.getNodeName())) {
				result.add(new Node(Integer.parseInt(x.get(2)) + n.getGn(),
						Integer.parseInt(x.get(2)) + n.getGn() + heuristicList.get(x.get(1)), n, x.get(1)));
			} else if (x.get(1).equals(n.getNodeName())) {
				result.add(new Node(Integer.parseInt(x.get(2)) + n.getGn(),
						Integer.parseInt(x.get(2)) + n.getGn() + heuristicList.get(x.get(0)), n, x.get(0)));
			}
		}
		nodesGenerated += result.size();
		return result;
	}

	
	/**
	 * Method to expand a node
	 * @param searchId to check if it is informed or uninformed search
	 * @param n node to be expanded
	 * @return the successor nodes after expanding
	 */
	public List<Node> expandFunction(int searchId, Node n) {
		if (searchId == 3) {
			return successorFunctionForUninformed(n);
		} else {
			return successorFunctionForInformed(n);
		}

	}

	/**
	 * Method to find the route between starting and goal node
	 * @param startingState 
	 * @param goalState
	 * @param searchID to check whether the search is informed or uninformed.
	 * @return the goal node if found, else returns null
	 */
	public Node graphSearch(String startingState, String goalState, int searchID) {

		insertFirstNodeToFringe(startingState);
		while (!fringe.isEmpty()) {
			Node n = new Node(fringe.get(0).getGn(), fringe.get(0).getD(), fringe.get(0).getParent(),
					fringe.get(0).getNodeName());
			fringe.remove(0);
			if (searchID == 3) {
				Collections.sort(fringe, new NodeComparatorForUninformed());
			} else {
				Collections.sort(fringe, new NodeComparatorForInformed());
			}
			nodesExpanded++;
			if (n.getNodeName().equals(goalState)) {
				return n;
			}
			if (!closeSet.contains(n.getNodeName())) {
				closeSet.add(n.getNodeName());
				for (Node x : expandFunction(searchID, n)) {
					fringe.add(x);
					if (searchID == 3) {
						Collections.sort(fringe, new NodeComparatorForUninformed());
					} else {
						Collections.sort(fringe, new NodeComparatorForInformed());
					}
					if (maxNodesInMemory < fringe.size()) {
						maxNodesInMemory = fringe.size();
					}
				}

			}
		}
		return null;
	}

	public static void main(String[] args) {

		graphList = new ArrayList<List<String>>();
		fringe = new ArrayList<Node>();
		closeSet = new HashSet<String>();
		heuristicList = new HashMap<String, Integer>();
		find_route find_route = new find_route();
		FileReader fr;
		try {
			fr = new FileReader(args[0]);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (!line.equals("END OF INPUT")) {
				String[] words = line.split(" ");
				List<String> entry = new ArrayList<String>();
				for (String x : words) {
					entry.add(x);
				}
				graphList.add(entry);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		} catch (IOException e) {
			System.out.println("Could not read file");
		}
		if (args.length == 3) {
			Node n = find_route.graphSearch(args[1], args[2], 3);
			System.out.println("nodes Expanded: " + find_route.nodesExpanded + "\nnodes generated: "
					+ find_route.nodesGenerated + "\nmax nodes in memory: " + find_route.maxNodesInMemory);
			if (n != null) {
				System.out.println("distance: " + n.getGn() + ".0 km");
				System.out.println("route: ");
				String result = "";
				Node temp = n.getParent();
				while (temp != null) {
					result = temp.getNodeName() + " to " + n.getNodeName() + ", " + (n.getGn() - temp.getGn())
							+ ".0 km\n" + result;
					n = temp;
					temp = temp.getParent();
				}
				System.out.println(result);
			} else {
				System.out.println("distance: infinity");
				System.out.println("route: \nnone");
			}

		} else if (args.length == 4) {

			try {
				fr = new FileReader(args[3]);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (!line.equals("END OF INPUT")) {
					String[] words = line.split(" ");
					heuristicList.put(words[0], Integer.parseInt(words[1]));
					line = br.readLine();
				}
				br.close();
			} catch (FileNotFoundException e) {
				System.out.println("File not found.");
			} catch (IOException e) {
				System.out.println("Could not read file");
			}

			Node n = find_route.graphSearch(args[1], args[2], 4);
			System.out.println("nodes Expanded: " + find_route.nodesExpanded + "\nnodes generated: "
					+ find_route.nodesGenerated + "\nmax nodes in memory: " + find_route.maxNodesInMemory);
			if (n != null) {
				System.out.println("distance: " + n.getGn() + ".0 km");
				System.out.println("route: ");
				String result = "";
				Node temp = n.getParent();
				while (temp != null) {
					result = temp.getNodeName() + " to " + n.getNodeName() + ", " + (n.getGn() - temp.getGn())
							+ ".0 km\n" + result;
					n = temp;
					temp = temp.getParent();
				}
				System.out.println(result);
			} else {
				System.out.println("distance: infinity");
				System.out.println("route: \nnone");
			}
		} else {
			System.out.println("Invalid number of arguments");
		}

	}
}
