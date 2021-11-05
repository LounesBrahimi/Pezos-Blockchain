package state;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;

import tools.Utils;

public class ListAccounts {

	private ArrayList<Account> listAccounts;
	private Utils util;
	
	public ListAccounts() {
		listAccounts = new ArrayList<Account>();
		util = new Utils();
	}
	
	public void extractAllAccounts(byte[] accountsBytes) {
		if (accountsBytes.length >= 52) {
			Account account = new Account();
			account.extractAccount(accountsBytes);
			listAccounts.add(account);
			if (accountsBytes.length > 52) {
				accountsBytes = Arrays.copyOfRange(accountsBytes,52,accountsBytes.length);
				extractAllAccounts(accountsBytes);	
			}
		}
	}
	
	public Account getAccount(String pk) throws DecoderException {
		for(Account account: listAccounts)
		  if(Arrays.equals(account.getUserPubkey(),util.toBytesArray(pk)))
			return account;
		return null;
	  } 
	  
}
