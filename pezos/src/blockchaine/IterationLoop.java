package blockchaine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import connection.Connection;
import repl.Interaction;
import tools.Utils;
public class IterationLoop {
	private DataOutputStream out;
	private DataInputStream  in;

	public IterationLoop(Connection connection, String pkString, String skString, int temps) throws IOException, DecoderException, InterruptedException, InvalidKeyException, DataLengthException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, CryptoException {
		this.out = connection.getOut();
		this.in  = connection.getIn();
		Utils util = new Utils();
		long timestampLastReceptionBroadcast = 0;
		long secondsBeforeNextbroadcast = 0;
		Block lastBroadcastedBlock = null;
		
		
		util.sendToSocket (util.to2BytesArray(1),out,"tag 1");
		////// 4th message = tag 1
		
		while(true) {
			////// 5th message = block
			byte[] lastBroadcastedBlockAsBytes = util.getFromSocket(174,in,"block"); // 174 bytes = 2 tag + 172 block
			lastBroadcastedBlock = new Block(lastBroadcastedBlockAsBytes);
			System.out.println("#lastBroadcastedBlock#\n"+lastBroadcastedBlock);

			////// verify errors 
			(new Interaction()).verifyErrors(lastBroadcastedBlock,out,in,pkString,skString);
			
			////// timing
			TimeUnit.SECONDS.sleep((temps*60)+2);
		}
	}
}
