import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.ObjectPool;
import resortsFunctions.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResortsServlet extends HttpServlet {

    private final static String QUEUE_NAME = "ResortsServletQueue";

    private Gson gson = new Gson();
    private ObjectPool<Channel> pool;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        //System.out.println(urlPath);
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        urlPath = urlPath.substring(1);
        String[] ss = urlPath.split("/");

        if(!Validate.resortInfoSeasonDayPost(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Message message = new Message();
            message.setMessage("string");
            res.getOutputStream().print(gson.toJson(message));
            res.getOutputStream().flush();
            return;
        } else {
            // todo resort not found function need to be done
            try {
                String reqInfo = getReqInfo(req);
                ResortsBodyInfo resortsBodyInfo = gson.fromJson(reqInfo, ResortsBodyInfo.class);

                if (!Validate.resortBodyInfoValid(resortsBodyInfo)) {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("body parameters error");
                    return;
                }

                JsonObject resortInfo = createResortInfo(ss, resortsBodyInfo);

                Channel channel = null;
                try {
                    channel = pool.borrowObject();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    channel.basicPublish("", QUEUE_NAME, null, resortInfo.toString().getBytes());
                } catch (Exception e) {
                    System.out.println(e.toString());
                } finally {
                    try {
                        if (channel != null) {
                            pool.returnObject(channel);
                        }
                    } catch (Exception e) {
                        System.out.println("return channel error");
                    }
                }
                res.setStatus(HttpServletResponse.SC_CREATED);
            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("input correct!");
        }
    }

    // create the json object
    private JsonObject createResortInfo(String[] urlInfo, ResortsBodyInfo resortsBodyInfo) {
        JsonObject resortInfo = new JsonObject();
        resortInfo.addProperty("resortID", Integer.valueOf(urlInfo[0]));
        resortInfo.addProperty("SeasonValue", resortsBodyInfo.getYear());
        return resortInfo;
    }

    private String getReqInfo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = req.getReader().readLine()) != null) {
            sb.append(temp);
        }

        return sb.toString();
    }



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

}

