package pm.cat.pogoserv.net.util;

import java.io.File;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.net.Request;
import pm.cat.pogoserv.net.Service;

public class FileService implements Service {
	
	private final String root;
	
	public FileService(String root){
		this.root = root;
	}
	
	@Override
	public Request process(HttpExchange he) throws IOException {
		File file = new File(root + "/" + he.getRequestURI());
		FileRequest ret = new FileRequest(he, file);
		if(file.exists())
			Log.d("FileService", "Serving file: %s", file);
		else{
			Log.w("FileService", "Requested file doesn't exist: %s", file);
			ret.setStatus(404);
		}
		
		return ret;
	}
	
}
