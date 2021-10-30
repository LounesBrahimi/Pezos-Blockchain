import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;

public class IterationLoop {
	DataInputStream  in  = null;
	DataOutputStream out = null;

	public IterationLoop(Connection connection) throws IOException, DecoderException, InterruptedException {
		this.in  = connection.in;
		this.out = connection.out;
		
		while(true) {
			Block block = Utils.getCurrentBlockFromSocket(in,out);
			System.out.println(block);
			block.verify(in,out);
			TimeUnit.MINUTES.sleep(10);
		}
	}
}
