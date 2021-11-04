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
		byte[] timestampLastReceivedBroadcast = null;
		
		while(true) {
			////// 4th message = tag 1
			util.sendToSocket (util.to2BytesArray(1),out,"tag 1");

			////// 5th message = block
			byte[] broadcastedBlockAsBytes = util.getFromSocket(174,in,"block"); // receivedMessage = 174 bytes = 2 tag + 172 block, no size 2 bytes
			Block blockAsObjet = new Block(broadcastedBlockAsBytes);
			//timestampLastReceivedBroadcast = ;

			System.out.println("#### ici 2 ######");
			System.out.println("#print the block in loop#\n"+blockAsObjet);
			System.out.println("#### ici 3s ######");
			
			TimeUnit.MINUTES.sleep(1);
		}
	}
}
