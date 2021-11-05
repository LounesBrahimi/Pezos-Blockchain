package operations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import tools.Utils;

/*
 * class permettant de hasher la liste des operations
 * */
public class HachOfOperations {
	
	// liste des operations communiquee de la part du serveur
	private ArrayList<Operation> operations;
	private Utils util;
	
	public HachOfOperations() {
		this.operations = null;
		util = new Utils();
	}
	
	public HachOfOperations(ArrayList<Operation> operations) {
		this.operations = operations;
		util = new Utils();
	}
	
	/*
	 * Methode permettant de hasher la liste des operation et de renvoyer le resultat sous forme
	 * d'un tableau de bytes
	 * */
	public byte[] ops_hash() throws IOException {
		if (this.operations.size() == 0) {
			return util.Bytes32s();
		} else if (this.operations.size() == 1) {
			return this.util.hash(operations.get(0).getContent(), 32);
		} else {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] tmpHash = this.util.hash(operations.get(operations.size()-1).getContent(), 32);
			operations.remove(operations.size()-1);
			outputStream.write(ops_hash());
			outputStream.write(tmpHash);
			return this.util.hash(outputStream.toByteArray(),32);
		}
	}
}
