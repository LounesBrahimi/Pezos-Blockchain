// Exemple de signature:
//seed: 12230025a6118122e9eac8785c74193819441fe57fec4845
//seed's hash: 747ef500f4188f015695dccf69ca668903982cf7196dcc22e149bdd745d9a513
//seed's hash signature: db7815524e36aeb3f85f403a1043b0df8cc96f799b6d364e50135b813163d0c68c1047ed94b87f9a163aa0714853b50153334f8bf65602468b5dbacb8828c700

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

public class Connection {
	Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream  in  = null;

	public Connection(String hostname, int port, String skString, String pkString)
		throws UnknownHostException, IOException, InterruptedException, DecoderException, DataLengthException, CryptoException {

		try (Socket socket = new Socket(hostname, port)) { 
			this.socket     = socket;
			this.in         = new DataInputStream (new BufferedInputStream (socket.getInputStream())); // buffered ????
			this.out        = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			System.out.println("socket.getInetAddress().isReachable(10000) = "+socket.getInetAddress().isReachable(10000));

			////// 1st message = seed
			byte[] seed = Utils.getFromSocket(24,this.in,"seed"); 
			//seed = Utils.toBytesArray("12230025a6118122e9eac8785c74193819441fe57fec4845"); System.out.println("seed = "+Utils.toStringOfHex(seed));

			/////// 2nd message = pk
			Utils.sendToSocket (pkString,this.out,"pk");

			/////// 3d message = signature of hashSeed
			byte[] hashSeed = Utils.hash(seed, 32);
			byte[] signature = Utils.signature(hashSeed, skString);
			System.out.println("hashSeed  = " + Utils.toStringOfHex(hashSeed));
			System.out.println("signature = " + Utils.toStringOfHex(signature));
			Utils.sendToSocket(signature,this.out,"signature");
			
			////// 4th message = tag 1
			byte[] msg = { (byte)0x00, (byte)0x01 };
			Utils.sendToSocket (msg,this.out,"tag 1");

			////// 5th message = block
			byte[] blockBytes = Utils.getFromSocket(176,in); // block 172 + tag 2 + size 2
		} 
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
}

// блокирующее чтение в пакете java.io.Reader, InputStream
// inputStream базовый класс для чтения байтов, определение конца потока,
// блокирует
// DataInputStream разновидность InputStream, считывать примитивные типы данных
// независимым от машины способом
// BufferedInputStream разновидность inputStream, буфер для
// оптимизации скорости доступа 

// Неблокирующий java.nio асинхронное чтение, буферизация = отличие
// неблокирующего чтения, Буфферы = Временные хранилища фиксированного размера
// для транспортируемых данных
// читать-писать голые байты не так эффективно => потоки можно обернуть в классы
// адаптеры, буферизированные или нет:
// BufferedReader in = new BufferedReader(new
// InputStreamReader(socket.getInputStream())); // BufferedWriter // in.readLine
// размер буфера 8192
// InputStreamReader reader; // блокирующий вызов, поток останавливается пока
// данные не станут доступными
// ByteBuffer очистить данные из потока с помощью flush()
// to hold the lock on the object = wait = call it within a synchronized block
// wait() releases the monitor, then reacquire it before returning
// owner of the objects monitor = is in a synchronized block using that object
// NOT use a Thread object's monitor for synchronization, waiting, notifying -
// code within Thread already does that => create a separate object for the
// purpose of synchronization/wait/notify.
/*
 * java.lang.IllegalMonitorStateException: current thread is not owner
 * BufferedReader bufferedreader = new BufferedReader(new
 * InputStreamReader(socket.getInputStream())); bufferedreader.wait(); String
 * fromServer = bufferedreader.readLine();
 */