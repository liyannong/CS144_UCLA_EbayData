package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        //This servlet is used to receive query string from request and
        //search in the database and render result.jsp

        //Get search string and parameters
        String q = request.getParameter("q");
        String numReturn = request.getParameter("numResultsToReturn");
        String numSkip = request.getParameter("numResultsToSkip");
        System.out.println(numReturn + numSkip);
        int returnNum = Integer.parseInt(numReturn);
        int skipNum = Integer.parseInt(numSkip);

        //Search the query
        AuctionSearchClient client = new AuctionSearchClient();
        SearchResult[] sr = client.basicSearch(q, returnNum, skipNum);
        //System.out.println(q + " " + numReturn + " " + numSkip);

        //Send results to jsp
        request.setAttribute("searchResult", sr);
        request.setAttribute("query", q);
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }
}
