import com.google.gson.Gson;
import skierFunctions.*;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;


public class SkierServlet extends HttpServlet {

    private final static String QUEUE_NAME = "SkierServletQueue";

    private Gson gson = new Gson();
    private ObjectPool<Channel> pool;

    public void init() {
        this.pool = new GenericObjectPool<Channel>(new ConnectionPoolFactory());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        String[] ss = urlPath.split("/");

        if (!Validate.urlInfoCheck(ss)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Message msg = new Message();
            msg.setMessage("string");
            res.getWriter().write(gson.toJson(msg));
        } else {
            try {
                String reqInfo = getReqInfo(req);
                SkierBodyInfo skierBodyInfo = gson.fromJson(reqInfo, SkierBodyInfo.class);

                if (!Validate.skierBodyInfoValid(skierBodyInfo)) {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("body parameters error");
                    return;
                }

                JsonObject liftInfo = createLiftRideInfo(ss, skierBodyInfo);

                Channel channel = null;
                try {
                    channel = pool.borrowObject();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    channel.basicPublish("", QUEUE_NAME, null, liftInfo.toString().getBytes());
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
        }
    }

    private String getReqInfo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = req.getReader().readLine()) != null) {
            sb.append(temp);
        }

        return sb.toString();
    }


    // create the json object
    private JsonObject createLiftRideInfo(String[] urlInfo, SkierBodyInfo skierBodyInfo) {
        JsonObject liftRideInfo = new JsonObject();
        liftRideInfo.addProperty("resortID", Integer.valueOf(urlInfo[1]));
        liftRideInfo.addProperty("seasonID", Integer.valueOf(urlInfo[3]));
        liftRideInfo.addProperty("dayID", Integer.valueOf(urlInfo[5]));
        liftRideInfo.addProperty("skierId", Integer.valueOf(urlInfo[7]));
        liftRideInfo.addProperty("time", skierBodyInfo.getTime());
        liftRideInfo.addProperty("liftNum", skierBodyInfo.getLiftNum());
        liftRideInfo.addProperty("waitTime", skierBodyInfo.getWaitTime());

        return liftRideInfo;
    }


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
        return true;
    }



}

