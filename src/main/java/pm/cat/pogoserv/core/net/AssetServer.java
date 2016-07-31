package pm.cat.pogoserv.core.net;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import pm.cat.pogoserv.util.Util;

// TODO: Some kind of caching or actually a DATABASE
public class AssetServer implements HttpHandler {
	
	private final String path;
	
	public AssetServer(String path){
		this.path = path;
	}
	
	@Override
	public void handle(HttpExchange he) throws IOException {
		byte[] ret = null;
		try{
			ret = Util.readFile(path + he.getRequestURI().getPath());
		}catch(Exception e){
			he.sendResponseHeaders(404, 0);
			he.getResponseBody().close();
			return;
		}
		
		he.sendResponseHeaders(200, ret.length);
		OutputStream out = he.getResponseBody();
		out.write(ret);
		out.close();
	}

}
