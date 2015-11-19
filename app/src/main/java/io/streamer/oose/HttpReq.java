package io.streamer.oose;
import com.loopj.android.http.*;

public class HttpReq {
    private static final String BASE_URL = "http://192.168.43.75:8080";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient sclient = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void sget(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        sclient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}