import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;

public class IterationLoop {
	DataOutputStream out = null;
	DataInputStream  in  = null;
	private static final int GET_CURRENT_HEAD     = 1;
	private static final int CURRENT_HEAD         = 2;
	private static final int GET_BLOCK            = 3;
	private static final int BLOCK                = 4;
	private static final int GET_BLOCK_OPERATIONS = 5;
	private static final int BLOCK_OPERATIONS     = 6;
	private static final int GET_BLOCK_STATE      = 7;
	private static final int BLOCK_STATE          = 8;
	private static final int INJECT_OPERATION     = 9;
	
	  
	public IterationLoop(Connection connection) throws IOException, DecoderException, InterruptedException {
		this.out = connection.out;
		this.in  = connection.in;
		Block block;
		
		while(true) {
			////// 4th message = tag 1 GET_CURRENT_HEAD
			Utils.sendToSocket(GET_CURRENT_HEAD,out,"GET_CURRENT_HEAD");

			////// 5th message = block
			System.out.println(block = new Block(Utils.getFromSocket(174,in,"block")));
			
			TimeUnit.MINUTES.sleep(10);
		}
	}
}
