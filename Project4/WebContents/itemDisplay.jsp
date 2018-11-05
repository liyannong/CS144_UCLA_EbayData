<!-- YOU HAVE TO IMPORT THE CLASS IN JSP FILE-->
<%@ page import="edu.ucla.cs.cs144.SearchResult"%>
<!DOCTYPE html>
<html>
<head>
	<title>Item Info</title>
</head>

<body>
	<h1>HELLO!</h1>
	<% out.println((String)request.getAttribute("xmlData"));%>

	<%
		String buyPrice = (String)request.getAttribute("buy_price");
		out.println(buyPrice);
	 	if (buyPrice!=""&&buyPrice!="0.00"&&buyPrice!="0"&&buyPrice!=null)
	 		 out.println("<a href=\"transaction\">Pay Now!</a>");
	 %>


</body>
</html>