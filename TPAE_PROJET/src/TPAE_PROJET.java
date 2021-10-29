// fermer la connection ?
// Les clés sont encodés en hexadécimal et doivent être décodés vers des bytes.
// TCP keepalives ?
// un seul hash dans l'arbre?

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

public class TPAE_PROJET {
	static String hostname = "78.194.168.67";
	static int    port     = 1337;
	static String pk       = "b8b606dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21"; // public
	static String sk       = "1f06949f1278fcbc0590991180d5b567d240c0b0576d1d34cad66db49d4eea4a"; // secret

	public static void main(String[] args) throws DecoderException, ParseException, IOException, InterruptedException, DataLengthException, CryptoException {
		//Block testBlock = new Block(44,"1c80203a30e5de4d980cc555131d1b4a4750edc82c0c443179d88de1ae4f6cdf","2021-10-10 15:21:09","0000000000000000000000000000000000000000000000000000000000000000","22a00d1c8c0fbaefedd71ddb83d455033efd259a8f0adf189b9f850a0d1945f2","cc3faffc696c86db13d50752fdb7edd0ee1ce19ab350f60899939fc139d58996419c13b812b7f005fafaf23924d2f1df555036bc61e7b67cb679375e5756b306");
		//System.out.println(testBlock+"\n");
		
		ArrayList<String> donnees = new ArrayList<String>(); // size = 2^n
		donnees.add("a");
		donnees.add("b");
		donnees.add("c");
		donnees.add("d");
		donnees.add("e");
		donnees.add("f");
		donnees.add("g");
		donnees.add("h");
		MerkleTree tree = new MerkleTree(donnees);
		System.out.println("INITIAL TREE : "+tree);
		System.out.println("WINTESS TREE : "+Utils.witness("a",tree));
		
		//Connection connection = new Connection(hostname,port,sk,pk);
		//new IterationLoop(connection); 
	}
}