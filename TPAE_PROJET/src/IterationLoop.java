import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.codec.DecoderException;

public class IterationLoop {
	DataOutputStream out = null;
	DataInputStream  in  = null;

	public IterationLoop(Connection connection) throws IOException, DecoderException {
		this.out = connection.out;
		this.in  = connection.in;

		////// 4th message = tag 1
		byte[] msg = { (byte)0x00, (byte)0x01 };
		Utils.sendToSocket (msg,out,"tag 1");

		////// 5th message = block
		byte[] blockAsBytes = Utils.getFromSocket(174,in,"block"); // // receivedMessage = 174 bytes = 2 tag + 172 block, pas de 2 bytes
		Block blockAsObjet = new Block(blockAsBytes);
		System.out.println(blockAsObjet);
	}
}
