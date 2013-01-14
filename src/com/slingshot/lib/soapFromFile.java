package com.slingshot.lib;

import android.os.Environment;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 09.01.13
 * Time: 11:29
 * make soap request with expense from file on sd
 */

public class soapFromFile {
    public String text=null;
//private String logPass="admin:123";


    String fileEnvelopePath="";
    public soapFromFile(String pathIn){
        fileEnvelopePath=pathIn;
        //logPass=login+":"+password;
    }

    public String responseString=null;
    public Element call(String url, String soapAction)  {
        final DefaultHttpClient httpClient=getClient(); // new DefaultHttpClient();
        Log.d("url_soap", url);
        // параметры запроса
        HttpParams params = httpClient.getParams();
        // HttpConnectionParams.setConnectionTimeout(params, 10000);
        //  HttpConnectionParams.setSoTimeout(params, 15000);
        params.setBooleanParameter("http.protocol.expect-continue", false);   //!!
        // устанавливаем параметры
        HttpProtocolParams.setUseExpectContinue(params, true);   //httpClient.getParams()

        // С помощью метода POST отправляем конверт
        HttpPost httppost = new HttpPost(url);
        // и заголовки

        httppost.setHeader("soapaction", soapAction);
        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");





        try {
            // выполняем запрос
           // HttpEntity entity = new StringEntity(envelope);
            HttpEntity entity = new InputStreamEntity( new FileInputStream(new File(Environment.getExternalStorageDirectory(),fileEnvelopePath)),-1);
            httppost.setEntity(entity);
            // Заголоаок запроса
            ResponseHandler<String> rh=new ResponseHandler<String>() {
                // вызывается, когда клиент пришлет ответ
                public String handleResponse(HttpResponse response)  throws ClientProtocolException, IOException {
                    // получаем ответ
                    HttpEntity entity = response.getEntity();
                    // читаем его в массив
                    StringBuffer out = new StringBuffer();
                    byte[] b = EntityUtils.toByteArray(entity);
                    // write the response byte array to a string buffer
                    out.append(new String(b, 0, b.length));
                    return out.toString();
                }
            };

            responseString=httpClient.execute(httppost, rh);
        }catch (Exception e) {      Log.v("exception", e.toString());}

        // закрываем соединение
        httpClient.getConnectionManager().shutdown();
        //  text=  httpClient.
        text= responseString;
        if(responseString!=null){
            Log.d("soap", responseString);
        }else {
            Log.d("soap", "responseString =null");}

        //парсим

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(responseString)));
            Element body=(Element) ((Element) (doc.getElementsByTagName("soap:Envelope").item(0))).getElementsByTagName("soap:Body").item(0);
            return body;

        } catch (Exception e) {       e.printStackTrace(); return null; }

        // return null;
    }




    //
    public DefaultHttpClient getClient()  {

        DefaultHttpClient ret = null;

        //SETS UP PARAMETERS
        HttpParams params = new BasicHttpParams();
        //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        // HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);
        //REGISTERS SCHEMES FOR BOTH HTTP AND HTTPS
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));




        // final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        //  sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        try{
            registry.register(new Scheme("https", new Naive_SSLSocketFactory(), 443));
        }catch (Exception e){e.getMessage();}
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        ret = new DefaultHttpClient(manager, params);
        return ret;

    }

    public class Naive_SSLSocketFactory extends SSLSocketFactory
    {
        protected SSLContext Cur_SSL_Context = SSLContext.getInstance("TLS");
        public Naive_SSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
        {
            super(null, null, null, null, null, null);
            Cur_SSL_Context.init(null, new TrustManager[]{new X509_Trust_Manager()}, null);
        }
        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException
        {
            return Cur_SSL_Context.getSocketFactory().createSocket(socket, host, port, autoClose);
        }
        @Override
        public Socket createSocket() throws IOException
        {
            return Cur_SSL_Context.getSocketFactory().createSocket();
        }
    }

    private class X509_Trust_Manager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }
    }


}
