package com.mobileleader.image.util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtils {
	
	private static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
	}

	/**
	 * json -> Object
	 * 
	 * @param String jsonStr
	 * @param Class<T> classType
	 * @return Object
	 */
	public static <T> T fromJson(String jsonStr, Class<T> classType){
		try {
			return mapper.readValue(jsonStr, classType);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Object -> Json String
	 * 
	 * @param Object obj
	 * @return json String
	 */
	public static String toJson(Object obj) {	
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	/**
	 * Object -> Json Pretty Print
	 * 
	 * @param Object object
	 * @return json
	 */
	public static String ObjectPrettyPrint(Object object) {
		return jsonPrettyPrint(toJson(object));
	}
	
	/**
	 * Json String -> Json Pretty Print
	 * 
	 * @param String jsonStr
	 * @return json
	 */
	public static String jsonPrettyPrint(String jsonStr) {
		Object obj;
		try {
			obj = mapper.readValue(jsonStr, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			
		} catch (IOException e) {
			return null;
		}
	}
}
