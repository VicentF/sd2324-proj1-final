package tukano.servers.java;

import java.util.function.Consumer;
import java.util.logging.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Blobs;

public class JavaBlobs implements Blobs {

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
