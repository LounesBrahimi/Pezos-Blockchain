package state;

import java.util.Arrays;

import org.apache.commons.codec.DecoderException;

import tools.Utils;

public class State {

	private byte[] stateData;
	private byte[] tag;
	private byte[] dictateurPubkey;
	private byte[] predecessor_timestamp;
	private byte[] nbBytesInNextSequence;
	private byte[] accountsBytes;
	private Utils util;
	
	public byte[] getDictPK() {
		return this.dictateurPubkey;
	}
	
	public State() {
		util = new Utils();
	}
	
	public void extractState(byte[] receivedMessage) {
		System.out.println("****************** extractState("+util.toHexString(receivedMessage)+")");
		this.tag = Arrays.copyOfRange(receivedMessage,0,2);
		this.dictateurPubkey = Arrays.copyOfRange(receivedMessage,2,34);
		this.predecessor_timestamp = Arrays.copyOfRange(receivedMessage,34,42);
		this.nbBytesInNextSequence = Arrays.copyOfRange(receivedMessage,42,46);
		this.accountsBytes = Arrays.copyOfRange(receivedMessage,46,receivedMessage.length);
		stateData = Arrays.copyOfRange(receivedMessage,2,receivedMessage.length);
		System.out.println("dictat_pubk : "+ util.toHexString(dictateurPubkey));
		System.out.println("predecessor_timestamp : "+ util.toHexString(predecessor_timestamp));
		//System.out.println("nbBytesInNextSequence : "+ util.toHexString(nbBytesInNextSequence));
		//System.out.println("accountsBytes : "+ util.toHexString(accountsBytes));
	}
	
	public byte[] hashTheState() {
		return util.hash(this.stateData, 32);
	}
	
	public byte[] getAccountsBytes() {
		return this.accountsBytes;
	}

	public byte[] getPredecessorTimestamp(){
		return this.predecessor_timestamp;
	}
	
	public Account getAccount(String pk) throws DecoderException {
		ListAccounts listAccounts = new ListAccounts();
		listAccounts.extractAllAccounts(this.accountsBytes);
		return listAccounts.getAccount(pk);
	}
}