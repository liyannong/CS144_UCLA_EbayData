package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Date;

public class ConfirmationServlet extends HttpServlet implements Servlet {
    public ConfirmationServlet() {
    }

    //Show confirmation page for after credit card info is received
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Get the current session
        HttpSession session = request.getSession(true);
        if (session == null) {
            request.getRequestDispatcher("/errorSession.html").forward(request, response);
        } else {
            //Need the casting since getAttribute returns an object
            String itemID = (String) session.getAttribute("itemId");
            String itemName = (String) session.getAttribute("name");
            String buyPrice = (String) session.getAttribute("buy_price");
            if (itemID == null || itemName == null || buyPrice == null) {
                request.getRequestDispatcher("/errorSession.html").forward(request, response);
            } else {
                request.setAttribute("itemId", itemID);
                request.setAttribute("name", itemName);
                request.setAttribute("buy_price", buyPrice);

                String cardNum = request.getParameter("card_num");
                if (cardNum == null || cardNum == "")
                    request.getRequestDispatcher("/CardError.html").forward(request, response);
                else {
                    request.setAttribute("card_num", cardNum);
                    request.getRequestDispatcher("/confirmation.jsp").forward(request, response);
                    request.removeAttribute("card_num");
                }
                request.removeAttribute("itemId");
                request.removeAttribute("name");
                request.removeAttribute("buy_price");

            }

        }
    }
}