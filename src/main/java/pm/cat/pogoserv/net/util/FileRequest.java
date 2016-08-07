package pm.cat.pogoserv.net.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.Files;
import com.sun.net.httpserver.HttpExchange;

import pm.cat.pogoserv.net.Request;

public class FileRequest extends Request {
	
	private final File file;
	
	public FileRequest(HttpExchange e, File file) throws IOException {
		super(e);
		this.file = file;
	}

	@Override
	public void writeReplyTo(OutputStream out) throws IOException {
		if(getStatus() == 200)
			Files.copy(file, out);
	}

}
