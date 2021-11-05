package repl;

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

import blockchaine.Block;
import connection.Connection;
import tools.Utils;

/*
 * class Constituant le broadcast automatique
 * */
public class IterationLoop {
	private DataOutputStream out;
	private DataInputStream  in;

	public IterationLoop(Connection connection, String pkString, String skString, int temps) throws IOException, DecoderException, InterruptedException, InvalidKeyException, DataLengthException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, CryptoException {
		this.out = connection.getOut();
		this.in  = connection.getIn();
		Utils util = new Utils();
		Block lastBroadcastedBlock = null;
		
		// Envoie le tag 1 pour récupérer le block courants
		util.sendToSocket (util.to2BytesArray(1),out,"tag 1");
		
		while(true) {
			
			//Construction de l'objet block
			byte[] lastBroadcastedBlockAsBytes = util.getFromSocket(174,in,"block");
			
			lastBroadcastedBlock = new Block(lastBroadcastedBlockAsBytes);
			System.out.println("=======Block courant : =======\n"+lastBroadcastedBlock+"\n================");

			// détéction de l'erreur et envoie du tag 9 pour la correction 
			(new Interaction()).verifyErrors(lastBroadcastedBlock,out,in,pkString,skString);
			
			// sleep selon le timing chosie
			TimeUnit.SECONDS.sleep(temps+2);
		}
	}
}
