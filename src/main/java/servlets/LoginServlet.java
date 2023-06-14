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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve the foreign date parameter from the request
        String foreignDate = request.getParameter("foreignDate");

        // Construct the Hebcal API URL
        String hebcalAPIUrl = "https://www.hebcal.com/converter?cfg=json&gy="
                + foreignDate.substring(0, 4) + "&gm=" + foreignDate.substring(5, 7) + "&gd="
                + foreignDate.substring(8, 10);

        // Make an HTTP GET request to the Hebcal API
        URL url = new URL(hebcalAPIUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response from the Hebcal API
        Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
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

        // Create a PrintWriter to write the HTML response
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
        int hy = jsonObject.getInt("hy");
        String hm = jsonObject.getString("hm");
        int hd = jsonObject.getInt("hd");
        String hebrew = jsonObject.getString("hebrew");
        String hebDate = hy + " " + hm + " " + hd + ", " + hebrew;
        return hebDate;
    }
}
