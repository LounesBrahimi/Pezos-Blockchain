import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.codec.DecoderException;

public class MerkleTree implements Serializable {
	MerkleTree left  = null;
	MerkleTree right = null;
	byte[]     hash  = null;
	byte[] 	   wantedHash = null;
	
	//////////// constructors
	public MerkleTree(ArrayList<String> donnees) throws IOException { // Ex 2
		if(donnees.size()==1) {
			this.left   = null;
			this.right  = null;
			this.hash   = Utils.blake2b.digest(donnees.get(0).getBytes()); // Utils.toBytes ?
		}
		else { // size=2^n
			ArrayList<String> partieGauche = new ArrayList<String>();
			partieGauche.addAll(donnees.subList(0,donnees.size()/2)); 
			ArrayList<String> partieDroite = new ArrayList<String>();
			partieDroite.addAll(donnees.subList(donnees.size()/2,donnees.size())); 
			this.left  = new MerkleTree(partieGauche);
			this.right = new MerkleTree(partieDroite);
			this.hash  = Utils.concat_hash(this.left.hash,this.right.hash);
		}
	}
	
	public MerkleTree(MerkleTree left, MerkleTree right, byte[] hash) throws IOException {
		this.left  = left;
		this.right = right;
		this.hash  = hash;
	}
	
	public MerkleTree(byte[] hash) throws IOException, DecoderException { 
		this.left   = null;
		this.right  = null;
		this.hash   = hash; // Arrays.copy?
	}

	public MerkleTree(String str) throws IOException { 
		this.left   = null;
		this.right  = null;
		this.hash   = Utils.blake2b.digest(str.getBytes());
	}


	////////////////////// getters and utils
	public String toString() {
		String rootHash = String.format("%.4s...",Utils.toHexString(this.hash));
		return rootHash + (this.left==null&&this.right==null ? "*  ":("  "+this.left.toString()+this.right.toString()));
	}
	
	public MerkleTree clone() {
		MerkleTree cloneTree = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream ous = new ObjectOutputStream(baos);
			ous.writeObject(this);
			ous.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			cloneTree = (MerkleTree)ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cloneTree;
	}
			

	////////// archive 
	public MerkleTree find(String wantedString) throws DecoderException {
		return find(Utils.toBytesArray(wantedString));
	}
		
	public MerkleTree find(byte[] wantedHash) {
		if(this.left==null || this.right==null) { // a leaf
			if(Arrays.equals(this.hash,wantedHash)) 
				return this;
			else
				return null;
		}
		else {
			MerkleTree findLeftFree  = this.left .find(wantedHash);
			MerkleTree findRightFree = this.right.find(wantedHash);
			if(findLeftFree!=null)
				return findLeftFree;
			else if(findRightFree!=null)
				return findRightFree;
			else
				return null;
		}
	}

	public byte[] witnessAsListNodes(String wantedString) throws IOException { // Ex 4
		return witnessAsListOfNodes(Utils.blake2b.digest(wantedString.getBytes()));
	}
	
	public byte[] witnessAsListOfNodes(byte[] wantedHash) throws IOException { // Ex 4
		if(this.left==null || this.right==null) { 
			// the leaves (of the witness) have been already verified from theirs parent nodes // mais on ne rentre pas dans les feuilles ?
			return wantedHash;
		}
		else if(Arrays.equals(this.left.hash,wantedHash) || Arrays.equals(this.right.hash,wantedHash)) {
			// one of the children == wantedHash
			System.out.printf("%.4s\n",Utils.toHexString(this.left.hash)); ///
			System.out.printf("%.4s\n",Utils.toHexString(this.right.hash)); ///
			System.out.printf("%.4s\n",Utils.toHexString(this.hash));
			return Utils.concat_hash(this.left.hash, this.right.hash);
		}
		else if(this.left.left==null || this.left.right==null || this.right.left==null || this.right.right==null) { 
			// is a parent of a leaf, no child == wantedHash
			return wantedHash;
		}
		else { 
			// isn't leaf, isn't a parent of a leaf
			byte[] potentialNewWantedHashLeft  = this.left.witnessAsListOfNodes(wantedHash);
			if(!Arrays.equals(potentialNewWantedHashLeft,wantedHash)) {
				System.out.printf("%.4s\n",Utils.toHexString(this.hash));
				System.out.printf("%.4s\n",Utils.toHexString(this.right.hash));
				return potentialNewWantedHashLeft;
			}
			byte[] potentialNewWantedHashRight = this.right.witnessAsListOfNodes(wantedHash);
			if(!Arrays.equals(potentialNewWantedHashRight,wantedHash)) {
				System.out.printf("%.4s\n",Utils.toHexString(this.hash));
				System.out.printf("%.4s\n",Utils.toHexString(this.left.hash));
				return potentialNewWantedHashRight;
			}
		}
		return wantedHash; // null?
	}

	public byte[] pathToWantedHash(String wantedString) throws IOException { 
		return pathToWantedHash(Utils.blake2b.digest(wantedString.getBytes()));
	}
	
	public byte[] pathToWantedHash(byte[] wantedHash) throws IOException { 
		if(this.left==null || this.right==null) { 
			// the leaves (of the witness) have been already verified from theirs parent nodes // mais on ne rentre pas dans les feuilles ?
			return wantedHash;
		}
		else if(Arrays.equals(this.left.hash,wantedHash) || Arrays.equals(this.right.hash,wantedHash)) {
			// one of the children == wantedHash
			System.out.printf("%.4s\n",Utils.toHexString(this.left.hash));
			System.out.printf("%.4s\n",Utils.toHexString(this.hash));
			return Utils.concat_hash(this.left.hash, this.right.hash);
		}
		else if(this.left.left==null || this.left.right==null || this.right.left==null || this.right.right==null) { 
			// is a parent of a leaf, no child == wantedHash
			return wantedHash;
		}
		else { 
			// isn't leaf, isn't a parent of a leaf
			byte[] potentialNewWantedHashLeft  = this.left.pathToWantedHash(wantedHash);
			if(!Arrays.equals(potentialNewWantedHashLeft,wantedHash)) {
				System.out.printf("%.4s\n",Utils.toHexString(this.hash));
				return potentialNewWantedHashLeft;
			}
			byte[] potentialNewWantedHashRight = this.right.pathToWantedHash(wantedHash);
			if(!Arrays.equals(potentialNewWantedHashRight,wantedHash)) {
				System.out.printf("%.4s\n",Utils.toHexString(this.hash));
				return potentialNewWantedHashRight;
			}
		}
		return wantedHash; // null ?
	}
}