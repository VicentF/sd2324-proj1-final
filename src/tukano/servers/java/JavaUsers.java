package tukano.servers.java;

import java.util.List;

import tukano.api.Following;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Users;
import tukano.api.java.Result.ErrorCode;
import tukano.database.Hibernate;
import tukano.discover.Discovery;

public class JavaUsers implements Users{
	
	private Hibernate hibernateInst = Hibernate.getInstance();

	@Override
	public Result<String> createUser(User user) {
		
		//check if user has null parameters
		if (user.getDisplayName() == null || user.getEmail() == null || user.getPwd() == null || user.getUserId() == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		
		//check if already exists with same id 
		List<User> savedUsers = hibernateInst.jpql("SELECT u FROM User u WHERE u.userId = '"+user.userId()+"'", User.class);
				if(!savedUsers.isEmpty()) 
					return Result.error(Result.ErrorCode.CONFLICT);
	
        try {
        	
        	hibernateInst.persist(user);
            return Result.ok(user.getUserId());
        } catch (Exception e) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
	}

	@Override
	public Result<User> getUser(String userId, String pwd) {
		//check if user has null parameters
		if (userId == null || pwd == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		
	    // Retrieve user by userId
	    List<User> savedUsers = hibernateInst.jpql("SELECT u FROM User u WHERE u.userId = '" + userId + "'", User.class);
	    if (savedUsers.isEmpty())
	        return Result.error(Result.ErrorCode.NOT_FOUND);

	    // Check password match
	    User user = savedUsers.get(0);
	    if (!user.getPwd().equals(pwd))
	        return Result.error(Result.ErrorCode.FORBIDDEN);
	    
	    
        try {
            Discovery discovery = Discovery.getInstance();
            discovery.announce("users", "http://172.18.0.3:3546/rest");  
        } catch (Exception e) {

            e.printStackTrace();
        }


	    return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String pwd, User user) {
	    if (userId == null || pwd == null || user == null)
	        return Result.error(Result.ErrorCode.BAD_REQUEST);
	    
	    // Check if the user is trying to update the userID or if there's nothing to update
	    if (user.getUserId() != null)
	        return Result.error(Result.ErrorCode.BAD_REQUEST);
	    
	    // Retrieve user by userId
	    List<User> users = hibernateInst.jpql("SELECT u FROM User u WHERE u.userId = '" + userId + "'", User.class);
	    if (users.isEmpty())
	        return Result.error(Result.ErrorCode.NOT_FOUND);
	    
	    User userToUpdate = users.get(0);
	    // Check if the provided password matches the stored password
	    if (!userToUpdate.getPwd().equals(pwd))
	        return Result.error(Result.ErrorCode.FORBIDDEN);
	    
	    // Update user details if provided
	    if (user.getPwd() != null)
	        userToUpdate.setPwd(user.getPwd());
	    if (user.getDisplayName() != null)
	        userToUpdate.setDisplayName(user.getDisplayName());
	    if (user.getEmail() != null)
	        userToUpdate.setEmail(user.getEmail());

	    // Try to update the user in the database
	    try {
	        hibernateInst.update(userToUpdate);
	    } catch (Exception e) {
	        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
	    }

	    return Result.ok(userToUpdate);
	}


	@Override
	public Result<User> deleteUser(String userId, String pwd) {
		if(userId == null || pwd == null) {
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}
		
	    List<User> savedUsers = hibernateInst.jpql("SELECT u FROM User u WHERE u.userId = '" + userId + "'", User.class);
	    
	    if (savedUsers.isEmpty())
	    	return Result.error(Result.ErrorCode.NOT_FOUND);
	    
	    User userToDelete = savedUsers.get(0);
	    
	    if(!userToDelete.getPwd().equals(pwd)) {
	    	return Result.error(Result.ErrorCode.FORBIDDEN);
	    }
	    try {
	        hibernateInst.delete(userToDelete);
	        return Result.ok(userToDelete);  
	    } catch (Exception e) {
	        return Result.error(Result.ErrorCode.INTERNAL_ERROR);  
	    }
	    
	    
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
        if (pattern != null) {
            List<User> matches = hibernateInst.jpql("SELECT u FROM User u WHERE LOWER(u.userId) LIKE '%" + pattern.toLowerCase() + "%'", User.class);
            
            
            return Result.ok(matches);
            

        }
        else {
            return Result.error(ErrorCode.BAD_REQUEST);
        }
	}

}
