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
import tukano.api.Short;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;

public class RestShortsClient implements Shorts {

    private final URI serverURI;
    private final Client client;
    private final WebTarget target;

    public RestShortsClient(URI serverURI) {
        this.serverURI = serverURI;
        ClientConfig config = new ClientConfig();
        this.client = ClientBuilder.newClient(config);
        this.target = client.target(serverURI).path(RestShorts.PATH);
    }

    @Override
    public Result<Short> createShort(String userId, String password) {
        Response response = target.request()
                                  .post(Entity.entity(new Object[]{userId, password}, MediaType.APPLICATION_JSON));
        return handleResponse(response, Short.class);
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        Response response = target.path(shortId)
                                  .queryParam("password", password)
                                  .request()
                                  .delete();
        return handleResponseStatus(response);
    }

    @Override
    public Result<Short> getShort(String shortId) {
        Response response = target.path(shortId)
                                  .request()
                                  .get();
        return handleResponse(response, Short.class);
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        Response response = target.queryParam("userId", userId)
                                  .request()
                                  .get();
        return handleResponse(response, new GenericType<List<String>>() {});
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        Response response = target.path("follow")
                                  .queryParam("userId1", userId1)
                                  .queryParam("userId2", userId2)
                                  .queryParam("isFollowing", isFollowing)
                                  .queryParam("password", password)
                                  .request()
                                  .post(Entity.text(""));
        return handleResponseStatus(response);
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        Response response = target.path("followers")
                                  .queryParam("userId", userId)
                                  .queryParam("password", password)
                                  .request()
                                  .get();
        return handleResponse(response, new GenericType<List<String>>() {});
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        Response response = target.path(shortId)
                                  .queryParam("userId", userId)
                                  .queryParam("isLiked", isLiked)
                                  .queryParam("password", password)
                                  .request()
                                  .post(Entity.text(""));
        return handleResponseStatus(response);
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        Response response = target.path(shortId)
                                  .queryParam("password", password)
                                  .request()
                                  .get();
        return handleResponse(response, new GenericType<List<String>>() {});
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        Response response = target.path(userId).path("feed")
                                  .queryParam("userId", userId)
                                  .queryParam("password", password)
                                  .request()
                                  .get();
        return handleResponse(response, new GenericType<List<String>>() {});
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

    private Result<Void> handleResponseStatus(Response response) {
        int status = response.getStatus();
        if (status == Status.OK.getStatusCode()) {
            return Result.ok(null);
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
