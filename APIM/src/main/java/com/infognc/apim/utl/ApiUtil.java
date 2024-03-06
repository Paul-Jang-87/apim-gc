package com.infognc.apim.utl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ApiUtil {
	private static final Logger logger = LoggerFactory.getLogger(ApiUtil.class);
	protected static final String REQ_MNDT_FIELD_ERROR = "요청 필수항목이 누락되었습니다.";
	
	private static final String keyHmacSha512 = Configure.get("keyHmacSha512");
	
	// APIM 개발 - WAS 운영을 맞추기 위해 세팅한 키 값 ?
	private long baseTimeStamp = 10 * 60 * 1000;	// 10분 제한?
	Encoder base64encoder = Base64.getEncoder();
	Decoder base64decoder = Base64.getDecoder();
	
	
	public String encode(String sourceString) {
		byte[] sourceBytes = sourceString.getBytes();
		sourceString = new String( base64encoder.encode(sourceBytes) );
		return sourceString;
	}
	
	public String decode(String targetString) {
		byte[] targetBytes = targetString.getBytes();
		targetString = new String( base64decoder.decode(targetBytes) );
		return targetString;
	}
	
	public boolean isValidTime(String inboundTime) throws ParseException {
		boolean rtnBool = true;
		String convTime = inboundTime.substring(0,8) + inboundTime.substring(9, 15);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		System.out.println(dateFormat.toString());
		
		Date inboundDate = null;
		inboundDate = dateFormat.parse(convTime);
		System.out.println(">> in date :: " + inboundDate.toString());
		System.out.println(">> new date :: " + new Date().toString());
		long nowTimeStamp = (new Date()).getTime();
		long inboundTimeStamp = inboundDate.getTime();
		
		if(nowTimeStamp > (inboundTimeStamp + baseTimeStamp)) {	// 기준시간 이내의 inbound 체크
			rtnBool = false;
		}
		
		return rtnBool;
	}

	
	public static void hasValue(Map param, String[] keys, String ldName) throws Exception {
		boolean hasValue = true;
		StringBuilder sb = new StringBuilder();
		logger.debug("===== hasValue start======" + param.get(ldName));
		String str = "";
		List<Map> lMinfo = (List<Map>) param.get(ldName);
		
		for(Map<String, String> map : lMinfo) {
			logger.debug(">> lMinfo size :: " + lMinfo.size());
			if(param.isEmpty() || !param.containsKey(ldName) || lMinfo.isEmpty() || lMinfo.size() == 0) {
				throw new Exception(REQ_MNDT_FIELD_ERROR + " " + ldName);
			} else {
				for(String key :  keys) {
					if(null==map.get(key) || "".equals(map.get(key))) {
						sb.append(" ");
						sb.append(key);
						hasValue = false;
					}
				}
			}
			logger.debug(">>>> hasValue lMinfo :: " + lMinfo);
		}
		if(!hasValue) {
			throw new Exception(REQ_MNDT_FIELD_ERROR + sb.toString());
		}
		logger.debug(">>>> hasValue lMinfo :: " + sb);
	}
	
	
	public static void hasValue(Map<String,String> inParamMap, String[] keys) throws Exception {
		boolean hasValue = true;
		StringBuilder sb = new StringBuilder();
		logger.debug("===== hasValue start======" + inParamMap + "==" + keys);
		if(inParamMap.isEmpty()) {
			throw new Exception(REQ_MNDT_FIELD_ERROR);
		}else {
			for(String key :  keys) {
				if(!inParamMap.containsKey(key) || inParamMap.get(key).isEmpty()) {
					sb.append(" ");
					sb.append(key);
					hasValue = false;
				}
			}
		}
		if(!hasValue) {
			throw new Exception(REQ_MNDT_FIELD_ERROR + sb.toString());
		}
		
	}
	
	public void checkHmacAuth(String appName, String apimAuth, String authTime, String gtid) throws Exception {
		logger.debug(">>> keyHmacSha512 :: " + keyHmacSha512);
		byte[] byteSha512key = keyHmacSha512.getBytes();
		SecretKeySpec keySpec = new SecretKeySpec(byteSha512key, "HmacSHA512");
		logger.debug(">> keyspec :: " + keySpec);
		logger.debug(">> authTime :: " + authTime);
		
		Mac mac = null;
		String serverAuth = null;
		String hmacMessage = authTime+"@"+appName+"@"+gtid;
		
		try {
			mac.getInstance("HmacSHA512");
			mac.init(keySpec);
			byte[] result = mac.doFinal(hmacMessage.getBytes());
			logger.debug("hmacMessage >>> " + hmacMessage.getBytes());
			logger.debug("hmacMessage2 >>> " + mac.doFinal(hmacMessage.getBytes()));
			logger.debug("result >>> " + result);
			serverAuth = new String(base64encoder.encode(result), "UTF-8");
			
			logger.info("## serverAuth >> " + serverAuth);
			logger.info("## info >> " + serverAuth + ", apimAuth >> " + apimAuth);
			
		}catch(NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e1) {
			throw new HmacSha512Exception();
		}
		
		if(!serverAuth.equals(apimAuth)) {
			logger.info("error >> " + serverAuth + ", apimAuth >> " + apimAuth);
			throw new HmacSha512Exception("HMAC authorization failed");
		}
	
		if(!isValidTime(authTime)) {
			throw new HmacTimeoutException("Request not valid (timeout)");
		}
	}
	
	public static String nullToString(Object obj) {
		return nullToString(obj, "");
	}
	
	public static String nullToString(Object obj, String defStr) {
		String rtn = "";
		if((obj==null) || ("".equals(obj.toString().trim()))) {
			rtn = defStr;
		} else if(obj instanceof Map) {
			Gson gson = new Gson();
			rtn = gson.toJson(obj);
		} else if(obj instanceof String[]) {
			rtn = String.join("-", (String[])obj);
		} else {
			rtn = obj.toString().trim();
		}
		
		return rtn; 
	}
	
	
	
	
}
