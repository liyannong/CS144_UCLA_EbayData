package edu.ucla.cs.cs144;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Collections;

//Helper class for item info
@XmlRootElement(name = "Item")
public class Item {
    @XmlAttribute(name = "ItemId")
    public String id;

    @XmlElement(name = "Name")
    public String name;

    @XmlElement(name = "Category")
    public List<String> categories = new ArrayList<>();

//    @XmlElement(name = "Currently")
//    public String currently;

    @XmlElement(name = "Buy_Price")
    public String buyPrice;

    @XmlElement(name = "First_Bid")
    public String firstBId;


    @XmlElementWrapper(name = "Bids")
    @XmlElement(name = "Bid")
    public List<Bid> bids = new ArrayList<>();

    @XmlElement(name = "Location")
    public Location location;

    @XmlElement(name = "Started")
    public String started;

    @XmlElement(name = "Ends")
    public String ends;

    @XmlElement(name = "Seller")
    public Seller seller;

//    @XmlElement(name = "Description")
//    public String description;

    public void sortedBids() {
        Collections.sort(bids, new Comparator<Bid>() {
            @Override
            public int compare(Bid b1, Bid b2) {
                SimpleDateFormat parser = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");

                Date date1 = parser.parse(b1.time);
                Date date2 = parser.parse(b2.time);
                if (date1.compareTo(date2) > 0)
                    return -1;
                else if (date1.compareTo(date2) < 0)
                    return 1;
                else
                    return 0;
            }
        });
    };
}