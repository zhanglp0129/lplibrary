package com.lpkaifa.lplibrary.encrypt;

import com.lpkaifa.lplibrary.base64.Base64Utils;
import com.lpkaifa.lplibrary.exception.KeyInvalidException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Random;

/**
 * RSA算法的加密解密。
 * 为方便传输，密文以base64呈现
 *
 */
public class RSA {
	private final BigInteger e;	// 公开指数
	private final BigInteger d;	// 私有指数
	private final BigInteger n;	// 模数
	private final BigInteger p;	// 第一个素数
	private final BigInteger q;	// 第二个素数
	private final BigInteger dp;// d mod (p-1)
	private final BigInteger dq;// d mod (q-1)
	private final BigInteger qi;// q^-1 mod p
	
	/**
	 * 随机公钥私钥，默认为1024个二进制位
	 */
	public RSA() {
		this(1024);
	}
	
	/**
	 * 指定位数，随机公钥私钥
	 * @param bits 位数。当bit<=768时，该加密算法不安全
	 */
	public RSA(int bits) {
		this(getPrime(bits/2,40),getPrime(bits/2,40));
	}
	
	private static BigInteger getPrime(int bits,int certainty) {
		// 获取一个大素数
		Random rnd = new Random();
		BigInteger n = new BigInteger(bits, rnd);
		// 将n的最低位设置为1，确保它是奇数
		n = n.setBit(0);
		while (!n.isProbablePrime(certainty)) {
			n = new BigInteger(bits, rnd);
			n = n.setBit(0);
		}
		return n;
	}
	
	/**
	 * 指定私钥来获取n,e,d
	 * 格式：PEM格式，PKCS#8
	 * @param privateKeyString 私钥字符串
	 */
	public RSA(String privateKeyString) {
		// 去除标记信息
		String privateKeyBase64 = privateKeyString.replaceAll("\\s+","")
				.replace("-----BEGINPRIVATEKEY-----","")
				.replace("-----ENDPRIVATEKEY-----", "")
				.replace("-----BEGINRSAPRIVATEKEY-----","")
				.replace("-----ENDRSAPRIVATEKEY-----","");
		byte[] privateKeyBytes = Base64Utils.decode(privateKeyBase64);
		
		RSAPrivateCrtKey rsaPrivateKey;
		// 解析PKCS#8的私钥
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			rsaPrivateKey = (RSAPrivateCrtKey) factory.generatePrivate(spec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
		
		n = rsaPrivateKey.getModulus();
		e = rsaPrivateKey.getPublicExponent();
		d = rsaPrivateKey.getPrivateExponent();
		p = rsaPrivateKey.getPrimeP();
		q = rsaPrivateKey.getPrimeQ();
		dp = rsaPrivateKey.getPrimeExponentP();
		dq = rsaPrivateKey.getPrimeExponentQ();
		qi = rsaPrivateKey.getCrtCoefficient();
	}
	
	/**
	 * 指定公钥私钥来获取n,e,d
	 * 格式：PEM格式，PKCS#8
	 * @param publicKeyString 公钥字符串
	 * @param privateKeyString	私钥字符串
	 */
	public RSA(String publicKeyString,String privateKeyString) {
		this(privateKeyString);
	}
	
	/**
	 * 传入两个素数，来生成密钥
	 * 采用试除法判断素数，并不能保证准确
	 * @param p
	 * @param q
	 */
	public RSA(BigInteger p,BigInteger q) throws KeyInvalidException {
		if(!(p.isProbablePrime(40)&&q.isProbablePrime(40))) {
			throw new KeyInvalidException("生成密钥的p或q不是素数！");
		}
		this.p=p;
		this.q=q;
		BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));	// 欧拉函数
		n = p.multiply(q);
		
		// 创建公钥
		if(phiN.compareTo(BigInteger.valueOf(65537))>0) {
			// 选取65537作为公开指数
			e=BigInteger.valueOf(65537);
		} else {
			// 选取3作为公开指数
			e=BigInteger.valueOf(3);
		}
		
		// 创建对应私有指数
		d = e.modInverse(phiN);
		
