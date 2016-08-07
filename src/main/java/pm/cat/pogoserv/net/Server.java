package pm.cat.pogoserv.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.zip.GZIPOutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import pm.cat.pogoserv.Config;
import pm.cat.pogoserv.Log;

public class Server {
	
	private final HttpServer serv;
	private final LinkedList<ServiceWrapper> services = new LinkedList<>();
	
	public Server(Executor e) throws IOException {
		serv = HttpServer.create();
		serv.setExecutor(e);
	}
	
	public void bind(int port) throws IOException{
		bind(new InetSocketAddress("localhost", port));
	}
	
	public void bind(InetSocketAddress addr) throws IOException{
		Log.d("Server", "Binding to " + addr);
		serv.bind(addr, Config.SERVER_BACKLOG);
	}
	
	public void setExecutor(Executor e){
		serv.setExecutor(e);
	}
	
	public <T extends Request> void addService(String context, Service service){
		ServiceWrapper w = new ServiceWrapper(service);
		serv.createContext(context, w);
		synchronized(services){
			services.add(w);
		}
	}
	
	public void listen(){
		Log.d("Server", "Listening now");
		serv.start();
	}
	
	public void stop(){
		Log.d("Server", "Stoppings");
		serv.stop(0);
		while(!services.isEmpty()){
			ServiceWrapper w = services.removeFirst();
			Log.d("Server", "Shutting down %s", w.service);
			w.service.shutdown();
		}
	}
	
	private class ServiceWrapper implements HttpHandler {
		
		private final Service service;
		
		ServiceWrapper(Service service){
			this.service = service;
		}
		
		@Override
		public void handle(HttpExchange he) throws IOException {
			Log.d("Server", "[<<] %s (%s)", he.getRemoteAddress(), service);
			
			Request r = null;
			try{
				r = service.process(he);
			}catch(Exception e){
				Log.e("Server", "Uncaught exception in service: %s", service);
				Log.e("Server", e);
			}

			
			if(r == null || r.hasFailed()){
				Log.e("Server", "Internal server error in %s/%s", service, r);
				he.sendResponseHeaders(r == null ? 500 : r.getStatus(), 0);
				he.getResponseBody().close();
				return;
			}
			
			int status = r.getStatus();
			r.setHeader("Server", Config.SERVER);

			String acceptEncoding = r.getHeader("Accept-Encoding");
			boolean useGzip = Config.HTTP_USE_GZIP && acceptEncoding != null && acceptEncoding.contains("gzip");
			if(useGzip)
				r.setHeader("Content-Encoding", "gzip");
			
			OutputStream out = null;
			try{
				if(Config.HTTP_USE_CHUNKED){
					he.sendResponseHeaders(status, 0);
					out = he.getResponseBody();
					if(useGzip)
						out = new GZIPOutputStream(out);
					r.writeReplyTo(out);
				}else{
					ByteArrayOutputStream tmp = new ByteArrayOutputStream(256);
					OutputStream dest = useGzip ? new GZIPOutputStream(tmp) : tmp;
					r.writeReplyTo(dest);
					he.sendResponseHeaders(status, tmp.size());
					out = he.getResponseBody();
					tmp.writeTo(out);
					dest.close();
				}
			}catch(Exception e){
				Log.e("Server", "Error while writing response %s/%s", service, r);
				Log.e("Server", e);
				return;
			}finally{
				if(out != null){
					out.close();
				}
			}
			
			Log.d("Server", "[>>] %s: %d OK %s/%s: [%s %s]", 
					he.getRemoteAddress(), status, service, r, useGzip?"gzip":"", Config.HTTP_USE_CHUNKED?"chunked":"");
			
		}
		
	}
	
}
