package com.mobileleader.edoc.sample;

import org.apache.commons.io.FilenameUtils;

public class Test {

	public static void main(String[] args) {
	
		String name = "test/convert/convert.pdf";
		System.out.println(FilenameUtils.getPath(name));
		System.out.println(FilenameUtils.getPathNoEndSeparator(name));
		System.out.println(FilenameUtils.getFullPath(name));
		System.out.println(FilenameUtils.getFullPathNoEndSeparator(name));
		System.out.println(FilenameUtils.getPrefix(name));
		System.out.println(FilenameUtils.getExtension(name));
		System.out.println(FilenameUtils.getName(name));
	}
}
