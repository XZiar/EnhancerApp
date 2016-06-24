package xziar.enhancer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util
{
	public static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes)
	{
		char[] hexChars = new char[bytes.length * 2];
		for (int i = 0, j = 0; i < bytes.length;)
		{
			int v = bytes[i++] & 0xFF;
			hexChars[j++] = hexArray[v >>> 4];
			hexChars[j++] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String md5(byte[] data)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			return bytesToHex(md.digest(data));
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
