package blockchaine;

import java.util.Arrays;

import tools.Utils;

public class State {

	private byte[] dictateurPubkey;
	private byte[] predecessot_timestamp;
	private byte[] nbBytesInNextSequence;
	private byte[] accountsBytes;
	private Utils util;
	
	public State() {
		util = new Utils();
	}
	
	public void extractState(byte[] receivedMessage) {
		this.dictateurPubkey = Arrays.copyOfRange(receivedMessage,0,32);
		this.predecessot_timestamp = Arrays.copyOfRange(receivedMessage,32,40);
		this.nbBytesInNextSequence = Arrays.copyOfRange(receivedMessage,40,44);
		this.accountsBytes = Arrays.copyOfRange(receivedMessage,44,receivedMessage.length);
		System.out.println("dictat_pubk : "+ util.toHexString(dictateurPubkey));
		System.out.println("predecessot_timestamp : "+ util.toHexString(predecessot_timestamp));
		System.out.println("nbBytesInNextSequence : "+ util.toHexString(nbBytesInNextSequence));
		System.out.println("accountsBytes : "+ util.toHexString(accountsBytes));
	}
	
	public byte[] getAccountsBytes() {
		return this.accountsBytes;
	}
}