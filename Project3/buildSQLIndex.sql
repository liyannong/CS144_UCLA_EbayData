CREATE TABLE IF NOT EXISTS Locations(
ItemID VARCHAR(40) NOT NULL,
Location GEOMETRY NOT NULL,
SPATIAL INDEX(Location)) 
ENGINE=MyISAM;


#Need to run dos2unix 
INSERT INTO Locations (ItemID, Location)
SELECT ItemID, Point(CAST(Seller_Latitude AS DECIMAL(8,2)), CAST(Seller_Longitude AS DECIMAL(8,2))) 
FROM Item
WHERE Seller_Latitude is NOT NULL AND Seller_Longitude is NOT NULL);


