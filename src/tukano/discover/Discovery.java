package tukano.discover;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public interface Discovery {
	/**
	 * Get discovered URIs for a given service name
	 * @param serviceName - name of the service
	 * @param minReplies - minimum number of requested URIs. Blocks until the number is satisfied.
	 * @return array with the discovered URIs for the given service name.
	 */
	public URI[] knownUrisOf(String serviceName, int minReplies);
	/**
	 * Used to announce the URI of the given service name.
	 * @param serviceName - the name of the service
	 * @param serviceURI - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI);
	/**
	 * Get the instance of the Discovery service
	 * @return the singleton instance of the Discovery service
	 */
	public static Discovery getInstance() {
		return DiscoveryImpl.getInstance();
	}
}


class DiscoveryImpl implements Discovery {
	private static Discovery singleton;
	
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2262);
	private static final String DELIMITER = "\t";
	static final int DISCOVERY_RETRY_TIMEOUT = 5000;
	static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;
	


	private static final int MAX_DATAGRAM_SIZE = 65536;


	private Map<String, Set<URI>> uris = new ConcurrentHashMap<>();
	
	synchronized static Discovery getInstance() {
		if ( singleton == null ) {
			
			singleton = new DiscoveryImpl();
		}
		
		
		return singleton;
	}

	private DiscoveryImpl() {
		this.startListener();
	}

	@Override
	public void announce(String serviceName, String serviceURI) {
	    byte[] packetBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
	    DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length, DISCOVERY_ADDR);

	    // Start a background thread for sending periodic announcements
	    new Thread(() -> {
	        try (DatagramSocket socket = new DatagramSocket()) {
	            while (!Thread.currentThread().isInterrupted()) {
	                try {
	                    socket.send(packet);
	                    Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
	                } catch (InterruptedException ie) {
	                    Thread.currentThread().interrupt(); // Properly handle thread interruption
	                } catch (IOException ioException) {
	                    ioException.printStackTrace(); 
	                }
	            }
	        } catch (SocketException se) {
	            se.printStackTrace(); // Log exceptions 
	        }
	    }, "ServiceAnnouncerThread").start();
	}

	private void startListener() {
	    new Thread(() -> {
	        try (MulticastSocket multicastSocket = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
	            multicastSocket.joinGroup(DISCOVERY_ADDR, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
	            
	            
	            while (!Thread.currentThread().isInterrupted()) {
	                try {
	                    byte[] buf = new byte[MAX_DATAGRAM_SIZE];
	                    
	                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                    
	                    multicastSocket.receive(packet);
	                    
	                    String message = new String(packet.getData(), 0, packet.getLength());
	                    
	                    String[] parts = message.split(DELIMITER);
	                    
	                    
	                    if (parts.length == 2) {
	                        uris.computeIfAbsent(parts[0], k -> ConcurrentHashMap.newKeySet()).add(URI.create(parts[1]));
	                    }
	                   
	                } catch (IOException e) {
	                    System.err.println("Failed to receive or process packet: " + e.getMessage());
	                }
	            }
	        } catch (IOException e) {
	            System.err.println("Multicast socket failed: " + e.getMessage());
	        }
	        
	    }, "DiscoveryListenerThread").start();
	}

	@Override
	public URI[] knownUrisOf(String serviceName, int minEntries) {
	    long startTime = System.currentTimeMillis();
	    long timeout = DISCOVERY_RETRY_TIMEOUT;
	    
	    while (System.currentTimeMillis() - startTime < timeout) {
	    	
	        synchronized (uris) {
	            var res = uris.getOrDefault(serviceName, Collections.emptySet());
	            if (res.size() >= minEntries) {
	                return res.toArray(new URI[res.size()]);
	            }
	        }
	        
			try {
				Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
			} catch (InterruptedException e) {
			}
	    }
	    
	    throw new RuntimeException("Timeout waiting for URIs");
	}





}