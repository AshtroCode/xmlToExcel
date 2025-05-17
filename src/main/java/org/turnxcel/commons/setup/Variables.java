package com.commons.setup;

import java.util.LinkedHashMap;
import java.util.Map;

public class Variables {
	public static final String ResourcesDirectory = System.getProperty("user.dir")+"\\resources\\";
	public static final String ProjectDirectory = System.getProperty("user.dir");
	public static final String InputDataDirectory = System.getProperty("user.dir")+"\\InputData\\";
	public static final String OutputDataDirectory = System.getProperty("user.dir")+"\\OutputData\\";
	public static final String LogDirectory = System.getProperty("user.dir")+"\\Logs\\";
	public static final String XMLtoExcelOutputDirectory = System.getProperty("user.dir")+"\\OutputData\\XMLtoExcel.xlsx";

	public static String XMLFilePath = InputDataDirectory;
	
	public static Map<String, String> XMLDataMap = new LinkedHashMap<>();

}

