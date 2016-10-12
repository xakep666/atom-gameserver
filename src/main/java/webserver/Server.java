package webserver;

import controller.InMemoryBase;
import controller.UsersBase;
import matchmaker.MatchMaker;
import matchmaker.SinglePlayerMatchMaker;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import webserver.auth.AuthenticationFilter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xakep666 on 12.10.16.
 *
 * Provides web-server to access all services using REST api
 */
public class Server {
    private static final int PORT = 80;
    private Server() {}
    public static UsersBase base = new InMemoryBase();
    public static List<MatchMaker> matchMakers = new LinkedList<>();

    static {
        matchMakers.add(new SinglePlayerMatchMaker());
    }

    public static void start() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(PORT);
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                Server.class.getPackage().getName()
        );

        jerseyServlet.setInitParameter(
                "com.sun.jersey.spi.container.ContainerRequestFilters",
                AuthenticationFilter.class.getCanonicalName()
        );

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}