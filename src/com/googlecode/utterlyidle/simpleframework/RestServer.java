package com.googlecode.utterlyidle.simpleframework;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.CloseableCallable;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.ServerConfiguration;
import com.googlecode.utterlyidle.httpserver.HelloWorld;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.jetty.RestApplicationActivator;
import com.googlecode.utterlyidle.modules.RequestInstanceModule;
import com.googlecode.utterlyidle.modules.SingleResourceModule;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static com.googlecode.utterlyidle.ServerConfiguration.serverConfiguration;
import static java.lang.String.format;
import static java.lang.System.nanoTime;

public class RestServer implements Server {
    private final Connection connection;
    private final Closeable closableActivator;
    private Url url;

    public RestServer(final CloseableCallable<Application> applicationActivator) throws Exception {
        this(applicationActivator, serverConfiguration());
    }

    public RestServer(final CloseableCallable<Application> applicationActivator, final ServerConfiguration configuration) throws Exception {
        closableActivator = applicationActivator;
        connection = startApp(applicationActivator, configuration);
    }

    public void close() throws IOException {
        connection.close();
        closableActivator.close();
    }

    public static void main(String[] args) throws Exception {
        new Test();
    }

    public Url getUrl() {
        return url;
    }

    public static class Test extends RestServer {
        public Test() throws Exception {
            super(new RestApplicationActivator(new SingleResourceModule(HelloWorld.class)), serverConfiguration().port(8000));
        }
    }

    private Connection startApp(CloseableCallable<Application> applicationActivator, ServerConfiguration configuration) throws Exception {
        long start = nanoTime();
        SocketConnection connection1 = startUpApp(applicationActivator.call(), configuration);
        System.out.println(format("Listening on %s, started SimpleWeb in %s msecs", url, calculateMilliseconds(start, nanoTime())));
        return connection1;
    }

    private SocketConnection startUpApp(Application application, ServerConfiguration configuration) throws IOException {
        Container container = new RestContainer(application.add(new RequestInstanceModule(configuration.serverUrl())));
        SocketConnection connection = new SocketConnection(new ContainerServer(container, configuration.maxThreadNumber()));
        InetSocketAddress socketAddress = (InetSocketAddress) connection.connect(new InetSocketAddress(configuration.serverUrl().host(), configuration.serverUrl().port()));
        updatePort(configuration, socketAddress);
        url = configuration.serverUrl();
        return connection;
    }

    private void updatePort(ServerConfiguration configuration, InetSocketAddress socketAddress) {
        configuration.port(socketAddress.getPort());
    }
}
