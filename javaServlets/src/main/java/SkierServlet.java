import com.google.gson.Gson;
import skierFunctions.Message;
import skierFunctions.Resorts;
import skierFunctions.Validate;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

public class SkierServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
        Gson gson = new Gson();
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        urlPath = urlPath.substring(1);

        System.out.println(urlPath);
        String[] ss = urlPath.split("/");

        //res.setStatus(HttpServletResponse.SC_OK);
        if(ss.length == 2) {
            // todo check the body parameters
            if(ss[1].equals("vertical")) {
                if(foundData(ss)) {
                    res.setStatus(HttpServletResponse.SC_OK);
                    Resorts resorts = new Resorts();
                    resorts.setSeasonID("string");
                    resorts.setTotalVert(0);
                    res.getOutputStream().print(gson.toJson(resorts));
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
            if(validInput(ss)) {
                if(foundData(ss)) {
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.getWriter().write(34507);
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

    private boolean foundData(String[] ss) {
        return true;
    }

    private boolean validInput(String[] ss) {
        if(isDigit(ss[0]) && isDigit(ss[2]) && isDigit(ss[4]) && isDigit(ss[6])) {
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
        //System.out.println(urlPath);
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        urlPath = urlPath.substring(1);

        if(!Validate.skierInfoSeasonDay(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("not proper paramterers");
            return;
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("input correct!");
        }
    }
}

