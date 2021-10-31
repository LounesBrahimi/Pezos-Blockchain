package blockchaine;

import java.nio.ByteBuffer;
import java.util.Arrays;

import tools.Utils;

public class Operation {
	private byte[] pubkey;
	private byte[] signature;
	private byte[] tagOperation;
	private long time;
	private byte[] hash;
	private Utils util;
	
	public int typeOfTag() {
		return this.tagOperation[1] ;
	}
	
	public Operation(byte[] receivedOperation) {
		this.util = new Utils();
		this.tagOperation = Arrays.copyOfRange(receivedOperation,4,6);
		this.pubkey = Arrays.copyOfRange(receivedOperation,6,38);
		this.signature = Arrays.copyOfRange(receivedOperation,38,102);
		if ((typeOfTag() == 1) || (typeOfTag() == 3) || (typeOfTag() == 4)) {
			this.hash = Arrays.copyOfRange(receivedOperation,102,134);
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
			System.out.println("hash : "+ util.toHexString(this.hash));
		} else if (typeOfTag() == 2) {
			this.time = util.toLong(Arrays.copyOfRange(receivedOperation,102,110));
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
			System.out.println("time : "+this.time+" seconds");
		} else if (typeOfTag() == 5) {
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
		}
	}
}
