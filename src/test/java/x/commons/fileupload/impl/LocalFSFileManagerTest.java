package x.commons.fileupload.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import x.commons.fileupload.FileManager.FileSavingException;

public class LocalFSFileManagerTest {

	private static LocalFSFileManager sug = null;
	private static String testFile;
	private static String md5;
	
	private static final String DATA_DIR = "/tmp/test";
	
	@BeforeClass
	public static void init() {
		sug = new LocalFSFileManager(DATA_DIR);
		
		testFile = LocalFSFileManagerTest.class.getResource("/testdata-big.txt").getPath();
		md5 = "0ed749ce46512a3f638363a6e962d11e";
	}
	
	@Test
	public void calculateAbsolutePathForFile() {
		String absPath = sug.calculateAbsolutePathForFile(md5);
		System.out.println(absPath);
		
		absPath = sug.calculateAbsolutePathForFile(md5);
		System.out.println(absPath);
	}
	
	@Test
	public void saveFile() throws FileNotFoundException, FileSavingException {
		sug.saveFile(md5, new FileInputStream(testFile));
	}
	
	@AfterClass
	public static void cleanup() throws IOException {
		File dir = new File(DATA_DIR);
		FileUtils.deleteDirectory(dir);
	}
}
