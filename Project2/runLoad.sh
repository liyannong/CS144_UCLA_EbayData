#!/bin/sh

#First, copy from shared to current folder
#cp ./shared/runLoad.sh runLoad.sh

#Second, transfer to unix file because their differnt in end of line
#dos2unix runLoad.sh

#Thirdly, run this file
#sh ./runLoad.sh

chmod u+x runLoad.sh
#You need to run sudo sh pj2.sh

mysql CS144 < drop.sql

#For test purpose
#ant run

ant run-all

mysql CS144 < create.sql


mysql CS144 < load.sql

rm Item.dat 
rm ItemCategory.dat
rm Bid.dat
rm Bidder.dat 
rm Seller.dat 