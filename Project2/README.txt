The purpose of this project is parse XML data files into MySQL through Java.

1. Move all needed files into CS144Share dir, where you can exchange files between host and VM.
   The files needed are:
   MyParser.java: Java program used to parse XML file into .dat file which can be accepted by MySQL. 
   create.sql, drop.sql, load.sql: SQL script file used to create tables, drop tables and bulk load data.
   build.xml: build file for "ant", which compiles and runs Java program.
   runLoad.sh: Shell script which automatically run Java program and import the data into MySQL.

2. In the /home/CS144 dir, transfer windows generated runLoad.sh file to unix version using:
   dos2unix runLoad.sh

3. In the /home/CS144 dir, run the shell script file using:
   sh runLoad.sh

When you get here, all data are parsed from XML file into MySQL data and are stored in five different tables.