package edu.ucla.cs.cs144;

import javax.xml.bind.annotation.*;

//Helper class for Seller
public class Seller {
    @XmlAttribute(name = "Rating")
    public String rating;

    @XmlAttribute(name = "UserID")
    public String userID;
}