package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class LoginServlet extends HttpServlet {

   @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) //the user sends does parameters
            throws ServletException, IOException {
                //what api wants? for example https://www.hebcal.com/converter?cfg=json&gy=2023&gm=06&gd=14
        String foreignDate = request.getParameter("foreignDate");
        String hebcalAPIUrl = "https://www.hebcal.com/converter?cfg=json&gy="
                + foreignDate.substring(0, 4) + "&gm=" + foreignDate.substring(5, 7) + "&gd="
                + foreignDate.substring(8, 10);

        // Make an HTTP request to the Hebcal API
        URL url = new URL(hebcalAPIUrl);
        //the url.openConnection is superclas we want to be speciffific 
        //we adding additional functionality 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // Read the response from the Hebcal API
        Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");  // Set the scanner encoding to UTF-8
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        scanner.close();
        conn.disconnect();

        // Parse the JSON response and extract the Hebrew date
        String hebDate = extractHebrewDateFromResponse(sb.toString());

        // Set the content type and encoding of the response
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Create a PrintWriter to write the HTML response ,we sending request back to user
        PrintWriter out = response.getWriter();

        // Write the HTML response
        out.println("<html><body>");
        out.println("Foreign Date: " + foreignDate + "<br>");
        out.println("Hebrew Date: " + hebDate);
        out.println("</body></html>");
    }

    private String extractHebrewDateFromResponse(String jsonResponse) {
        // Parse the JSON response and extract the Hebrew date
        JSONObject jsonObject = new JSONObject(jsonResponse);
        //String hebDate = jsonObject.getString("hy");
        String hebDate = jsonObject.getString("hebrew");
        return hebDate;
    }
}
