package com.common.app.http;

import android.content.Context;

import com.common.app.common.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class HttpClientHelper {

    private static final String CHARSET = HTTP.UTF_8;
    private static HttpClient customerHttpClient;
    private static String TAG = "HttpClientHelper";

    private HttpClientHelper() {
    }

    public static synchronized HttpClient getHttpClient() {
        if (null == customerHttpClient) {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            ConnManagerParams.setTimeout(params, 10000);
            ConnPerRouteBean connPerRoute = new ConnPerRouteBean(1000);
            ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
            ConnManagerParams.setMaxTotalConnections(params, 100);
            HttpConnectionParams.setConnectionTimeout(params, 20000);
            HttpConnectionParams.setSoTimeout(params, 20000);

            // 设置HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
            customerHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return customerHttpClient;
    }

    public static InputStream getImageStream(String url) {
        try {
            if (url == null || url.trim().length() == 0) {
                return null;
            }
            URL u = new URL(url);
            return (InputStream) u.getContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static HttpEntity getHttpEntityFromGet(Context context, String url, boolean retry) {
        Log.v(TAG, "getHttpEntityFromGet===========" + url);

        try {
            if (url != null) {
                url = url.replaceAll("\\^", "%5e");
                url = url.replaceAll("\\{", "%7b");
                url = url.replaceAll("\\}", "%7d");
            } else {
                return null;
            }

            HttpGet request = new HttpGet(url);

            //in this place we can set request header

            HttpResponse response = null;
            try {
                HttpClient client = getHttpClient();
                response = client.execute(request);
            } catch (NullPointerException e) {
                customerHttpClient = null;
                HttpClient client = getHttpClient();
                response = client.execute(request);
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("the http request failed");
            }
            HttpEntity resEntity = response.getEntity();
            return (resEntity == null) ? null : resEntity;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            if (retry) return null;
            if (context != null) {
                reportHttpLog(context, 1);
            }
//            Log.v(TAG, "use bak url = " + url);
//            url = url.replace("host", "bak host");
//            return getHttpEntityFromGet(context, url, true);
            
            return null;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (ConnectionPoolTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (retry) return null;
            
            return null;
        } finally {
        }
    }

    public static InputStream getStreamFromGet(String url) {
        return getStreamFromGet(null, url);
    }

    public static InputStream getStreamFromGet(Context context, String url) {
        try {
            HttpEntity resEntity = getHttpEntityFromGet(context, url, false);
            InputStream result = (resEntity == null) ? null : resEntity.getContent();
            if (resEntity != null && resEntity.getContentEncoding() != null
                    && resEntity.getContentEncoding().getValue() != null
                    && "gzip".equals(resEntity.getContentEncoding().getValue())) {
                result = new GZIPInputStream(result);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static String getStringFromGet(String url) {
        return getStringFromGet(null, url);
    }

    public static String getStringFromGet(Context context, String url) {
        try {
//            HttpEntity resEntity = getHttpEntityFromGet(url);
//            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
            InputStream is = getStreamFromGet(context, url);
            return (is == null) ? null : convertStreamToString(is).trim();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }


    protected static HttpEntity getHttpEntityFromPost(Context context, String url, List<NameValuePair> params, boolean retry) {
        try {
            // 编码参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            for (NameValuePair p : params) {
                formparams.add(p);
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
            //in this place we can set request header
            request.setEntity(entity);
            // 发送请求
            HttpResponse response = null;
            try {
                HttpClient client = getHttpClient();
                response = client.execute(request);
            } catch (NullPointerException e) {
                customerHttpClient = null;
                HttpClient client = getHttpClient();
                response = client.execute(request);
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("the http request failed");
            }
            HttpEntity resEntity = response.getEntity();
            return (resEntity == null) ? null : resEntity;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (ConnectionPoolTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
            
            return null;
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } finally {
        }
    }

    protected static HttpEntity getHttpEntityFromPost(Context context, String url, String requestBody, boolean retry) { 
        try {
            StringEntity entity = new StringEntity(requestBody, CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
           
           
            // 发送请求
            HttpResponse response = null;
            try {
                HttpClient client = getHttpClient();
                response = client.execute(request);
            } catch (NullPointerException e) {
                customerHttpClient = null;
                HttpClient client = getHttpClient();
                response = client.execute(request);
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("the http request failed");
            }
            HttpEntity resEntity = response.getEntity();
            return (resEntity == null) ? null : resEntity;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (ConnectionPoolTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (retry) return null;
           
            return null;
        } finally {
        }
    }

    public static InputStream getStreamFromPost(String url, Map<String, Object> parms) {
        return getStreamFromPost(null, url, parms);
    }

    public static InputStream getStreamFromPost(Context context, String url, Map<String, Object> parms) {
        try {
            if ("".equals(url) || parms == null || url.trim().length() <= 0) {
                return null;
            }

            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            if (parms != null && parms.size() > 0) {
                Iterator it = parms.entrySet().iterator();
                int i = 0;
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    formparams.add(new BasicNameValuePair(key.toString(), value.toString()));
                    i++;
                }
            }

            HttpEntity resEntity = getHttpEntityFromPost(context, url, formparams, false);
            InputStream result = (resEntity == null) ? null : resEntity.getContent();
            if (resEntity != null && resEntity.getContentEncoding() != null
                    && resEntity.getContentEncoding().getValue() != null
                    && "gzip".equals(resEntity.getContentEncoding().getValue())) {
                result = new GZIPInputStream(result);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static InputStream getStreamFromPost(String url, String requestBody) {
        return getStreamFromPost(null, url, requestBody);
    }

    public static InputStream getStreamFromPost(Context context, String url, String requestBody) {
        try {
            HttpEntity resEntity = getHttpEntityFromPost(context, url, requestBody, false);
            InputStream result = (resEntity == null) ? null : resEntity.getContent();
            if (resEntity != null && resEntity.getContentEncoding() != null
                    && resEntity.getContentEncoding().getValue() != null
                    && "gzip".equals(resEntity.getContentEncoding().getValue())) {
                result = new GZIPInputStream(result);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static String getStringFromPost(String url, Map<String, Object> parms) {
        return getStringFromPost(null, url, parms);
    }

    public static String getStringFromPost(Context context, String url, Map<String, Object> parms) {
        try {
            InputStream is = getStreamFromPost(context, url, parms);
            return (is == null) ? null : convertStreamToString(is).trim();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static String getStringFromPost(String url, String requestBody) {
        return getStringFromPost(null, url, requestBody);
    }

    public static String getStringFromPost(Context context, String url, String requestBody) {
        try {
            InputStream is = getStreamFromPost(context, url, requestBody);
            return (is == null) ? null : convertStreamToString(is).trim();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    private static void reportHttpLog(final Context context, int type) {

    }


    public static String convertStreamToString(InputStream is) {
        /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
