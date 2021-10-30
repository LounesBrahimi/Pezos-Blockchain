package tools;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ove.crypto.digest.Blake2b;
import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.*;

public class Utils {
	
	public Utils() {
	}
	
	/*
	 * Recupere un message depuis le serveur
	 * */
	public byte[] getFromSocket(int nbBytesWanted, DataInputStream in, String comment) throws IOException {
		byte[] result = new byte[nbBytesWanted];
		int nbBytesReceived = in.read(result,0,nbBytesWanted); 
		System.out.println("#log#"+(comment==""?"":comment+" ")+"received: "+ nbBytesReceived+ " bytes " + new String(Hex.encodeHex(result)));
		return result;
	}	
	
	/*
	 * Convertie un tableau de caracteres en un tableau de bytess
	 * */
	public byte[] toBytesArray(char[] charArray) throws DecoderException {
		return Hex.decodeHex(charArray);
	}
	
	/*
	 * Convertie un String en un tableau de bytess
	 * */
	static byte[] toBytesArray(String str) throws DecoderException {
		return Hex.decodeHex(str.toCharArray());
	}
	
	/*
	 * Permet d'envoyer un message sous format String vers le serveur
	 * */
	public void sendToSocket(String stringToSend, DataOutputStream out) throws IOException, DecoderException {
		sendToSocket(toBytesArray(stringToSend),out,"");
	}
	
	/*
	 * Permet d'envoyer un tableau de byte vers le serveur
	 * */
	public void sendToSocket(byte[] bytesArrayToSend, DataOutputStream out) throws IOException, DecoderException {
		sendToSocket(bytesArrayToSend,out,"");
	}
	
	public void sendToSocket(String stringToSend, DataOutputStream out, String comment) throws IOException, DecoderException {
		sendToSocket(toBytesArray(stringToSend),out,comment);
	}
	
	public void sendToSocket(byte[] bytesArrayToSend, DataOutputStream out, String comment) throws IOException, DecoderException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(to2BytesArray(bytesArrayToSend.length));
		outputStream.write(bytesArrayToSend);
		bytesArrayToSend = outputStream.toByteArray(); 
		out.write(bytesArrayToSend); 
		out.flush(); // binome !
		System.out.println((comment==""?"":comment+" ")+"sent: "+toHexString(bytesArrayToSend));
	}
	
	/*
	 * Convertue un entier en bytes
	 * */
	public byte[] to2BytesArray(int int2bytes) { 
		ByteBuffer convertedToBytes = ByteBuffer.allocate(2);
		convertedToBytes.putShort((short)int2bytes);
		return convertedToBytes.array();
	}
	
	/*
	 * Convertie un tableau de byte en string hexadecimal
	 * */
	public  String toHexString(byte[] bytes) {
		if(bytes==null || bytes.length==0) return "";
		final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	/*
	 * Crypte en blake2b
	 * */
	public byte[] hash(byte[] valeurToHash, int hashParamNbBytes) {
		Blake2b.Param param = new Blake2b.Param().setDigestLength(hashParamNbBytes);
		final Blake2b blake2b = Blake2b.Digest.newInstance(param);        
		return blake2b.digest(valeurToHash);
	}
	
	/*
	 * Signature en Ed25519
	 * */
	public byte[] signature(byte[] msgToSign, String skString) throws DecoderException, DataLengthException, CryptoException {
		byte[] skBytes = Utils.toBytesArray(skString);
		Ed25519PrivateKeyParameters sk2 = new Ed25519PrivateKeyParameters(skBytes);
		Signer signer = new Ed25519Signer();
		signer.init(true, sk2);
		signer.update(msgToSign, 0, 32);
		byte[] signature = null;
		signature = signer.generateSignature();
		return signature;
	}
	
}
