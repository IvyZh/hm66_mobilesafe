package com.ivy.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

	public static String InputStream2String(InputStream is) throws IOException{
		byte[] b = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while((len = is.read(b)) != -1){
			//把读到的字节先写入字节数组输出流中存起来
			bos.write(b, 0, len);
		}
		//把字节数组输出流中的内容转换成字符串
		//默认使用utf-8
		String text = new String(bos.toByteArray());
		return text;
	}
}
