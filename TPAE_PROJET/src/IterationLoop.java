import java.io.IOException;

public class IterationLoop {
	Connection connection;
	public IterationLoop(Connection connection ) {
		this.connection=connection;  
		System.out.println("connection.socket.isConnected()="+connection.socket.isConnected());
		byte[] blockAsString = new byte[280]; // 280 const ?
		try {
			connection.in.read(blockAsString,0,280); 
		} catch (IOException ex) {
	        System.out.println("I/O error: " + ex.getMessage());
		} 
		System.out.println("blockAsString = "+blockAsString );
	}
	
}
