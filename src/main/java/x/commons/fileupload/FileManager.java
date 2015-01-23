package x.commons.fileupload;

import java.io.File;
import java.io.InputStream;

public interface FileManager {
	
	public boolean exists(String md5);

	/**
	 * @param md5
	 * @param in
	 * @return 如果此MD5值对应的文件已存在，返回false；否则储存此文件并返回true
	 * @throws FileSaveException
	 */
	public boolean saveFile(String md5, InputStream in) throws FileSaveException;
	
	public InputStream getFileInputStream(String md5);
	
	public File getFile(String md5);
}
