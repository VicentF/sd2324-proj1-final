package tukano.servers.rest;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.rest.RestBlobs;
import tukano.servers.java.JavaBlobs;

@Singleton
public class RestResourceBlobs implements RestBlobs {
    private final Blobs blobService;

    public RestResourceBlobs() {
        this.blobService = new JavaBlobs();
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
    public void upload(String blobId, byte[] bytes) {
        processResult(blobService.upload(blobId, bytes));
    }

    @Override
    public byte[] download(String blobId) {
        return processResult(blobService.download(blobId));
    }
}
