<!-- YOU HAVE TO IMPORT THE CLASS IN JSP FILE-->
<%@ page import="edu.ucla.cs.cs144.SearchResult"%>
<!DOCTYPE html>
<html>
<head>
	<title>Search Result</title>
</head>

<body>
	<h1>HELLO!</h1>
	
	<% 
	SearchResult[] rs = (SearchResult[])request.getAttribute("searchResult");
	String query = (String)request.getAttribute("query");
	//out.println(rs.length);
	out.println("Your query is: " + query);
	%>
	<%
	    for (int i=0; i<srArr.length;i++){
	    	SearchResult result = rs[i];
	%>
		<h2> <%= result.getItemId() %>   <%= result.getName() %> </h2>
    
    <% } %>

</body>
</html>