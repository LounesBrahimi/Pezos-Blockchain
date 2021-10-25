import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

public class Connection {
	Socket socket;
	public DataOutputStream out;
	public DataInputStream  in;
	
	// блокирующее чтение в пакете java.io.Reader, InputStream, тред останавливается, пока не получит данные
	// inputStream базовый класс для чтения байтов, определение конца потока, блокирует
	// DataInputStream разновидность InputStream, считывать примитивные типы данных независимым от машины способом
	// BufferedInputStream разновидность inputStream, использует буфер для оптимизации скорости доступа к данным. данные в основном считываются заранее, и это сокращает доступ к диску или сети

	// Неблокирующий java.nio асинхронное чтение, буферизация = отличие неблокирующего чтения, Буфферы = Временные хранилища фиксированного размера для транспортируемых данных
	// читать-писать голые байты не так эффективно => потоки можно обернуть в классы адаптеры, буферизированные или нет:
	// BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // BufferedWriter // in.readLine размер буфера 8192
	// InputStreamReader reader;  //  блокирующий вызов, поток останавливается пока данные не станут доступными
	// ByteBuffer очистить данные из потока с помощью flush()
	// to hold the lock on the object = wait = call it within a synchronized block
	// wait() releases the monitor, then reacquire it before returning
	// owner of the objects monitor = is in a synchronized block using that object
	// NOT use a Thread object's monitor for synchronization, waiting, notifying - code within Thread already does that => create a separate object for the purpose of synchronization/wait/notify.
	/* java.lang.IllegalMonitorStateException: current thread is not owner
	BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	bufferedreader.wait();
	String fromServer = bufferedreader.readLine(); */

	public Connection(String hostname, int port, String sk, String pk) throws UnknownHostException, IOException, InterruptedException, DecoderException {
    	
    	try (Socket socket = new Socket(hostname, port)) {// ensures that each resource is closed at the end ?
    		this.socket = socket;
    		this.in     = new DataInputStream (new BufferedInputStream (socket.getInputStream ())); // buffered ?
    		this.out    = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    		// setKeepAlive() на сокете ? // setSoTimeoutе ?
    		
    		byte[] seed = new byte[24];
    		this.in.read(seed, 0, 24);
    		System.out.println("Seed received: " + new String(Hex.encodeHex(seed))); 
    		byte[] myseed = Hex.decodeHex("12230025a6118122e9eac8785c74193819441fe57fec4845".toCharArray());
    		
    		byte[] pubkeyBytes;
			pubkeyBytes = Hex.decodeHex(pk.toCharArray()); 
			byte[] privkeyBytes = Hex.decodeHex(sk.toCharArray()); 
			System.out.println("on envoie : "+Utils.addLength16bits(pubkeyBytes));
    		out.write(Utils.addLength16bits(pubkeyBytes)); 
			System.out.println("on envoie : "+Utils.hash(seed,32));
			System.out.println("seed hash : "+ Hex.encodeHex(Utils.hash(myseed,32)));

			//----------------------
//			Signature sig = Signature.getInstance("Ed25519");
			Ed25519PrivateKeyParameters sk2 = new Ed25519PrivateKeyParameters(privkeyBytes);
	        Signer signer = new Ed25519Signer();
	        signer.init(true, sk2);
	        signer.update(Utils.hash(myseed,32), 0, 32);
	        byte[] signature = null;
	        try {
	        signature = signer.generateSignature();
	        } catch (Exception e) {
	        	
	        }
	        String hexsignature = new String(Hex.encodeHex(signature)); 
	        System.out.println("signature = "+hexsignature);
//			sig.initSign(null);
//		    sig.update(Utils.hash(seed,32));
//		    byte[] s = sig.sign();
			//----------------------
			
//    		out.write(signature(Utils.hash(seed,32)));
			System.out.println("1) socket()="+socket.toString()+",socket.isConnected()="+socket.isConnected());

			/////////////////////////////////////
			byte[] msg= {(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x01};
    		out.write(msg);

			byte[] blockAsHex = new byte[176]; // 172 const ? 
			this.in.read(blockAsHex,0,176); // Reads some number of bytes and stores them into the buffer array, returns the number of bytes read, blocks until input data is available/EOF/exception
			System.out.println("blockAsHex    = "+blockAsHex);
			System.out.println("blockAsString = "+new String(Hex.encodeHex(blockAsHex)));			 

			System.out.println("2) socket()="+socket.toString()+",socket.isConnected()="+socket.isConnected());
			// sock.setSoTimeout(millis) вызвать sock.read(...) и ждать не более определенного времени, прежде чем он вызовет исключение 
    	} catch (UnknownHostException ex) {
    		System.out.println("Server not found: " + ex.getMessage());
    	} catch (IOException ex) {
    		System.out.println("I/O error: " + ex.getMessage());
    	}
		System.out.println("3) socket()="+socket.toString()+",socket.isConnected()="+socket.isConnected());
		
   		/*try {// ensures that each resource is closed at the end ?
   			// InputStreamReader reader = new InputStreamReader(input);  //  блокирующий вызов, поток останавливается пока данные не станут доступными
   			// InputStream  in  = socket.getInputStream();
   			// OutputStream out = socket.getOutputStream();
   			PrintStream out = new PrintStream(socket.getOutputStream());
   			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));*/
	}

    public void close() {
    	try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException ex) {
	        System.out.println("I/O error: " + ex.getMessage());
	    }
    }   

    /////////////// getters 
    public DataOutputStream out() {
    	return out;	
    }
 
    public DataInputStream  in() {
    	return in;	
    }
}