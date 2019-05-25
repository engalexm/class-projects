package friends;

import java.io.*;
import java.util.*;

public class FriendsApp {
	
	public static void main(String[] args) {
		Scanner sc =  new Scanner(System.in);
		System.out.println("===== Enter graph filename.");
		String filename = sc.nextLine();
		try {
			Graph g = new Graph(new Scanner(new File(filename)));
			
			/*System.out.println("\n1. SHORTEST PATH");
			System.out.println("\n===== Enter source name, or q! to quit.");
			String p1 = sc.nextLine();
			if(!p1.equals("q!")) {
				System.out.println("\n===== Enter target name.");
				String p2 = sc.nextLine();
				if(Friends.shortestChain(g, p1, p2) == null) {
					System.out.println("\nNo path found.");
				} else {
					System.out.println("\nShortest path between " + p1 + " and " + p2 + " is:");
					System.out.println(Friends.shortestChain(g, p1, p2));
				}
			}*/
			
			
			/*System.out.println("\n2. CLIQUE FINDER");
			System.out.println("\n===== Enter school name to find cliques, or q! to quit.");
			String school = sc.nextLine();
			if(!school.equals("q!")) {
				if(Friends.cliques(g, school) == null) {
					System.out.println("\nNo cliques found for that school.");
				} else {
					System.out.println("\nAll cliques within " + school + ":");
					System.out.println(Friends.cliques(g, school));
				}
			}*/
			
			
			System.out.println("\n3. CONNECTORS");
			System.out.println("\n===== Finding all connectors in your graph.");
			if(Friends.connectors(g) == null) {
				System.out.println("\nNo connectors found in your graph.");
			} else {
				System.out.println("\nAll connectors:");
				System.out.println(Friends.connectors(g));
			}

		} catch (FileNotFoundException e) {
			System.out.println("\n===== File not found. " + e);
		}
		sc.close();
	}
	
}