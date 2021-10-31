package blockchaine;

import tools.Utils;

public class Operation {
	private byte[] pubkey;
	private byte[] signature;
	private byte[] tagOperation;
	private byte[] time;
	private byte[] hash;
	private Utils util;
	
	public Operation(byte[] receivedOperation) {
		this.util = new Utils();
		
	}
}
