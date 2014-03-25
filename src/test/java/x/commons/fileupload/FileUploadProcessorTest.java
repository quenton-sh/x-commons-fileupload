package x.commons.fileupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import x.commons.fileupload.impl.LocalFSFileManager;

public class FileUploadProcessorTest {

	private static FileUploadProcessorFactory factory;
	private static final String TEST_DIR = "/tmp/test";
	private static final String DATA_DIR = TEST_DIR + "/data";
	private static final String TMP_DIR = TEST_DIR + "/tmp";
	
	private static FileManager fileManager;
	
	@BeforeClass
	public static void init() {
		new File(DATA_DIR).mkdirs();
		new File(TMP_DIR).mkdirs();
		
		fileManager = new LocalFSFileManager(DATA_DIR);
		
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setRepository(new File(TMP_DIR));
		fileItemFactory.setFileCleaningTracker(new FileCleaningTracker());
		
		factory = new FileUploadProcessorFactory();
		factory.setFileManager(fileManager);
		factory.setMaxFileSize(50000);
		factory.setFileItemFactory(fileItemFactory);
	}
	
	@Test
	public void test() throws Exception {
		// over quota:
		FileUploadProcessor sug = factory.newFileUploadProcessor();
		InputStream in = this.getClass().getResourceAsStream("/testdata-big.txt");
		int size = sug.uploadToTmpFile(in);
		assertTrue(size == -1);
		in.close();

		// normal upload:
		sug = factory.newFileUploadProcessor();
		String absFilePath = this.getClass().getResource("/testdata-small1.txt").getPath();
		String fileContent = FileUtils.readFileToString(new File(absFilePath));
		in = new FileInputStream(absFilePath);
		size = sug.uploadToTmpFile(in);
		assertTrue(size > 20);
		
		String tmpFileContent = new String(IOUtils.toByteArray(sug.getTmpFileInputStream()), "UTF-8");
		assertEquals(fileContent, tmpFileContent);
		
		String md5 = sug.getMD5();
		assertEquals("e13a50eb66e7669585fe3e1e85e31504", md5);
		
		// save
		sug.save();
		InputStream dataIn = fileManager.getFileInputStream(md5);
		String dataFileContent = IOUtils.toString(dataIn, "UTF-8");
		assertEquals(fileContent, dataFileContent);
		
		// drop
		sug = factory.newFileUploadProcessor();
		sug.uploadToTmpFile(this.getClass().getResourceAsStream("/testdata-small2.txt"));
		md5 = sug.getMD5();
		sug.drop();
		System.gc();
		try {
			sug.getFileManager().getFileInputStream(md5);
			fail("Accessing a non-exist file should throw a FileNotFoundException!");
		} catch (FileNotFoundException e) {
			// that's right
		}
	}
	
	@AfterClass
	public static void cleanup() throws IOException {
		File dir = new File(TEST_DIR);
		FileUtils.deleteDirectory(dir);
	}
}
