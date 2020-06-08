package jksj.usual.exception.chapter01;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.IOException;

public class TomcatUtils {

    private static final ThreadLocal<String> userThreadLocal = ThreadLocal.withInitial(() -> null);

    public static void main(String[] args) throws LifecycleException {

        // 1.新建一个Tomcat对象
        Tomcat tomcat = new Tomcat();

        // 2.创建一个连接器，默认 8080 端口
        Connector connector = tomcat.getConnector();
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        protocol.setMaxThreads(1);

        // 3.设置Host的属性
        Host host = tomcat.getHost();

        // 4.加载class
        String classPath = System.getProperty("user.dir");
        tomcat.addContext(host, "/", classPath);

        Wrapper wrapper = tomcat.addServlet("/", "HelloServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

                String before = userThreadLocal.get();
                System.out.println("before " + before);

                String now = req.getParameter("userId");
                System.out.println("now " + now);

                userThreadLocal.set(now);

                resp.getWriter().write(Thread.currentThread().getName() + " success at " + System.currentTimeMillis());
            }
        });
        wrapper.addMapping("/hello");

        // 5.启动
        tomcat.start();
        // 6.强制Tomcat Server等待
        tomcat.getServer().await();

        // 访问 http://localhost:8080/hello
    }
}
