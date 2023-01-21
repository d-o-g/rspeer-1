package org.rspeer.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class ProxySocketProvider extends SocketProvider {

    private static final String USE_SYSTEM_PROXIES = "java.net.useSystemProxies";

    private final ProxySelector selector = ProxySelector.getDefault();

    @Override
    public Socket provide() throws IOException {
        if (host == null || port == -1) {
            throw new IllegalStateException("SocketProvider not initialized!");
        }

        boolean useSystemProxies = Boolean.parseBoolean(System.getProperty(USE_SYSTEM_PROXIES));
        if (!useSystemProxies) {
            System.setProperty(USE_SYSTEM_PROXIES, "true");
        }

        boolean secure = port == 443;
        List<Proxy> primary;
        List<Proxy> secondary;

        try {
            primary = selector.select(new URI((secure ? "https" : "http") + "://" + host));
            secondary = selector.select(new URI((secure ? "http" : "https") + "://" + host));
        } catch (URISyntaxException ex) {
            return direct();
        }

        List<Proxy> proxies = new ArrayList<>();
        proxies.addAll(primary);
        proxies.addAll(secondary);

        ProxyAuthenticationException exception = null;

        for (Proxy proxy : proxies) {
            Socket socket;
            try {
                Socket proxied = proxy(proxy);
                if (proxied == null) {
                    continue;
                }
                socket = proxied;
            } catch (ProxyAuthenticationException e) {
                exception = e;
                continue;
            } catch (IOException e) {
                continue;
            }
            return socket;
        }

        if (exception != null) {
            throw exception;
        }

        return direct();
    }

    private Socket connect(String host, int port, String headers) throws IOException {
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(10000);
        OutputStream out = socket.getOutputStream();

        if (headers == null) {
            out.write(("CONNECT " + host + ":" + port + " HTTP/1.0\n\n").getBytes(Charset.forName("ISO-8859-1")));
        } else {
            out.write(("CONNECT " + host + ":" + port + " HTTP/1.0\n" + headers + "\n\n").getBytes(Charset.forName("ISO-8859-1")));
        }

        out.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();

        if (line != null) {
            if (line.startsWith("HTTP/1.0 200") || line.startsWith("HTTP/1.1 200")) {
                return socket;
            }

            if (line.startsWith("HTTP/1.0 407") || line.startsWith("HTTP/1.1 407")) {
                int count = 0;
                String authenticate = "proxy-authenticate: ";

                for (line = reader.readLine(); line != null && count < 50; count++) {
                    if (line.toLowerCase().startsWith(authenticate)) {
                        line = line.substring(authenticate.length()).trim();
                        int index = line.indexOf(' ');
                        if (index != -1) {
                            line = line.substring(0, index);
                        }

                        throw new ProxyAuthenticationException(line);
                    }

                    line = reader.readLine();
                }

                throw new ProxyAuthenticationException("");
            }
        }

        out.close();
        reader.close();
        socket.close();
        return null;
    }

    private Socket proxy(Proxy proxy) throws IOException {
        if (proxy.type() == Proxy.Type.DIRECT) {
            return direct();
        }

        SocketAddress address = proxy.address();
        if (!(address instanceof InetSocketAddress)) {
            return null;
        }

        InetSocketAddress inet = (InetSocketAddress) address;
        if (proxy.type() == Proxy.Type.HTTP) {
            String headers = null;
            try {
                Class<?> authInfo = Class.forName("sun.net.www.protocol.http.AuthenticationInfo");
                Method authHandle = authInfo.getDeclaredMethod("getProxyAuth", String.class, Integer.TYPE);
                authHandle.setAccessible(true);
                Object auth = authHandle.invoke(null, inet.getHostName(), inet.getPort());
                if (auth != null) {
                    Method preemptiveHandle = authInfo.getDeclaredMethod("supportsPreemptiveAuthorization");
                    preemptiveHandle.setAccessible(true);
                    if ((Boolean) preemptiveHandle.invoke(auth)) {
                        Method nameHandle = authInfo.getDeclaredMethod("getHeaderName");
                        nameHandle.setAccessible(true);
                        Method valueHandle = authInfo.getDeclaredMethod("getHeaderValue", URL.class, String.class);
                        valueHandle.setAccessible(true);
                        String name = (String) nameHandle.invoke(auth);
                        String value = (String) valueHandle.invoke(auth, new URL("https://" + host + "/"), "https");
                        headers = name + ": " + value;
                    }
                }
            } catch (Exception ignored) {

            }

            return connect(inet.getHostName(), inet.getPort(), headers);
        }

        if (proxy.type() == Proxy.Type.SOCKS) {
            Socket socket = new Socket(proxy);
            socket.connect(new InetSocketAddress(host, port));
            return socket;
        }

        return null;
    }
}