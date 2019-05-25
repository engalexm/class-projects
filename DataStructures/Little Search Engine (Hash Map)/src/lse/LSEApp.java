package lse;

import java.io.*;
import java.util.*;

public class LSEApp {
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("==== Enter document list filename, then return. \n");
		String docsFile = sc.nextLine();
		System.out.println();
		System.out.println("==== Enter noise words filename, then return. \n");
		String noiseFile = sc.nextLine();
		System.out.println();
		
		
		LittleSearchEngine lse = new LittleSearchEngine();
		
		try {
			lse.makeIndex(docsFile, noiseFile);
		} catch(FileNotFoundException e) {		
			System.out.println("==== No such document file. " + e);
		}
		
		System.out.println("Noise words: " + lse.noiseWords + "\n");
		System.out.println("Keywords & Occurrences ArrayList: " + lse.keywordsIndex + "\n");
		
		
		System.out.println("==== Enter two keywords.\n==== Return after each.\n==== Return to finish. \n");
		String kw1 = sc.nextLine();
		String kw2 = sc.nextLine();
		
		sc.close();
		
		System.out.println("==== Searching for: " + kw1 + " OR " + kw2 + "\n");
		
		System.out.println("==== Top 5 documents, in order of frequency:");
		ArrayList<String> top5res = lse.top5search(kw1, kw2);
		for(int i = 0; i < top5res.size(); i++) {
			System.out.println(i+1 + "." + "\t" + top5res.get(i));
		}
		
		System.out.println("==== Complete.");
	}
		
		
		
		
}
