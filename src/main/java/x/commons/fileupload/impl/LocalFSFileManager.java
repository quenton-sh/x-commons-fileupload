package x.commons.fileupload.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import x.commons.fileupload.FileManager;

public class LocalFSFileManager implements FileManager {
	
	private static final int CACHE_SIZE = 10000;
	
	private String dataDir;
	
	private Map<String, String> filePathCache;
	
	@SuppressWarnings("serial")
	public LocalFSFileManager(String dataDir) {
		this.dataDir = dataDir;
		
		this.filePathCache = new LinkedHashMap<String, String>(CACHE_SIZE + 1, 1.0f, true) {
			@Override
			protected boolean removeEldestEntry(
					Map.Entry<String, String> eldest) {
				return size() > CACHE_SIZE;
			}
		};
	}
	
	@Override
	public boolean exists(String md5) {
		String absPath = this.calculateAbsolutePathForFile(md5);
		File file = new File(absPath);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean saveFile(String md5, InputStream in) throws FileSavingException {
		String absPath = this.calculateAbsolutePathForFile(md5);
		File file = new File(absPath);
		if (file.exists()) { // 文件已存在，退出
			return false;
		} 
		
		file.getParentFile().mkdirs();
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			IOUtils.copy(in, out);
		} catch (IOException e) {
			throw new FileSavingException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
		return true;
	}

	@Override
	public InputStream getFileInputStream(String md5) throws FileNotFoundException {
		String absPath = this.calculateAbsolutePathForFile(md5);
		return new FileInputStream(absPath);
	}
	
	@Override
	public File getFile(String md5) throws FileNotFoundException {
		String absPath = this.calculateAbsolutePathForFile(md5);
		File file = new File(absPath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		return file;
	}
	
	protected String calculateAbsolutePathForFile(String md5) {
		
		String absPath = this.filePathCache.get(md5);
		if (absPath != null) {
			return absPath;
		}
		
		String sec1 = md5.substring(0, 8);
		String sec2 = md5.substring(8, 16);
		String sec3 = md5.substring(16, 24);
		String sec4 = md5.substring(24);
		
		int mod1 = new BigInteger(sec1, 16).mod(new BigInteger("100")).intValue();
		int mod2 = new BigInteger(sec2, 16).mod(new BigInteger("100")).intValue();
		int mod3 = new BigInteger(sec3, 16).mod(new BigInteger("100")).intValue();
		int mod4 = new BigInteger(sec4, 16).mod(new BigInteger("100")).intValue();
		
		String path1 = String.format("%02d", mod1);
		String path2 = String.format("%02d", mod2);
		String path3 = String.format("%02d", mod3);
		String path4 = String.format("%02d", mod4);
		
		absPath = StringUtils.join(new String[] {this.dataDir, path1, path2, path3, path4, md5}, File.separator);
		this.filePathCache.put(md5, absPath);
		
		return absPath;
	}
}
