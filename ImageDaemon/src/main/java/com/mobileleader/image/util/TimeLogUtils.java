package com.mobileleader.image.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeLogUtils {

	private static final Logger logger = LoggerFactory.getLogger("TimeLogger");
	
	public static void logging(long startTime, long endTime, String comment) {
		logger.info("{} : {} sec", String.format("%-10s", comment), String.format("%.3f",(float) (endTime - startTime) / 1000.0));	
	}
	
	public static void logStart(String comment) {
		logger.info(comment);
		logger.info("=========================");
	}
	
	public static void logEnd(String comment) {
		logger.info("=========================");
		logger.info(comment + "\n");
	}
	
	public static void logStepStart(String comment) {
		logger.info("{}", comment);
		logger.info("-------------------------");
	}
	
	public static void logStepEnd() {
		logger.info("-------------------------\n");
	}
	
	public static void log(String log) {
		logger.info("{}", log);
	}
}
