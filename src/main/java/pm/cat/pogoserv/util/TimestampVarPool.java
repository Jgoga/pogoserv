package pm.cat.pogoserv.util;

// No need to check for empty list when removing nodes
// list should never be empty
// TODO: maybe preallocate head+tail?
public class TimestampVarPool {
	
	private TSNode<?> head, tail;
	private long lastValidTime = 0;
	
	public <T> TSNode<T> allocate(T t){
		TSNode<T> ret = new TSNode<>(t);
		synchronized(this){
			if(head == null){
				head = tail = ret;
			}else{
				appendTail(ret);
			}
		}
		return ret;
	}
	
	private void appendTail(TSNode<?> node){
		tail.next = node;
		node.prev = tail;
		tail = node;
		node.next = null;
	}
	
	public TSNode<?> getHead(){
		return head;
	}
	
	public TSNode<?> getTail(){
		return tail;
	}
	
	public class TSNode<T> {
		private TSNode<?> prev, next;
		private long timestamp = 0;
		private T t;
		private boolean deleted = false;
		
		private TSNode(T t){
			this.t = t;
		}
		
		public T write(){
			timestamp = System.currentTimeMillis();
			if(this != tail){
				synchronized(TimestampVarPool.this){
					if(prev != null)
						prev.next = next;
					next.prev = prev;
					appendTail(this);
				}
			}
			return t;
		}
		
		public T read(){
			return t;
		}
		
		public void delete(){
			deleted = true;
		}
		
		public TSNode<T> deleteAndSet(T t){
			delete();
			this.t = t;
			return this;
		}
		
		public TSNode<?> getNext(){
			return next;
		}
		
		public TSNode<?> getPrev(){
			return prev;
		}
		
		public long getTimestamp(){
			return timestamp;
		}
		
		public boolean isDeleted(){
			return deleted;
		}
		
	}
	
}
