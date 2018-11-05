package edu.ucla.cs.cs144;

import javax.xml.bind.annotation.*;

//Helper class for location
public class Location {
    @XmlValue
    public String location;

    @XmlAttribute(name = "Latitude")
    public String latitude;

    @XmlAttribute(name = "Longitude")
    public String longitude;
}