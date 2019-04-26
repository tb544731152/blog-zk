package com.weapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class HttpUtils  {

	public static String sendGet(String url, String param) {
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "multipart/form-data");
			// 设置连接超时
			conn.setConnectTimeout(3 * 1000);
			// 设置读超时
			conn.setReadTimeout(3 * 1000);
			conn.connect();
			Map<String, List<String>> map = conn.getHeaderFields();

			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}

	public static String postJsonRequest(String urls, String param) {
		String result = "";
		URL url = null;
		OutputStream out = null;
		HttpURLConnection httpurl = null;
		try {
			url = new URL(urls);
			httpurl = (HttpURLConnection) url.openConnection();
			httpurl.setConnectTimeout(5 * 1000);
			httpurl.setReadTimeout(8 * 1000);
			httpurl.setRequestMethod("POST");
			httpurl.setDoOutput(true);
			out = httpurl.getOutputStream();
			out.write(param.getBytes("UTF-8"));
			out.flush();
		} catch (Exception ex) {
			new RuntimeException("ThreadPoolTask.post()与客户端通讯(读取数据)异常");
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader bufferreader = null;
		try {
			bufferreader = new BufferedReader(new InputStreamReader(httpurl
					.getInputStream(), "UTF-8"));
			StringBuffer stringbuffer = new StringBuffer();
			int ch;
			while ((ch = bufferreader.read()) > -1) {
				stringbuffer.append((char) ch);
			}
			result = stringbuffer.toString().trim();

			httpurl.disconnect();
		} catch (Exception e) {
			new RuntimeException("ThreadPoolTask.post()与客户端通讯(读取数据)异常");
		} finally {
			try {
				bufferreader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
