package tukano.clients.rest;

import java.util.function.Supplier;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;

import static tukano.api.java.Result.ErrorCode.INTERNAL_ERROR;
import static tukano.api.java.Result.ErrorCode.TIMEOUT;

public class RestClient {
    private static final int MAX_ATTEMPTS = 10;
    private static final int PAUSE_DURATION = 1000;

    protected <T> Result<T> reTry(Supplier<Result<T>> operation) {
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            try {
                return operation.get();
            } catch (ProcessingException pe) {
                if (attempts >= MAX_ATTEMPTS - 1) break;
                sleep(PAUSE_DURATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                return Result.error(INTERNAL_ERROR);
            }
            attempts++;
        }
        return Result.error(TIMEOUT);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ie.printStackTrace();
        }
    }

    protected <T> Result<T> toJavaResult(Response response, Class<T> resultType) {
        try {
            Status status = response.getStatusInfo().toEnum();
            if (status == Status.OK && response.hasEntity()) {
                return Result.ok(response.readEntity(resultType));
            } else if (status == Status.NO_CONTENT) {
                return Result.ok();
            }
            return Result.error(mapStatusCodeToError(status.getStatusCode()));
        } finally {
            response.close();
        }
    }

    public static ErrorCode mapStatusCodeToError(int statusCode) {
        if (statusCode == 200 || statusCode == 209) {
            return ErrorCode.OK;
        } else if (statusCode == 409) {
            return ErrorCode.CONFLICT;
        } else if (statusCode == 403) {
            return ErrorCode.FORBIDDEN;
        } else if (statusCode == 404) {
            return ErrorCode.NOT_FOUND;
        } else if (statusCode == 400) {
            return ErrorCode.BAD_REQUEST;
        } else if (statusCode == 500) {
            return ErrorCode.INTERNAL_ERROR;
        } else if (statusCode == 501) {
            return ErrorCode.NOT_IMPLEMENTED;
        } else {
            return ErrorCode.INTERNAL_ERROR;
        }
    }
}
