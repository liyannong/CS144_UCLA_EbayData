SHOW DATABASES;
USE CS144;

CREATE TABLE Seller(Seller_UserID VARCHAR(40), Seller_Rating VARCHAR(40));


CREATE TABLE Bid(ItemID VARCHAR(40), Bidder_UserID VARCHAR(80), BidTime VARCHAR(40), Amount VARCHAR(20));


CREATE TABLE Bidder(Bidder_UserID VARCHAR(40), Bidder_Rating VARCHAR(40), Bidder_Location VARCHAR(40), Bidder_Country VARCHAR(40));


CREATE TABLE ItemCategory(ItemID VARCHAR(40), Category VARCHAR(80));


CREATE TABLE Item(ItemID VARCHAR(40), Name VARCHAR(80), Currently VARCHAR(80), Buy_Price VARCHAR(80), First_Bid VARCHAR(80), Number_of_Bids VARCHAR(80),
				  Seller_Location VARCHAR(80), Seller_Latitude VARCHAR(80), Seller_Longitude VARCHAR(80), Seller_Country VARCHAR(80), Started VARCHAR(80),
				  Ends VARCHAR(80), Seller_UserID VARCHAR(80), Description VARCHAR(4000));
