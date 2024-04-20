package tukano.clients.rest;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

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
import tukano.api.java.Blobs;
import tukano.api.rest.RestBlobs;

public class RestBlobsClient implements Blobs {

	@Override
	public Result<Void> upload(String blobId, byte[] bytes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<byte[]> download(String blobId) {
		// TODO Auto-generated method stub
		return null;
    }
}
