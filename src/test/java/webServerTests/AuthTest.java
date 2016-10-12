package webServerTests;

import com.squareup.okhttp.*;
import org.junit.Assert;
import org.junit.Test;
import webserver.APIServlet;

/**
 * Created by xakep666 on 12.10.16.
 *
 * Unit tests for authentication
 */
public class AuthTest {
    private static final String SERVICE_URL = "http://localhost:"+APIServlet.PORT+"/";
    @Test
    public void testRegister() {
        MediaType mType = MediaType.parse("raw");
        RequestBody body = RequestBody.create(mType,"login=user&password=pass");
        String requestUrl = SERVICE_URL + "auth/register";
        Request request =new Request.Builder()
                .url(requestUrl)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            new Thread(() -> {
                try {
                    APIServlet.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.toString());
                }
            }).start();
            Thread.sleep(20000); //wait till server starts
            OkHttpClient httpClient = new OkHttpClient();
            Response resp = httpClient.newCall(request).execute();
            System.out.println(resp.body().string());
            Assert.assertTrue(resp.isSuccessful());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
}
