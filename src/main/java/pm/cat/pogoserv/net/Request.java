package pm.cat.pogoserv.net;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public abstract class Request {
	
	private final HttpExchange httpExchange;
	private int status = 200;
	
	public Request(HttpExchange e) throws IOException {
		this.httpExchange = e;
	}
	
	public String getMethod(){
		return httpExchange.getRequestMethod();
	}
	
	public String getURI(){
		return httpExchange.getRequestURI().toString();
	}
	
	public String getHeader(String name){
		return httpExchange.getRequestHeaders().getFirst(name);
	}
	
	public void setHeader(String name, String value){
		httpExchange.getResponseHeaders().set(name, value);
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return status;
	}
	
	public boolean hasFailed(){
		return status >= 500 && status < 600;
	}
	
	public abstract void writeReplyTo(OutputStream out) throws IOException;
	
}
