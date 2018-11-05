package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.text.SimpleDateFormat;

import java.sql.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

import java.io.StringWriter;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	private IndexSearcher seracher = null;
	private QueryParser parser = null;

	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn){

		SearchResult[] result = null;
		try {

			System.out.println("performSearch");

			String filePath = "/var/lib/lucene/index1/";
			String searchField = "Content";
			SearchEngine se = new SearchEngine(filePath,searchField);
			String q = query;
			TopDocs topDocs = se.performSearch(q,se.performSearch(q,1).totalHits);

			//System.out.println("Results found: " + topDocs.totalHits);

			ScoreDoc[] hits = topDocs.scoreDocs;
			//System.out.println("hits length: "+hits.length);
			if (numResultsToSkip >= hits.length)
				return new SearchResult[0];
			int size = numResultsToSkip + numResultsToReturn > hits.length ? hits.length-numResultsToSkip : numResultsToReturn;
			result = new SearchResult[size];

			int count = numResultsToSkip;
			for (int i = 0; i < result.length; i++)
			{
				Document doc = se.getDocument(hits[count].doc);
				String ItemID=doc.get("ItemID");
				String Name = doc.get("Name");
				//String Description = doc.get("Description");
				result[i]=new SearchResult(ItemID, Name);
				count++;
			}
			System.out.println("performSearch done");
		}
		catch (Exception e)
		{
			System.out.println("Exception caught.\n");
		}

		return result;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		Connection conn;
		try{
			conn = DbManager.getConnection(true);
			HashSet<String> validItems = new HashSet<>();
			PreparedStatement stmt  =
					conn.prepareStatement("select ItemID from Locations where MBRContains(GeomFromText('Polygon(("
							+region.getLx()+" "+region.getLy()+","
							+region.getRx()+" "+region.getLy()+","
							+region.getRx()+" "+region.getRy()+","
							+region.getLx()+" "+region.getRy()+","
							+region.getLx()+" "+region.getLy()
							+"))'), Location)");

			//Store valid ItemId in the set
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				validItems.add(rs.getString("ItemID"));
			}
			//TEST
			System.out.println("There are: " + validItems.size() + "Valid Items");

			SearchResult[] sr = basicSearch(query, 0, Integer.MAX_VALUE);

			List<SearchResult> list = new ArrayList<>();
			for(int i = 0; i < sr.length; i++){
				if(validItems.contains(sr[i].getItemId())){
					list.add(sr[i]);
				}
			}

			//Check how many results to return
			int len = list.size();
			if(len < numResultsToSkip)
				return new SearchResult[0];
			if(len > numResultsToSkip + numResultsToReturn)
				return list.subList(numResultsToSkip, numResultsToSkip + numResultsToReturn).toArray(new SearchResult[0]);
			return list.subList(numResultsToSkip, len).toArray(new SearchResult[0]);

		} catch (Exception e){
			System.out.println(e);
			return new SearchResult[0];
		}
	}

	public String getXMLDataForItemId(String itemId) {
		//Find the info of a item given a itemId
		//Each element is checked step by step
		Connection conn;
		try {
			conn = DbManager.getConnection(true);
			PreparedStatement item_stmt = conn.prepareStatement("SELECT * FROM Item WHERE ItemId = ?");
			item_stmt.setString(1, itemId);
			ResultSet rs_item = item_stmt.executeQuery();

			if (! rs_item.first())
				return "";

			//TEST
			//return rs_item.getString("ItemId");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.newDocument();


			String ItemId = rs_item.getString("ItemId");
			Element item = doc.createElement("item");
			item.setAttribute("ItemId", ItemId);
			doc.appendChild(item);


			//Name
			String name = rs_item.getString("Name");
			Element nameE = doc.createElement("Name");
			nameE.appendChild(doc.createTextNode(name));
			item.appendChild(nameE);


			//Currently, Buy_Price, First_Bid
			String test = "";
			String[] cols = new String[]{/*"Currently",*/"Buy_Price", "First_Bid"};
			for (String col : cols) {
				String str = rs_item.getString(col);
				//System.out.println(str);
				if (str != null) {
					Element tempE = doc.createElement(col);
					tempE.appendChild(doc.createTextNode("$" + str));
					item.appendChild(tempE);
				}
			}


			//Category
			PreparedStatement cat_stmt = conn.prepareStatement("SELECT * FROM ItemCategory WHERE ItemId = ?");
			cat_stmt.setString(1, itemId);
			ResultSet cat_rs = cat_stmt.executeQuery();
			while (cat_rs.next()) {
				Element catE = doc.createElement("Category");
				catE.appendChild(doc.createTextNode(cat_rs.getString("Category")));
				//System.out.println(cat_rs.getString("Category"));
				item.appendChild(catE);
			}


			//Bid
			PreparedStatement bid_stmt = conn.prepareStatement("select * from Bid where ItemId=?");
			bid_stmt.setString(1, itemId);
			ResultSet bid_rs = bid_stmt.executeQuery();
			Element bids = doc.createElement("Bids");
			//int num = 0;

			while (bid_rs.next()) {
				//num++;
				Element bid = doc.createElement("Bid");
				Element bidder = doc.createElement("Bidder");
				String bidderID = bid_rs.getString("Bidder_UserID");
				bidder.setAttribute("UserID", bidderID);

				String bidTime = bid_rs.getString("BidTime");
				String amount = bid_rs.getString("Amount");
				Element timeE = doc.createElement("Time");
				Element amountE = doc.createElement("Amount");
				timeE.appendChild(doc.createTextNode(bidTime));
				amountE.appendChild(doc.createTextNode(amount));
				bid.appendChild(timeE);
				bid.appendChild(amountE);

				//System.out.println(bidderID);

				PreparedStatement bidder_stmt = conn.prepareCall("select * from Bidder where binary Bidder_UserID=?");
				bidder_stmt.setString(1, bidderID);
				ResultSet bidder_rs = bidder_stmt.executeQuery();

				if (bidder_rs.next()) {
					bidder.setAttribute("Rating", bidder_rs.getString("Bidder_Rating"));
					String location = bidder_rs.getString("Bidder_Location");
					String country = bidder_rs.getString("Bidder_Country");
					//System.out.println(bidder_rs.getString("Bidder_Rating") + " " + location + " " + country);

					if (location != null && location.length() != 0) {
						Element locationE = doc.createElement("Location");
						locationE.appendChild(doc.createTextNode(location));
						bidder.appendChild(locationE);
					}

					if (country != null && country.length() != 0) {
						Element countryE = doc.createElement("Country");
						countryE.appendChild(doc.createTextNode(country));
						bidder.appendChild(countryE);
					}
				}

				bid.appendChild(bidder);
				bids.appendChild(bid);

			}

			item.appendChild(bids);


			//Location
			Element locationEE = doc.createElement("Location");
			locationEE.appendChild(doc.createTextNode(rs_item.getString("Seller_Location")));
			String Latitude = rs_item.getString("Seller_Latitude");
			String Longitude = rs_item.getString("Seller_Longitude");
			//System.out.println(Latitude + " " + Longitude);

			if (Latitude != null && Latitude.length() != 0) {
				locationEE.setAttribute("Latitude", Latitude);
			}
			if (Longitude != null && Longitude.length() != 0) {
				locationEE.setAttribute("Longitude", Longitude);
			}

			item.appendChild(locationEE);


			//Started and Ends
			String[] times = new String[]{"Started", "Ends"};
			for (String time : times) {
				String str = rs_item.getString(time);
				//System.out.println(str);
				Element tempE = doc.createElement(time);
				tempE.appendChild(doc.createTextNode(str));
				item.appendChild(tempE);
			}


			//Seller
			PreparedStatement seller_stmt = conn.prepareCall("SELECT * FROM Seller WHERE Seller_UserID=?");
			String sellerID = rs_item.getString("Seller_UserID");
			System.out.println(sellerID);

			seller_stmt.setString(1, sellerID);
			ResultSet seller_rs = seller_stmt.executeQuery();
			Element sellerE = doc.createElement("Seller");

			if (seller_rs.next()) {
				sellerE.setAttribute("Rating", seller_rs.getString("Seller_Rating"));
				sellerE.setAttribute("UserID", sellerID);
			}

			item.appendChild(sellerE);
			//TEST
			//return ItemId;

			TransformerFactory tfFactory = TransformerFactory.newInstance();
			Transformer transformer = tfFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			StringWriter swriter = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(swriter));


			conn.close();
			return swriter.getBuffer().toString();


		} catch (Exception e) {
			System.out.println(e);
			return "";
		}
	}
	
	public String echo(String message) {
		return message;
	}

}
