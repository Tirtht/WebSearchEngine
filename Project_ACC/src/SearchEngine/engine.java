package SearchEngine;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import searchtrees.RedBlackBST;
import searchtrees.Sequences;
import textprocessing.BoyerMoore;

public class engine {

	public static final int MAX_DEPTH = 2;
	HashSet<String> visit_link;
	Hashtable<String, String> textOfWebsites;
	Document document;

	BoyerMoore bm;

	// int count=0;
	int offset = 0;
	int keywordCounter = 0;

	String HtmlToText;
	String website_name;

	public engine() {
		visit_link = new HashSet<String>();
		textOfWebsites = new Hashtable<String, String>();

	}

	public void getPagesLinks(String xURL, int xDepth) {

		if ((!visit_link.contains(xURL) && (xDepth < MAX_DEPTH))) {
			try {

				visit_link.add(xURL);
				// System.out.println(xURL);

				document = Jsoup.connect(xURL).get();
				Elements linksOnPage = document.select("a[href]");
				HtmlToText = Jsoup.parse(document.toString()).text();
				textOfWebsites.put(xURL, HtmlToText);
				xDepth++;

				for (Element page : linksOnPage) {
					getPagesLinks(page.attr("abs:href"), xDepth);
				}
			} catch (Exception e) {
				// System.out.println(e.toString());
			}
		}
	}

	public void getKeywordsCount(String xHtmlText, String xFindThis) {
		// System.out.println(xFindThis);
		xHtmlText = xHtmlText.toLowerCase();
		keywordCounter = 0;
		xFindThis = xFindThis.toLowerCase();
		bm = new BoyerMoore(xFindThis);
		offset = 0;

		while (offset != xHtmlText.length()) {
			offset = bm.search(xHtmlText);
			if (offset == xHtmlText.length()) {
				break;
			}
			keywordCounter++;
			xHtmlText = xHtmlText.substring(offset + 1);
		}
	}

	public void getTopResults(String xFindThis, int xResultCount) {
		RedBlackBST<Integer, String> Tree = new RedBlackBST<Integer, String>();
		for (Entry<String, String> entry : textOfWebsites.entrySet()) {
			getKeywordsCount(entry.getValue(), xFindThis);
			Tree.put(keywordCounter, entry.getKey());
		}
		for (int i = 0; i < xResultCount; i++) {
			if (Tree.isEmpty()) {
				break;
			}

			keywordCounter = Tree.max();
			website_name = Tree.get(Tree.max());
			Tree.deleteMax();
			if (keywordCounter == 0)
				break;
				System.out.println("(" + keywordCounter + ")"  + website_name);
		}
	}

	public static void main(String[] args) {
		
		engine searchObj = new engine();
		String URL = "http://en.wikipedia.org/";

		searchObj.getTopResults("English", 5);

		
				
		System.out.println("                                             ");
		System.out.println("             SEARCH ENGINE BOT               ");
		System.out.println("                                             ");
	
		System.out.println("---------------------------------------------");
		printGoogleResult(searchObj, URL);

	}

	public static void printGoogleResult(engine searchObj, String URL) {

		searchObj.getPagesLinks(URL, 0);
		boolean flag = true;
		while (flag) {
			System.out.println("\n Please select the following options:");
			System.out.println("------------------------------------------");
			System.out.println("| 1. Search Keyword			 |");
			System.out.println("------------------------------------------");
			System.out.println("| 2. Exit				 |");
			System.out.println("------------------------------------------");

			Scanner sc = new Scanner(System.in);
			int gSearch = sc.nextInt();

			switch (gSearch) {
			case 1:
				System.out.println("Please enter the Keyword to search:");
				Scanner scan = new Scanner(System.in);
				String keyword = "";
				keyword += scan.nextLine();
				// scan.close();

				String[] words = keyword.split(" ");
				for (String word : words)

				{
					spellchecktest obj1 = new spellchecktest();
					String list1 = obj1.SpellCheck(word);

					if (!list1.contains(word) && !list1.isEmpty()) {

						System.out.println("-------------------------------------");
						System.out.println("Oops ! Did you mean: " + list1 + "?");
						// System.out.println(list1);
						System.out.println("-------------------------------------");
						System.out.println("Our Search Results For " + list1 + " :");
						searchObj.getTopResults(list1, 5);
					} else {
						System.out.println("-------------------------------------");
						System.out.println("Our Search Results For " + word + " :");
						System.out.println("-------------------------------------");
						searchObj.getTopResults(word, 5);
					}
				}
				break;
			case 2:
				System.out.println("Thank you for using our search engine");
				flag = false;
				break;

			default:
				System.out.println("Sorry! You have entered wrong key.");
			}
		}
	}
}
