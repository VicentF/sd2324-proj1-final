package tukano.servers.rest;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory; 
import org.glassfish.jersey.server.ResourceConfig;
import tukano.discover.Discovery;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;





public class RestServerShorts {
    private static Logger Log = Logger.getLogger(RestServerShorts.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 4567;
    
    public static final String SERVICE = "shorts";
    
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {

        try {


            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            
            Discovery discovery = Discovery.getInstance();
            discovery.announce(SERVICE, serverURI);
            
            ResourceConfig config = new ResourceConfig();
            config.register(  RestResourceShorts.class );
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);



            Log.info(String.format("%s Server is ready @ %s\n", SERVICE, serverURI));

        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}