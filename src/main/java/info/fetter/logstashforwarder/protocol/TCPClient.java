package info.fetter.logstashforwarder.protocol;

import com.alibaba.fastjson.JSON;
import info.fetter.logstashforwarder.Event;
import info.fetter.logstashforwarder.ProtocolAdapter;
import info.fetter.logstashforwarder.util.AdapterException;
import org.apache.commons.io.HexDump;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

public class TCPClient implements ProtocolAdapter {

    private final static Logger logger = Logger.getLogger(TCPClient.class);

    private Socket socket;
    private SSLSocket sslSocket;
    private KeyStore keyStore;
    private String server;
    private int port;
    private DataOutputStream output;
    private DataInputStream input;
    PrintWriter writer;

    public TCPClient(String keyStorePath, String server, int port, int timeout) throws IOException {
        this.server = server;
        this.port = port;

        try {

            socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(server), port), timeout);
            socket.setSoTimeout(timeout);



            boolean useTLS=false;
            if (useTLS) {
                if(keyStorePath == null) {
                    throw new IOException("Key store not configured");
                }
                if(server == null) {
                    throw new IOException("Server address not configured");
                }

                keyStore = KeyStore.getInstance("JKS");
                keyStore.load(new FileInputStream(keyStorePath), null);

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
                tmf.init(keyStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                SSLSocketFactory socketFactory = context.getSocketFactory();

                sslSocket = (SSLSocket)socketFactory.createSocket(socket, server, port, true);
                sslSocket.setUseClientMode(true);
                sslSocket.startHandshake();

                output = new DataOutputStream(new BufferedOutputStream(sslSocket.getOutputStream()));
                input = new DataInputStream(sslSocket.getInputStream());
            }else{
                output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                input = new DataInputStream(socket.getInputStream());
            }

            writer = new PrintWriter(output, true);

            logger.info("Connected to " + server + ":" + port);
        } catch(IOException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int sendEvents(List<Event> eventList) throws AdapterException {
        try {
            int numberOfEvents = eventList.size();

            if(logger.isInfoEnabled()) {
                logger.info("Sending " + numberOfEvents + " events");
            }

            for(Event event : eventList) {
                writer.println(JSON.toJSONString(event.getKeyValues()));

            }
            output.flush();
        } catch(Exception e) {
            throw new AdapterException(e);
        }
        return 0;
    }

    public void close() throws AdapterException {
        try {
            sslSocket.close();
        } catch(Exception e) {
            throw new AdapterException(e);
        }
        logger.info("Connection to " + server + ":" + port + " closed");
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }
}
