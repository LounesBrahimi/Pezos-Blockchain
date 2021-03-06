package state;

import java.util.Arrays;

import tools.Utils;

/*
 * Class representant un compte dans la blockchaines
 * */
public class Account {
	
	private byte[] userPubkey;
	private byte[] predPez;
	private byte[] timestampPez;
	private byte[] operationsHashPez;
	private byte[] contextHashPez;
	private byte[] signaturePez;
	private Utils util;
	
	public Account(){
		util = new Utils();
	}
	
	public void extractAccount(byte[] accountsBytes) {
		this.userPubkey = Arrays.copyOfRange(accountsBytes,0,32);
		this.predPez = Arrays.copyOfRange(accountsBytes,32,36);
		this.timestampPez = Arrays.copyOfRange(accountsBytes,36,40);
		this.operationsHashPez = Arrays.copyOfRange(accountsBytes,40,44);
		this.contextHashPez = Arrays.copyOfRange(accountsBytes,44,48);
		this.signaturePez = Arrays.copyOfRange(accountsBytes,48,52);
	}

	public String toString() {
		return "### Account ###"+
			 "\n  userPubkey : "        + util.toHexString(userPubkey) +
			 "\n  predPez : "           + util.toHexString(predPez)    +
			 "\n  timestampPez : "      + util.toHexString(timestampPez) +
			 "\n  operationsHashPez : " + util.toHexString(operationsHashPez) +
			 "\n  contextHashPez : "    + util.toHexString(contextHashPez) +
			 "\n  signaturePez : "      + util.toHexString(signaturePez);
	  }

	public byte[] getUserPubkey() {
		return userPubkey;
	}
}
