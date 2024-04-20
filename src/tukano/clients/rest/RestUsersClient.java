package tukano.clients.rest;

import java.net.URI;
import java.util.List;

import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.User;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;

public class RestUsersClient implements Users {

    private final Client client;
    private final WebTarget target;
    private final URI serverURI;


    public RestUsersClient(URI serverURI) {
        this.serverURI = serverURI;
        
        ClientConfig config = new ClientConfig();
        
        this.client = ClientBuilder.newClient(config);
        
        this.target = client.target(serverURI).path(RestUsers.PATH);
    }

    @Override
    public Result<String> createUser(User user) {
    	
        Response response = target.request()
                                  .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        return handleResponse(response, String.class);
        
    }

    @Override
    public Result<User> getUser(String userId, String password) {
    	
        Response response = target.path(userId)
                                  .queryParam(RestUsers.PWD, password)
                                  .request(MediaType.APPLICATION_JSON)
                                  .get();
        return handleResponse(response, User.class);
        
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        Response response = target.path(userId)
                                  .queryParam(RestUsers.PWD, password)
                                  .request(MediaType.APPLICATION_JSON)
                                  .put(Entity.entity(user, MediaType.APPLICATION_JSON));
        return handleResponse(response, User.class);
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        Response response = target.path(userId)
                                  .queryParam(RestUsers.PWD, password)
                                  .request()
                                  .delete();
        return handleResponse(response, User.class);
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        Response response = target.queryParam(RestUsers.QUERY, pattern)
                                  .request(MediaType.APPLICATION_JSON)
                                  .get();
        return handleResponse(response, new GenericType<List<User>>(){});
    }

    private <T> Result<T> handleResponse(Response response, Class<T> responseType) {
        int status = response.getStatus();
        if (status == Status.OK.getStatusCode()) {
            return Result.ok(response.readEntity(responseType));
        } else {
            return Result.error(getErrorCodeFrom(status));
        }
    }

    private <T> Result<T> handleResponse(Response response, GenericType<T> responseType) {
        int status = response.getStatus();
        if (status == Status.OK.getStatusCode()) {
            return Result.ok(response.readEntity(responseType));
        } else {
            return Result.error(getErrorCodeFrom(status));
        }
    }

    public static ErrorCode getErrorCodeFrom(int status) {
        return switch (status) {
            case 200, 209 -> ErrorCode.OK;
            case 409 -> ErrorCode.CONFLICT;
            case 403 -> ErrorCode.FORBIDDEN;
            case 404 -> ErrorCode.NOT_FOUND;
            case 400 -> ErrorCode.BAD_REQUEST;
            case 500 -> ErrorCode.INTERNAL_ERROR;
            case 501 -> ErrorCode.NOT_IMPLEMENTED;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
