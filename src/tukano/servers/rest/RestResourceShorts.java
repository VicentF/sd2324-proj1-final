package tukano.servers.rest;

import java.util.List;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import tukano.servers.java.JavaShorts;

@Singleton
public class RestResourceShorts implements RestShorts {
    private final Shorts shortService;

    public RestResourceShorts() {
        this.shortService = new JavaShorts();
    }

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
    public Short createShort(String userId, String password) {
        return processResult(shortService.createShort(userId, password));
    }

    @Override
    public Void deleteShort(String shortId, String password) {
        return processResult(shortService.deleteShort(shortId, password));
    }

    @Override
    public Short getShort(String shortId) {
        return processResult(shortService.getShort(shortId));
    }

    @Override
    public List<String> getShorts(String userId) {
        return processResult(shortService.getShorts(userId));
    }

    @Override
    public Void follow(String userId1, String userId2, boolean isFollowing, String password) {
        return processResult(shortService.follow(userId1, userId2, isFollowing, password));
    }

    @Override
    public List<String> followers(String userId, String password) {
        return processResult(shortService.followers(userId, password));
    } 

    @Override
    public Void like(String shortId, String userId, boolean isLiked, String password) {
        return processResult(shortService.like(shortId, userId, isLiked, password));
    }

    @Override
    public List<String> likes(String shortId, String password) {
        return processResult(shortService.likes(shortId, password));
    }

    @Override
    public List<String> getFeed(String userId, String password) {
        return processResult(shortService.getFeed(userId, password));
    }
}
