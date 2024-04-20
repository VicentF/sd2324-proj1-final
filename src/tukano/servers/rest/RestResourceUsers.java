package tukano.servers.rest;

import java.util.List; 
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.rest.RestUsers;
import tukano.servers.java.JavaUsers;
import tukano.api.java.Users;



@Singleton
public class RestResourceUsers implements RestUsers {

    private final Users UserService;

    public RestResourceUsers() {
    	this.UserService = new JavaUsers();
    }
    
    
    
    //private method to convert results

    private Status convertToHttpStatus(Result<?> result) {
        switch (result.error()) {
        
        	case FORBIDDEN: return Status.FORBIDDEN;
        	case BAD_REQUEST: return Status.BAD_REQUEST;
            case CONFLICT: return Status.CONFLICT;
            case NOT_FOUND: return Status.NOT_FOUND;
            case INTERNAL_ERROR: return Status.INTERNAL_SERVER_ERROR;
            case NOT_IMPLEMENTED: return Status.NOT_IMPLEMENTED;
            case OK: return result.value() == null ? Status.NO_CONTENT : Status.OK;
            
            
            default: return Status.INTERNAL_SERVER_ERROR;
            
        }
    }
    
    private <T> T processResult(Result<T> result) {
        if (result.isOK()) {
            return result.value();
        } else {
            throw new WebApplicationException(convertToHttpStatus(result));
        }
    }

    @Override
    public String createUser(User user) {
    	
    return processResult(UserService.createUser(user));
        
    }
    

    @Override
    public User updateUser(String userId, String pwd, User user) {
    	
    return processResult(UserService.updateUser(userId, pwd, user));
    
    }
    

    @Override
    public User getUser(String userId, String pwd) {
        return processResult(UserService.getUser(userId, pwd));
    }


    @Override
    public User deleteUser(String userId, String pwd) {
    	
    	
        return processResult(UserService.deleteUser(userId, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) {
    	
    	
        return processResult(UserService.searchUsers(pattern));
    }

}