		dp = d.mod(p.subtract(BigInteger.ONE));
		dq = d.mod(q.subtract(BigInteger.ONE));
		qi = q.modInverse(p);
	}
	
	/**
	 * 获取公钥
	 * 格式：PEM格式，但不包含开头和结尾的标记信息，是一个纯base64字符串
	 * 填充模式：ENCRYPTION_PKCS1
	 * @return
	 */
	public String getPublicKey() {
		StringWriter stringWriter;
		try {
			RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n,e);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
			
			PemObject pemObject = new PemObject("RSA PUBLIC KEY", publicKey.getEncoded());
			stringWriter = new StringWriter();
			PemWriter pemWriter = new PemWriter(stringWriter);
			pemWriter.writeObject(pemObject);
			pemWriter.close();
		} catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
		
		return stringWriter.toString().replaceAll("\\s+","").replace("-----BEGINRSAPUBLICKEY-----","").replace("-----ENDRSAPUBLICKEY-----","");
	}
	
	/**
	 * 获取私钥
	 * 格式：PEM格式，但不包含开头和结尾的标记信息，是一个纯base64字符串
	 * 填充模式：ENCRYPTION_PKCS1
	 * @return
	 */
	public String getPrivateKey() {
		StringWriter stringWriter;
		try {
			RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateCrtKeySpec(n,e,d,p,q,dp,dq,qi);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
			
			PemObject pemObject = new PemObject("RSA PRIVATE KEY",privateKey.getEncoded());
			stringWriter = new StringWriter();
			PemWriter pemWriter = new PemWriter(stringWriter);
			pemWriter.writeObject(pemObject);
			pemWriter.close();
		} catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
		
		return stringWriter.toString().replaceAll("\\s+","").replace("-----BEGINRSAPRIVATEKEY-----","").replace("-----ENDRSAPRIVATEKEY-----","");
	}
	
	/**
	 * 将公钥和私钥保存在指定文件中
	 * 以PEM格式保存
	 * @param src
	 */
	public void copyKey(String src) {
		copyKey(new File(src));
	}
	
	/**
	 * 将公钥和私钥保存在指定文件中
	 * 以PEM格式保存
	 * @param file
	 */
	public void copyKey(File file) {
		PrintWriter pw = null;
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			pw = new PrintWriter(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// 先保存公钥
		pw.write("-----BEGIN PUBLIC KEY-----\n");
		pw.write(getPublicKey()+"\n");
		pw.write("-----END PUBLIC KEY-----\n\n");
		
		// 再保存私钥
		pw.write("-----BEGIN PRIVATE KEY-----\n");
		pw.write(getPrivateKey()+"\n");
		pw.write("-----END PRIVATE KEY-----\n");
		
		pw.close();
	}
	
	
	/**
	 * 判断两个数是否互素
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean isCoprime(BigInteger a,BigInteger b) {
		BigInteger r;
		do {
			r=a.mod(b);
			a=b;
			b=r;
		} while (!r.equals(0));
		return a.equals(1);
	}
	
	/**
	 * 检查公钥和私钥是否匹配，并检查是否密钥有效
	 * @return
	 */
	public boolean check() {
		if(!(p.isProbablePrime(40)&&q.isProbablePrime(40))) {
			// p或q不是素数
			return false;
		}
		if(n.compareTo(p.multiply(q))!=0) {
			// p*q != n
			return false;
		}
		BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));	//欧拉函数
		if(d.compareTo(phiN)>=0) {
			// d >= phiN
			return false;
		}
		if(e.multiply(d).mod(phiN).compareTo(BigInteger.ONE)!=0) {
			// e*d mod phiN != 1
			return false;
		}
		return true;
		
	}
	
	/**
	 * 加密算法，返回加密数据的base64格式
	 * @param plaintext 明文字符串
	 * @return 密文的base64
	 */
	public String encrypt(String plaintext) throws KeyInvalidException {
		return encrypt(plaintext.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * 加密算法，返回加密数据的base64格式
	 * @param plaintextBytes 明文的字节数组
	 * @return 密文的base64
	 */
	public String encrypt(byte[] plaintextBytes) throws KeyInvalidException {
		if(!check()) {
			throw new KeyInvalidException();
		}
		String ciphertextBase64;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			cipher.init(Cipher.ENCRYPT_MODE,publicKey);
			byte[] ciphertextBytes = cipher.doFinal(plaintextBytes);	// 密文的字节数组
			ciphertextBase64 = Base64Utils.encode(ciphertextBytes);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException |
				 BadPaddingException | IllegalBlockSizeException ex) {
			throw new RuntimeException(ex);
		}
		
		return ciphertextBase64;
	}
	
	
	/**
	 * 解密算法，返回明文的字符串
	 * @param ciphertext 密文的base64字符串
	 * @return 明文的字符串
	 */
	public String decryptToString(String ciphertext) throws KeyInvalidException {
		return new String(decryptToBytes(ciphertext), StandardCharsets.UTF_8);
	}
	
	/**
	 * 解密算法，返回明文的字节数组
	 * @param ciphertext 密文的base64字符串
	 * @return 明文的字节数组
	 */
	public byte[] decryptToBytes(String ciphertext) throws KeyInvalidException {
		if(!check()) {
			throw new KeyInvalidException();
		}
		byte[] plaintextBytes;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(n, d);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] ciphertextBytes = Base64Utils.decode(ciphertext);
			plaintextBytes = cipher.doFinal(ciphertextBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException |
				 IllegalBlockSizeException | BadPaddingException ex) {
			throw new RuntimeException(ex);
		}
		return plaintextBytes;
	}
}
