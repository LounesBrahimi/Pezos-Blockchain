package blockchaine;

import java.util.ArrayList;
import java.util.Arrays;

public class ListAccounts {

	private ArrayList<Account> listAccounts;
	
	public ListAccounts() {
		listAccounts = new ArrayList<Account>();
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
}
