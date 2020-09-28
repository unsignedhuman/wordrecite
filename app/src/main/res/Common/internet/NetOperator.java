package Common.internet.;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 用于访问网络
 * 从inputstream中下载xml文件
 * */
public class NetOperator {
    public final static String youdaoURL= "http://fanyi.youdao.com/openapi.do?keyfrom=youdao111&key=60638690&type=data&doctype=xml&version=1.1&q=";

    public static InputStream getInputStreamByUrl(String urlStr){
        InputStream tempInput=null;
        URL url=null;
        HttpURLConnection connection=null;
        //设置超时时间

        try{
            url=new URL(urlStr);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(10000);
            tempInput=connection.getInputStream();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tempInput;
    }

}