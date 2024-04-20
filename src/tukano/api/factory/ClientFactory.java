package tukano.api.factory;

import java.net.URI; 

import tukano.api.java.Users;
import tukano.clients.rest.RestUsersClient;
import tukano.discover.Discovery;

public class ClientFactory {

	public static Users getClient() {
	    Discovery discovery = Discovery.getInstance();

	    URI[] uris = fetchServiceUris(discovery, "users");
	    if (uris == null || uris.length == 0) {
	        return null; 
	    }

	    return createUserClientFromUri(uris[0]); // Use the first URI
	}

	private static URI[] fetchServiceUris(Discovery discovery, String serviceName) {
	    return discovery.knownUrisOf(serviceName, 1);
	}

	private static Users createUserClientFromUri(URI serverURI) {
	    if (serverURI.toString().endsWith("rest")) {
	        return new RestUsersClient(serverURI);
	    }
	    return null; 
	}

}
