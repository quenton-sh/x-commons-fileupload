package x.commons.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.commons.fileupload.FileManager.FileSavingException;

public class FileUploadProcessor {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private FileManager fileManager;
	private int maxFileSize = Integer.MAX_VALUE;
	private FileItem tmpFileItem;
	
	private String md5;
	
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void setTmpFileItem(FileItem tmpFileItem) {
		this.tmpFileItem = tmpFileItem;
	}

	// 上传到临时目录，检查数据体积，并计算md5码
	public int uploadToTmpFile(InputStream in) throws Exception {
		in = new QuotaInputStream(in, this.maxFileSize);
		MessageDigest md = MessageDigest.getInstance("MD5");
		in = new DigestInputStream(in, md);
		
		int size = -1;
		OutputStream out = null;
		try {
			out = this.tmpFileItem.getOutputStream();
			size = IOUtils.copy(in, out);
		} catch (OverQuotaException e) {
			return -1;
		} finally {
			IOUtils.closeQuietly(out);
		}
		this.md5 = Hex.encodeHexString(md.digest()).toLowerCase();
		logger.debug(String.format("Uploaded data size: %d", size));
		
		return size;
	}
	
	public String getMD5() {
		return this.md5;
	}
	
	public InputStream getTmpFileInputStream() throws IOException {
		if (this.tmpFileItem != null) {
			return this.tmpFileItem.getInputStream();
		}
		return null;
	}
	
	public void save() throws FileSavingException {
		try {
			this.fileManager.saveFile(this.md5, this.tmpFileItem.getInputStream());
		} catch (IOException e) {
			throw new FileSavingException(e);
		}
	}
	
	public void drop() {
		this.tmpFileItem = null;
	}
	
	public FileManager getFileManager() {
		return this.fileManager;
	}
}
