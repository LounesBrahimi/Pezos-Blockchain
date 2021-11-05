package pezos;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import connection.Connection;
import repl.IterationLoop;
/*
 * Class principale contenant le main qui est le point de depart de l'application
 * */
public class Main {

	public static void main(String[] args) throws DataLengthException, UnknownHostException, IOException, DecoderException, CryptoException, InterruptedException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
		String hostname = "78.194.168.67";
		int    port     = 1337;
		String pk       = "b8b606dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21"; // public
		String sk       = "1f06949f1278fcbc0590991180d5b567d240c0b0576d1d34cad66db49d4eea4a"; // secret
		
		Connection connection = new Connection(hostname,port,pk,sk);
		Scanner myObj = new Scanner(System.in);
			System.out.println("Quel mode voulez-vous lancer? \n 1 - Broadcast Auto \n 2 - Manuel ");
		    int choix = 0;
		while(choix != 1 && choix != 2){
		choix = myObj.nextInt();
		
		if(choix == 1){
			Scanner myObj2 = new Scanner(System.in);
			System.out.println("Quel est la durée \"en secondes\" d'attente pour un nouveau bloc ?");
			int temps = 0;
			temps = myObj2.nextInt();
			new IterationLoop(connection, pk, sk, temps);
		}
		if(choix == 2){
			connection.manualInteraction(pk, sk);
		} else {
			System.out.println("mauvais choix, recommencez ");
		}
	}
		connection.closeConnection();
	}
}