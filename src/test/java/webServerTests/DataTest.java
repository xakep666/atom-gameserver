package webServerTests;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Assert;
import org.junit.Test;
import webserver.APIServlet;

import static webServerTests.WebServerTest.SERVICE_URL;
import static webServerTests.WebServerTest.genRandomStr;

/**
 * Created by xakep666 on 13.10.16.
 *
 * Unit tests for Data API
 */
public class DataTest {
    @Test
    public void getLoggedInTest() {
        String user1 = genRandomStr();
        String user2 = genRandomStr();
        String pass = genRandomStr();
        APIServlet.base.register(user1,pass);
        APIServlet.base.register(user2,pass);
        APIServlet.base.requestToken(user1,pass);

        String requestUrl = SERVICE_URL + "data/users";
        Request request =new Request.Builder()
                .url(requestUrl)
                .get()
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            OkHttpClient httpClient = new OkHttpClient();
            Response resp = httpClient.newCall(request).execute();
            Assert.assertTrue(resp.isSuccessful());
            Assert.assertEquals(resp.body().string(),"{\"users\":[\""+user1+"\"]}");
            resp.body().close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
}
