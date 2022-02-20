import com.google.gson.Gson;
import resortsFunctions.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResortsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
        Gson gson = new Gson();

        if (urlPath == null || urlPath.length() == 0) {
            res.setStatus(HttpServletResponse.SC_OK);
            Resorts resorts = new Resorts("string", 0);
            res.getOutputStream().print(gson.toJson(resorts));
            res.getOutputStream().flush();
            return;
        }

        urlPath = urlPath.substring(1);

        System.out.println(urlPath);
        String[] ss = urlPath.split("/");



        if(!Validate.resortInfoSeasonDay(urlPath)) {
            Message message = new Message();
            message.setMessage("string");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getOutputStream().print(gson.toJson(message));
            res.getOutputStream().flush();
            return;
        } else {
            //res.setStatus(HttpServletResponse.SC_OK);
            if(ss.length == 2) {
                // todo check the body parameters
                if (isDigit(ss[0])) {
                    if (foundData(ss)) {
                        res.setStatus(HttpServletResponse.SC_OK);
                        Seasons seasons = new Seasons();
                        seasons.addSeasons("string");
                        res.getOutputStream().print(gson.toJson(seasons));
                        res.getOutputStream().flush();
                    } else {
                        Message message = new Message();
                        message.setMessage("string");
                        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        res.getOutputStream().print(gson.toJson(message));
                        res.getOutputStream().flush();
                    }
                } else {
                    Message message = new Message();
                    message.setMessage("string");
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getOutputStream().print(gson.toJson(message));
                    res.getOutputStream().flush();
                }
            } else {
                // the second get function
                if(validInput(ss)) {
                    if(foundData(ss)) {
                        res.setStatus(HttpServletResponse.SC_OK);
                        SkierInformation skierInformation = new SkierInformation();
                        skierInformation.setTime("Mission Ridge");
                        skierInformation.setNumSkiers(78999);
                        res.getOutputStream().print(gson.toJson(skierInformation));
                        res.getOutputStream().flush();
                    } else {
                        Message message = new Message();
                        message.setMessage("string");
                        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        res.getOutputStream().print(gson.toJson(message));
                        res.getOutputStream().flush();
                    }
                } else {
                    Message message = new Message();
                    message.setMessage("string");
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getOutputStream().print(gson.toJson(message));
                    res.getOutputStream().flush();
                }
            }
        }
    }

    private boolean foundData(String[] ss) {
        return true;
    }

    private boolean validInput(String[] ss) {
        if(isDigit(ss[0]) && isDigit(ss[2]) && isDigit(ss[4])) {
            return true;
        }

        return false;
    }

    private boolean isDigit(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();
        Gson gson = new Gson();
        //System.out.println(urlPath);
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        urlPath = urlPath.substring(1);

        if(!Validate.resortInfoSeasonDayPost(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Message message = new Message();
            message.setMessage("string");
            res.getOutputStream().print(gson.toJson(message));
            res.getOutputStream().flush();
            return;
        } else {
            // todo resort not found function need to be done
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("input correct!");
        }
    }
}

