package tukano.servers.java;

import java.net.URI;
import java.util.*;    
import java.util.logging.Logger;
import java.util.stream.Collectors;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.Following;
import tukano.api.Short;
import tukano.api.User;
import tukano.api.factory.ClientFactory;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.database.*;
import tukano.discover.Discovery;

public class JavaShorts implements Shorts {

    private static Logger Log = Logger.getLogger(JavaShorts.class.getName());

    private static int shortId = 1;
    private static int blobId = 1;
    private static Map<Integer, String> blobLocations = new HashMap<>(); // Where all blobs are located
    private Set<String> blobServers = new HashSet<>(); //All known blob server URLs

	@Override
	public Result<Short> getShort(String shortId) {

        if (shortId == null) 
        	return Result.error(ErrorCode.BAD_REQUEST);
        
        
        Hibernate hibernate = Hibernate.getInstance();
        var shorts = hibernate.jpql("SELECT s FROM Short s WHERE s.shortId = '" + shortId + "'", Short.class);
        
        if (shorts.isEmpty()) 
            return Result.error(ErrorCode.NOT_FOUND);
        
       Short shortReturn = shorts.get(0);

        return Result.ok(shortReturn);
	}
	
    
    @Override
    public Result<Short> createShort(String userId, String password) {
        Log.info("createShort : userId = " + userId);

        try {
            Users usersClient = ClientFactory.getClient();
            Result<User> userResult = usersClient.getUser(userId, password);
            

            if (!userResult.isOK()) {
                return Result.error(ErrorCode.BAD_REQUEST);
            }
            
          

            String blobUrl = "";

            Short newShort = new Short(String.valueOf(shortId++), userId, blobUrl);

            Hibernate hibernate = Hibernate.getInstance();

            hibernate.persist(newShort);
            return Result.ok(newShort);
            
         
    
            
        } catch (Exception ce) {
            ce.printStackTrace();
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
    }

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
return null;
	}




	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		Users usersClient = ClientFactory.getClient();
        Result<User> user1 = usersClient.getUser(userId1, password);
        
        if (!user1.isOK()) {
            return Result.error(ErrorCode.BAD_REQUEST);
        }
        
        Result<User> user2 = usersClient.getUser(userId2, "a"); // verificar se o user existe 
        
        if (!user2.isOK()) {
            if (!(user2.error() == ErrorCode.FORBIDDEN)) 
            	return Result.error(user2.error());
        }
        
        Hibernate hibernate = Hibernate.getInstance();
        
        Following c;
        Following d;
        
        var userA = hibernate.jpql("SELECT f FROM Following f WHERE f.followedUserId = '" + userId1 + "'", Following.class);
        
        var userB = hibernate.jpql("SELECT f FROM Following f WHERE f.followedUserId = '" + userId2 + "'", Following.class);

        if(userA.isEmpty()) {
        	c = new Following(userId1);
        	
        	hibernate.persist(c);
        }
        else {
        	c = userA.get(0);
        }
        
        if(userB.isEmpty()) {
        	d = new Following(userId2);
        	
        	hibernate.persist(d);
        }
        else {
        	d = userB.get(0);
        }
        
        List<String> Cfollows= c.getFollows();
        List<String> Dfollowers= c.getFollowers();
        
        
        if(isFollowing) {
        	if(Cfollows.contains(userId2)) {
        		return Result.error(ErrorCode.CONFLICT);
        	}
        	else {
        		Cfollows.add(userId2);
        		Dfollowers.add(userId1);
        	}
        }
        else if(!isFollowing) {
        	if(!Cfollows.contains(userId2)) {
        		return Result.error(ErrorCode.CONFLICT); //already not following
        	}
        	else {
        		Cfollows.remove(userId2);
        		Dfollowers.remove(userId1);
        	}
        }
        
        c.setFollowers(Cfollows);
        d.setFollows(Dfollowers);
        
        hibernate.update(c);
        hibernate.update(d);

        return Result.ok();

	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		Log.info("cabelo");
		Users usersClient = ClientFactory.getClient();
        Result<User> user = usersClient.getUser(userId, password);
        
        if (!user.isOK()) {
            return Result.error(ErrorCode.BAD_REQUEST);
        }
        Log.info("cabelo10");
        Hibernate hibernate = Hibernate.getInstance();
        Log.info("cabelo20");
        var query = hibernate.jpql("SELECT f FROM Following f WHERE f.followedUserId = '" + userId + "'", Following.class);
        if (query.isEmpty()) return Result.ok(new ArrayList<String>());
        return Result.ok(query.get(0).getFollowers());
        
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		

       
        return Result.ok(null);
	}
	
	@Override
	public Result<List<String>> getShorts(String userId) {
		Users usersClient = ClientFactory.getClient();
        Result<User> user = usersClient.getUser(userId, "1");
        
        if (!user.isOK()) {
            if (!(user.error() == ErrorCode.FORBIDDEN)) 
            	return Result.error(user.error());
        }
        
        
        Hibernate hibernate = Hibernate.getInstance();
        var userShorts = hibernate.jpql("SELECT s.shortId FROM Short s WHERE s.ownerId = '" + userId + "'", String.class);

        return Result.ok(userShorts);
	}

}
