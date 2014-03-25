package x.commons.fileupload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileManager {
	
	@SuppressWarnings("serial")
	public static class FileSavingException extends Exception {
		public FileSavingException() {
			super();
		}

		public FileSavingException(Exception e) {
			super(e);
		}

		public FileSavingException(String s) {
			super(s);
		}
	}
	
	public boolean exists(String md5);

	/**
	 * @param md5
	 * @param in
	 * @return 如果此MD5值对应的文件已存在，返回false；否则储存此文件并返回true
	 * @throws FileSavingException
	 */
	public boolean saveFile(String md5, InputStream in) throws FileSavingException;
	
	public InputStream getFileInputStream(String md5) throws FileNotFoundException;
	
	public File getFile(String md5) throws FileNotFoundException;
}
