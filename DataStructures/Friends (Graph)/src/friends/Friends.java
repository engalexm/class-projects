package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		if(!g.map.containsKey(p1) || !g.map.containsKey(p2)) {   return null;   }
		ArrayList<String> result = new ArrayList<String>();
		if(p1.equals(p2)) {   return null;   }	
		int[] prev = new int[g.members.length];  		for(int b = 0; b < prev.length; b++) {   prev[b] = -1;   }
		Queue<Integer> q = new Queue<Integer>(); 		q.enqueue(g.map.get(p1));
		boolean[] visited = new boolean[g.members.length];	visited[g.map.get(p1)] = true;

		while(!q.isEmpty()) {
			int i = q.dequeue();
			for(Friend ptr = g.members[i].first; ptr != null; ptr = ptr.next) {
				if(!visited[ptr.fnum]) {
					prev[ptr.fnum] = i;
					q.enqueue(ptr.fnum);
					visited[ptr.fnum] = true; 
				}
			}
		}
		
		// Could use a stack here... but why? This works fine.
		result.add(p2);
		for(int back = prev[g.map.get(p2)]; back != g.map.get(p1); back = prev[back]) {
			if(back == -1) {   return null;   }		
			result.add(0, g.members[back].name);
		}
		result.add(0, p1);
		
		return result;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[g.members.length];
		for(int i = 0; i < visited.length; i++) {
			if(!visited[i]) {
				ArrayList<String> clique = new ArrayList<String>();
				dfsSchool(g, school, visited, clique, i);
				if(clique.size() != 0) {   result.add(clique);   }	
			}
		}
		if(result.size() == 0) {   return null;   }
		else {   return result;   }
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		ArrayList<String> result = new ArrayList<String>();
		int[] dfsnum = new int[g.members.length];	
		int[] back = new int[g.members.length];
		Stack<Integer> counter = new Stack<>();
		
		for(int i = 0; i < dfsnum.length; i++) {
			if(dfsnum[i] == 0) {
				Stack<Integer> nums = new Stack<>();
				dfsConnectors(g, dfsnum, back, result, i, i, counter, nums);
			}
		}
		
		if(result.size() == 0) {   return null;   }
		else {   return result;   }
	}

	private static void dfsSchool(Graph g, String school, boolean[] visited, ArrayList<String> clique, int i) {
		visited[i] = true;
		if(g.members[i].school != null && g.members[i].school.equals(school)) {   
			clique.add(g.members[i].name);
			for(Friend ptr = g.members[i].first; ptr != null; ptr = ptr.next) {
				if(!visited[ptr.fnum]) {   dfsSchool(g, school, visited, clique, ptr.fnum);   }
			}
		} 
		return;
	}
	
	private static void dfsConnectors(Graph g, int[] dfsnum, int[] back, ArrayList<String> connectors, int startingPt, int i, Stack<Integer> counter, Stack<Integer> nums) {
		counter.push(i);		nums.push(i);
		dfsnum[i] = counter.size(); 
		back[i] = dfsnum[i];
		//System.out.println("dfs @ " + g.members[i].name + " " + dfsnum[i] + "/" + back[i]);
		for(Friend ptr = g.members[i].first; ptr != null; ptr = ptr.next) {
			if(dfsnum[ptr.fnum] == 0) { 	
				dfsConnectors(g, dfsnum, back, connectors, startingPt, ptr.fnum, counter, nums);
			} else {
				back[i] = Math.min(back[i], dfsnum[ptr.fnum]);
				//System.out.println("nbr " + g.members[ptr.fnum].name + " is already visited => " + g.members[i].name + " " + dfsnum[i] + "/" + back[i]);
			}
		}
		if(nums.peek().equals(i)) {   nums.pop();   }
		if(nums.size() == 0) { return; }
		if(dfsnum[nums.peek()] <= back[i]) {
			//System.out.println("dfsnum(" + g.members[nums.peek()].name + ") <= back(" + g.members[i].name + ")");
			if(nums.peek() != startingPt) {
				if(!connectors.contains(g.members[nums.peek()].name)) {
					connectors.add(g.members[nums.peek()].name);
					//System.out.println("[" + g.members[nums.peek()].name + " is a CONNECTOR]");
				}
			} else {
				//if(noNeighborConnectors(g, dfsnum, connectors, nums.peek())) {
				if(dfsnum[nums.peek()] + 1 < dfsnum[i]) {
					if(!connectors.contains(g.members[nums.peek()].name)) {
						connectors.add(g.members[nums.peek()].name);
						//System.out.println("[" + g.members[nums.peek()].name + " is starting point, and a connector in this case.]");
					}
				} else {
					//System.out.println("[" + g.members[nums.peek()].name + " is starting point, NOT a connector in this case.]");
				}	
			}
		} else {
			back[nums.peek()] = Math.min(back[i], back[nums.peek()]);
			//System.out.println("dfsnum(" + g.members[nums.peek()].name + ") > back(" + g.members[i].name + ") => " + g.members[nums.peek()].name + " " + dfsnum[nums.peek()] + "/" + back[nums.peek()]);
		}	
		return;
	}	
}

