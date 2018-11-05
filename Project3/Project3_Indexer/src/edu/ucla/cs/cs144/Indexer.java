package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    public Indexer() {
    }

    public void rebuildIndexes() {
        Connection conn = null;
        // Add your code below

        int count_Bid = 0;
        int count_Bidder = 0;
        int count_Item = 0;
        int count_ItemCategory = 0;
        int count_Seller = 0;


        // Below for Item
        String ItemID = "";
        String Name = "";
        String Description = "";

        // Below for ItemCategory
        StringBuilder Category = new StringBuilder("");// More efficient way in Java
        try {
            //Initialize indexWriter according to tutorial
            File file = new File("/var/lib/lucene/index1/");
            //Test
            //System.out.println(file.getAbsolutePath());
            Directory indexDir = FSDirectory.open(file);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(indexDir, config);

            // Below for creating ItemID, Category, Name, Description indexes
            conn = DbManager.getConnection(true);
            Statement s = conn.createStatement();
            ResultSet RS_Item = s.executeQuery("SELECT ItemID,Name,Description FROM Item");

            //The ? will be set later in the while loop
            PreparedStatement PS = conn.prepareStatement("SELECT Category FROM ItemCategory WHERE ItemID=?");


            //Iterate through every tuple of data
            while (RS_Item.next()) {
                //Each doc for each Item
                Document doc = new Document();

                //Record ItemID and Name
                ItemID = RS_Item.getString("ItemID");
                Name = RS_Item.getString("Name");
                Description = RS_Item.getString("Description");
                doc.add(new StringField("ItemID", ItemID, Field.Store.YES));
                doc.add(new TextField("Name", Name, Field.Store.YES));

                if (Description.equals(null)) Description = " ";

                //Initialize category string in each loop
                Category = new StringBuilder("");
                PS.setString(1, ItemID);
                ResultSet RS_ItemCategory = PS.executeQuery();

                //Loop through each category this Item belongs to
                while (RS_ItemCategory.next()) {
                    //Category=Category+RS_ItemCategory.getString("Category")+" ";
                    Category.append(RS_ItemCategory.getString("Category") + " ");
                    count_ItemCategory++;
                }
                RS_ItemCategory.close();
                //System.out.println(ItemID+", "+Name+", "+Category+", "+Description);
                //System.out.println(count_Item+", "+ItemID);


                //doc.add(new TextField("Category", Category.toString(), Field.Store.YES));
                //doc.add(new TextField("Description", Description, Field.Store.YES));
                String Content = ItemID + " " + Name + " " + Category.toString() + " " + Description;
                doc.add(new TextField("Content", Content, Field.Store.NO));
                indexWriter.addDocument(doc);

                count_Item++;
            }
            System.out.println("GET Here!");
            RS_Item.close();// After finishing the above iterations, no need to close

            //System.out.println("Item Sum:"+count_Item);
            //System.out.println("ItemCategory Sum:"+count_ItemCategory);

            PS.close();
            s.close();
            conn.close();
            indexWriter.close();

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while (ex != null) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        }

    }

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }
}
