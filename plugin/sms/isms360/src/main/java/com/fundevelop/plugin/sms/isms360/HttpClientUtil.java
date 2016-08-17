package com.fundevelop.plugin.sms.isms360;

import com.fundevelop.commons.web.utils.PropertyUtil;
import org.apache.http.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * .
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江) 创建于 16/8/17 13:13
 */
class HttpClientUtil {
    private String sendUrl;
    private HttpParams params;
    private HttpProcessor httpproc;
    private HttpRequestExecutor httpexecutor;
    private HttpContext context;
    private HttpHost host;
    private DefaultHttpClientConnection conn;
    private ConnectionReuseStrategy connStrategy;

    public HttpClientUtil() {
        this(PropertyUtil.get("isms360.sms.host.ip"), Integer.parseInt(PropertyUtil.get("isms360.sms.host.port"), 10), PropertyUtil.get("isms360.sms.host.sendUrl"));
    }

    public HttpClientUtil(String hostIp, int port, String sendUrl) {
        this.sendUrl = sendUrl;
        params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "GB2312");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);

        httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        httpexecutor = new HttpRequestExecutor();
        context = new BasicHttpContext(null);
        host = new HttpHost(hostIp, port);

        conn = new DefaultHttpClientConnection();
        connStrategy = new DefaultConnectionReuseStrategy();
        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
    }

    public String sendPostMessage(String user, String pwd, String ServiceID, String dest, String sender, String msg) {
        String msgid = "";

        try {
            String parf = "src=%s&pwd=%s&ServiceID=%s&dest=%s&sender=%s&msg=%s";
            String par = String.format(parf, user, pwd, ServiceID, dest, sender, msg);
            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",  sendUrl.trim());
            logger.debug(">> Request URI: " + request.getRequestLine().getUri());
            logger.debug(">> Request URI:" + par);
            byte[] data1 = par.getBytes("ASCII");
            ByteArrayEntity entiy = new ByteArrayEntity(data1);
            entiy.setContentType("application/x-www-form-urlencoded");
            request.setEntity(entiy);

            logger.debug(">> Request URI: " + request.getRequestLine().getMethod());
            request.setParams(params);

            if (!conn.isOpen()) {
                Socket socket = new Socket(host.getHostName(), host.getPort());
                conn.bind(socket, params);
            }

            HeaderIterator it = request.headerIterator();
            while(it.hasNext()) {
                Header hesd =  it.nextHeader();
                logger.debug(">> Request Header: " +hesd.getName() + " : " + hesd.getValue());
            }


            httpexecutor.preProcess(request, httpproc, context);
            HttpResponse response = httpexecutor.execute(request, conn, context);
            response.setParams(params);

            httpexecutor.postProcess(response, httpproc, context);

            logger.debug("<< Response: " + response.getStatusLine());
            msgid = EntityUtils.toString(response.getEntity());
            logger.debug(msgid);
            logger.debug("==============");
        }catch(Exception e){
            msgid = "";
        } finally {
            try {
                conn.close();
            } catch (IOException e) {}
        }

        return msgid;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
}
