package pm.cat.pogoserv.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.util.Filter;

public class Server {
	
	private static final byte[] EMPTY_RESPONSE = new byte[0];
	
	private final HttpServer serv;
	private int newRpcHandlerIdx = 1;
	
	public Server(Executor e) throws IOException {
		serv = HttpServer.create();
		serv.setExecutor(e);
	}
	
	public void bind(int port) throws IOException{
		bind(new InetSocketAddress("localhost", port));
	}
	
	public void bind(InetSocketAddress addr) throws IOException{
		Log.d("Server", "Binding to " + addr);
		serv.bind(addr, 3);
	}
	
	public void setExecutor(Executor e){
		serv.setExecutor(e);
	}
	
	public void setRpcAllocator(Filter<HttpRequest> f){
		removeContext("/plfe/rpc");
		serv.createContext("/plfe/rpc", new HttpRequestHandler(f));
	}
	
	public int addRpcHandler(Filter<HttpRequest> f){
		int idx = newRpcHandlerIdx++;
		serv.createContext("/plfe/"+idx+"/rpc", new HttpRequestHandler(f));
		return idx;
	}
	
	public void createContext(String context, HttpHandler handler){
		serv.createContext(context, handler);
	}
	
	public void removeContext(String context){
		// Ugly way to remove context, but the retarded
		// api doesn't have a getContext() or even a contextExists()
		// method.
		try{
			serv.removeContext(context);
		}catch(Exception ignored){ }
	}
	
	public void listen(){
		Log.d("Server", "Listening now");
		serv.start();
	}
	
	public void stop(){
		Log.d("Server", "Stoppings");
	}
	
	private class HttpRequestHandler implements HttpHandler {
		
		private final Filter<HttpRequest> f;
		
		HttpRequestHandler(Filter<HttpRequest> f){
			this.f = f;
		}
		
		@Override
		public void handle(HttpExchange he) throws IOException {
			HttpRequest re = HttpRequest.parse(Server.this, he);
			byte[] resp;
			try{
				f.run(re);
				resp = re.toByteArray();
			}catch(Exception e){
				Log.e("Server", "Uncaught exception in filter " + f);
				Log.e("Server", e);
				re.status = 500;
				resp = EMPTY_RESPONSE;
			}
			re.writeResponseHeaders(he.getResponseHeaders());
			Log.d("Server", "HTTP: Status: %d, Proto status: %d, reply length: %d", re.status, re.protoStatus, resp.length);
			he.sendResponseHeaders(re.status, resp.length);
			OutputStream out = he.getResponseBody();
			out.write(resp);
			out.close();
		}
		
	}
	
}
