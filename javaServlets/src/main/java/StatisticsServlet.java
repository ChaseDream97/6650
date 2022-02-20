import com.google.gson.Gson;
import statisticsFunctions.StatisticsInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        Gson gson = new Gson();

        res.setStatus(HttpServletResponse.SC_OK);
        StatisticsInfo statisticsInfo = new StatisticsInfo("/resorts", "GET", 11, 198);
        res.getOutputStream().print(gson.toJson(statisticsInfo));
        res.getOutputStream().flush();



    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // todo no post function at this moment
    }
}

