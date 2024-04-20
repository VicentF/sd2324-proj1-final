package tukano.servers.rest;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory; 
import org.glassfish.jersey.server.ResourceConfig;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;
import tukano.discover.Discovery;




public class RestServerUsers {
	private static Logger Log = Logger.getLogger(RestServerUsers.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static final int PORT = 3456;
    
    public static final String SERVICE = "users";
    

    public static void main(String[] args) {

        try {
        	
            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            
            Discovery discovery = Discovery.getInstance();
            discovery.announce(SERVICE, serverURI);
            ResourceConfig config = new ResourceConfig();
            config.register(  RestResourceUsers.class );

            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);


            Log.info(String.format("%s Server is ready @ %s\n", SERVICE, serverURI));

        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
