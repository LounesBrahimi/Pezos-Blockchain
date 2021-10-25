// Les clés sont encodés en hexadécimal et doivent être décodés vers des bytes.

import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.codec.DecoderException;

public class TPAE_PROJET {

	static String hostname = "78.194.168.67";
	static int port = 1337;
	static String pk = "b8b606dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21";
	//static String pubkey = "aaaaa6dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21";
	static String sk = "1f06949f1278fcbc0590991180d5b567d240c0b0576d1d34cad66db49d4eea4a";

	public static void main(String[] args) throws DecoderException, ParseException, IOException, InterruptedException {
		//Block testBlock = new Block(44,"1c80203a30e5de4d980cc555131d1b4a4750edc82c0c443179d88de1ae4f6cdf","2021-10-10 15:21:09","0000000000000000000000000000000000000000000000000000000000000000","22a00d1c8c0fbaefedd71ddb83d455033efd259a8f0adf189b9f850a0d1945f2","cc3faffc696c86db13d50752fdb7edd0ee1ce19ab350f60899939fc139d58996419c13b812b7f005fafaf23924d2f1df555036bc61e7b67cb679375e5756b306");
		//System.out.println(testBlock+"\n");

		Connection connection = new Connection(hostname,port,sk,pk);
		System.out.println("3) socket()="+connection.socket.toString()+",socket.isConnected()="+connection.socket.isConnected()+" // main");
	
		// new IterationLoop(connection); // ne marche pas
	}
}