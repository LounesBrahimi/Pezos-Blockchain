package pezos;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import blockchaine.IterationLoop;
import connection.Connection;

public class Main {

	public static void main(String[] args) throws DataLengthException, UnknownHostException, IOException, DecoderException, CryptoException, InterruptedException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
		String hostname = "78.194.168.67";
		int    port     = 1337;
		String pk       = "b8b606dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21"; // public
		String sk       = "1f06949f1278fcbc0590991180d5b567d240c0b0576d1d34cad66db49d4eea4a"; // secret
	
		Connection connection = new Connection(hostname,port,pk,sk);
		new IterationLoop(connection, pk, sk);
	}
}