package blockchaine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;

import connection.Connection;
import tools.Utils;
public class IterationLoop {
	private DataOutputStream out;
	private DataInputStream  in;

	public IterationLoop(Connection connection) throws IOException, DecoderException, InterruptedException {
		this.out = connection.getOut();
		this.in  = connection.getIn();
		Utils util = new Utils();
		
		while(true) {
			////// 4th message = tag 1
			byte[] msg = { (byte)0x00, (byte)0x01 };
			msg = util.to2BytesArray(1);
			util.sendToSocket (msg,out,"tag 1");

			////// 5th message = block
			byte[] blockAsBytes = util.getFromSocket(174,in,"block"); // receivedMessage = 174 bytes = 2 tag + 172 block, no size 2 bytes
			Block blockAsObjet = new Block(blockAsBytes);
			System.out.println(blockAsObjet);
			
			TimeUnit.MINUTES.sleep(10);
		}
	}
}
