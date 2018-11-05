package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.bind.JAXB;
import java.io.StringReader;

public class ItemServlet extends HttpServlet implements Servlet {

    //Handle getItem request and forward to itemDisplay.jsp
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        AuctionSearchClient client = new AuctionSearchClient();
        String xmlData = client.getXMLDataForItemId(request.getParameter("id"));
        StringReader reader = new StringReader(xmlData);
        try {
            Item item = JAXB.unmarshal(reader, Item.class);
            item.sortedBids();

            request.setAttribute("item", item);
            request.setAttribute("xmlData", xmlData);
            request.setAttribute("buy_price", item.buyPrice);
            request.setAttribute("name", item.name);
            request.setAttribute("itemId", item.id);

            request.getRequestDispatcher("itemDisplay.jsp").forward(request, response);
        } catch (Exception e) {
            request.getRequestDispatcher("getItem.html").forward(request, response);
        }
    }
}
