/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.Date;

class MyParser {

    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    static final String[] typeName = {
            "none",
            "Element",
            "Attr",
            "Text",
            "CDATA",
            "EntityRef",
            "Entity",
            "ProcInstr",
            "Comment",
            "Document",
            "DocType",
            "DocFragment",
            "Notation",
    };

    static class MyErrorHandler implements ErrorHandler {

        public void warning(SAXParseException exception)
                throws SAXException {
            fatalError(exception);
        }

        public void error(SAXParseException exception)
                throws SAXException {
            fatalError(exception);
        }

        public void fatalError(SAXParseException exception)
                throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                    "in the supplied XML files.");
            System.exit(3);
        }

    }

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector<Element> elements = new Vector<Element>();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName)) {
                elements.add((Element) child);
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }

    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }

    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        } else
            return "";
    }

    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }

    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try {
                am = nf.parse(money).doubleValue();
            } catch (ParseException e) {
                System.out.println("This method should work for all " +
                        "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) throws IOException {
        //Eliminate Duplicates in item table
        Set<String> SeenItem = new HashSet<>();
        //Eliminate Duplicates in seller table
        Set<String> SeenSeller = new HashSet<>();

        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        } catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }

        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);

        /* Fill in code here (you will probably need to write auxiliary
            methods). */

        //Delete all existing file in case keep adding but not creating new files
        File file1 = new File("Item.dat");
        File file2 = new File("ItemCategory.dat");
        File file3 = new File("Bid.dat");
        File file4 = new File("Bidder.dat");
        File file5 = new File("Seller.dat");


        //Get each Item in a list
        //Create a .dat file for each table
        BufferedWriter output1 = new BufferedWriter(new FileWriter("Item.dat", true));
        BufferedWriter output2 = new BufferedWriter(new FileWriter("ItemCategory.dat", true));
        BufferedWriter output3 = new BufferedWriter(new FileWriter("Bid.dat", true));
        BufferedWriter output4 = new BufferedWriter(new FileWriter("Bidder.dat", true));
        BufferedWriter output5 = new BufferedWriter(new FileWriter("Seller.dat", true));

        Element[] Item_List = getElementsByTagNameNR(doc.getDocumentElement(), "Item");
        String colSeparator = "*", nullReplacer = "NULL";

        for (int i = 0; i < Item_List.length; i++) {
            Element Item = Item_List[i];

            //TEST
            //System.out.println(Item.getTagName());

            //////////////////////////////////////////////
            //Construct Table Item(ItemID,Name,Currently,Buy_Price,First_Bid,Number_of_Bids,Seller_Location,
            //Seller_Latitude,Seller_Longitude,Seller_Country,Started,Ends,Seller_UserID,Description)
            //////////////////////////////////////////////

            String ItemID = "", Name = "", Currently = "", Buy_Price = "", First_Bid = "", Number_of_Bids = "";

            ItemID = Item.getAttribute("ItemID");
            if (SeenItem.contains(ItemID))
                continue;
            SeenItem.add(ItemID);

            //System.out.println(ItemID);

            Name = getElementTextByTagNameNR(Item, "Name");
            //System.out.println(Name);

            //Have to handle when Buy_Price doesn't exist, since need to pass it to strip method
            //If not, there will be error
            //The way is not to set it to nullReplacer since the strip method will return "" if not exist
            Buy_Price = getElementTextByTagNameNR(Item, "Buy_Price");
            //System.out.println(Buy_Price);

            First_Bid = getElementTextByTagNameNR(Item, "First_Bid");
            //System.out.println(First_Bid);

            Number_of_Bids = getElementTextByTagNameNR(Item, "Number_of_Bids");
            if (Number_of_Bids.equals(""))
                Number_of_Bids = nullReplacer;
            //System.out.println(Number_of_Bids);


            String Seller_Location = "", Seller_Latitude = "", Seller_Longitude = "", Seller_Country = "";

            Element Location = getElementByTagNameNR(Item, "Location");
            Seller_Location = getElementTextByTagNameNR(Item, "Location");
            Seller_Latitude = Location.getAttribute("Latitude");
            Seller_Longitude = Location.getAttribute("Longitude");
            if (Seller_Latitude.equals(""))
                Seller_Latitude = nullReplacer;
            if (Seller_Longitude.equals(""))
                Seller_Longitude = nullReplacer;
            Seller_Country = getElementTextByTagNameNR(Item, "Country");
            //System.out.println(Seller_Location + Seller_Latitude + Seller_Longitude + Seller_Country);


            String Started = "", Ends = "", Seller_UserID = "", Description = "";

            Started = getElementTextByTagNameNR(Item, "Started");
            Ends = getElementTextByTagNameNR(Item, "Ends");
            //System.out.println(dateTimeParserFormatter(Started)+ " " + dateTimeParserFormatter(Ends));

            Element seller = getElementByTagNameNR(Item, "Seller");
            Seller_UserID = seller.getAttribute("UserID");
            if (Seller_UserID.equals(""))
                Seller_UserID = nullReplacer;
            //System.out.println(Seller_Rating + " " + Seller_UserID);

            Description = getElementTextByTagNameNR(Item, "Description");
            //System.out.println(descriptionTruncater(Description));

            //Write into .dat file
            output1.write(ItemID + colSeparator + Name + colSeparator + strip(Currently) + colSeparator +
                    strip(Buy_Price) + colSeparator + strip(First_Bid) + colSeparator + Number_of_Bids +
                    colSeparator + Seller_Location + colSeparator + Seller_Latitude + colSeparator +
                    Seller_Longitude + colSeparator + Seller_Country + colSeparator + dateTimeParserFormatter(Started) +
                    colSeparator + dateTimeParserFormatter(Ends) + colSeparator + Seller_UserID + colSeparator + Description);
            output1.newLine();
            //////////////////////////////////////////////
            //END OF FIRST PART
            //////////////////////////////////////////////


            //////////////////////////////////////////////
            //Construct Table ItemCategory(ItemID, Category)
            //////////////////////////////////////////////
            Element[] Category_List = getElementsByTagNameNR(Item, "Category");
            String Category = "";

            for (int j = 0; j < Category_List.length; j++) {
                Element Cat = Category_List[j];
                //Can't use getTextContent of Node class here!!
                Category = Cat.getFirstChild().getNodeValue();
                //System.out.println(Category);
                output2.write(ItemID + colSeparator + Category + colSeparator);
                output2.newLine();
            }

            //////////////////////////////////////////////
            //END OF SECOND PART
            //////////////////////////////////////////////


            //////////////////////////////////////////////
            //Construct Table Bid(ItemID,Bidder_UserID,Time,Amount)
            //////////////////////////////////////////////
            //Construct Table Bidder(Bidder_UserID,Bidder_Rating,Bidder_Location,Bidder_Country)
            //////////////////////////////////////////////
            String Bidder_UserID = "", Time = "", Amount = "";
            String Bidder_Rating = "", Bidder_Location = "", Bidder_Country = "";

            Element Bids = getElementByTagNameNR(Item, "Bids");
            Element[] Bid_List = getElementsByTagNameNR(Bids, "Bid");


            for (int k = 0; k < Bid_List.length; k++) {
                Element Bidder = getElementByTagNameNR(Bid_List[k], "Bidder");
                Time = getElementTextByTagNameNR(Bid_List[k], "Time");
                if (Time == "")
                    Time = nullReplacer;
                Amount = getElementTextByTagNameNR(Bid_List[k], "Amount");
                if (Amount == "")
                    Amount = nullReplacer;

                Bidder_UserID = Bidder.getAttribute("UserID");
                if (Bidder_UserID == "")
                    Bidder_UserID = nullReplacer;
                Bidder_Rating = Bidder.getAttribute("Rating");
                if (Bidder_Rating == "")
                    Bidder_Rating = nullReplacer;

                Bidder_Location = getElementTextByTagNameNR(Bidder, "Location");
                if (Bidder_Location == "")
                    Bidder_Location = nullReplacer;
                //else Bidder_Location=columnSeparator+Bidder_Location+columnSeparator;
                Bidder_Country = getElementTextByTagNameNR(Bidder, "Country");
                if (Bidder_Country == "")
                    Bidder_Country = nullReplacer;

                output3.write(ItemID + colSeparator + Bidder_UserID + colSeparator + dateTimeParserFormatter(Time)
                        + colSeparator + strip(Amount) + colSeparator);
                output3.newLine();
                output4.write(Bidder_UserID + colSeparator + Bidder_Rating + colSeparator + Bidder_Location
                        + colSeparator + Bidder_Country + colSeparator);
                output4.newLine();

            }

            //////////////////////////////////////////////
            //END OF THIRD PART
            //////////////////////////////////////////////

            //////////////////////////////////////////////
            //Construct Table Seller(Seller_UserID,Seller_Rating)
            //////////////////////////////////////////////

            String Seller_Rating = "";
            Element Seller = getElementByTagNameNR(Item, "Seller");

            Seller_UserID = Seller.getAttribute("UserID");
            if (SeenSeller.contains(Seller_UserID))
                continue;
            SeenSeller.add(Seller_UserID);
            if (Seller_UserID == "")
                Seller_UserID = nullReplacer;
            Seller_Rating = Seller.getAttribute("Rating");
            if (Seller_Rating == "")
                Seller_Rating = nullReplacer;

            output5.write(Seller_UserID + colSeparator + Seller_Rating + colSeparator);
            output5.newLine();
            //////////////////////////////////////////////
            //END OF FOURTH PART
            //////////////////////////////////////////////


        }

        //Flush and close the file
        output1.flush();
        output1.close();
        output2.flush();
        output2.close();
        output3.flush();
        output3.close();
        output4.flush();
        output4.close();
        output5.flush();
        output5.close();


        /**************************************************************/

    }

    static String dateTimeParserFormatter(String dateTime) {
        if (dateTime.equals("\\N"))
            return "\\N";
        else {
            //TIMESTAMP has a range of '1970-01-01 00:00:01' UTC to '2038-01-19 03:14:07' UTC
            Date myDate = new Date(dateTime);
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String result = format.format(myDate);
                //System.out.println("Successfully parsed!");
                return result;
            } catch (Exception e) {
                System.out.println("Error Formatting!");
                return dateTime;
            }
        }
/*
        //1)Parse the input string to a Date object using formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yy HH:mm:ss", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(dt, formatter);
        //System.out.println(date);

        //2)Format the Date object to SQL timestamp style using formatter1
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS", Locale.ENGLISH);
        //System.out.println(formatter1.format(date));

        return formatter1.format(date);
*/
    }

    static String descriptionTruncater(String description) {
        if (description.length() > 4000)
            return description.substring(0, 4001);
        return description;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }

        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        } catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }

        //For tests
        // processFile(new File("iterms-0.xml"));

        System.out.println(strip("$2354.23"));

        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
    }
}
