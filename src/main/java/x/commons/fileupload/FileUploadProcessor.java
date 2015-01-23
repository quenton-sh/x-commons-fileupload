package x.commons.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadProcessor {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private FileManager fileManager;
	private int maxFileSize = Integer.MAX_VALUE;
	private FileItem tmpFileItem;
	
	private String md5;
	private int size = -1;
	
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void setTmpFileItem(FileItem tmpFileItem) {
		this.tmpFileItem = tmpFileItem;
	}

	/**
	 * 上传到临时目录，检查数据体积，并计算md5值
	 * @param in
	 * @throws FileUploadException
	 */
	public void uploadToTmpFile(InputStream in) throws FileUploadException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new FileUploadException(e);
		}
		in = new DigestInputStream(in, md);
		
		OutputStream out = null;
		try {
			out = this.tmpFileItem.getOutputStream();
			int totalRead = 0;
			byte[] buff = new byte[2048];
			int buffRead = -1;
			while ((buffRead = in.read(buff)) != -1) {
				out.write(buff, 0, buffRead);
				totalRead += buffRead;
				if (totalRead > this.maxFileSize) {
					// 文件体积超限，中断并退出
					return;
				}
			}
			this.size = totalRead;
		} catch (Exception e) {
			throw new FileUploadException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
		this.md5 = Hex.encodeHexString(md.digest()).toLowerCase();
		logger.debug(String.format("Uploaded data size: %d", this.size));
	}
	
	public String getMD5() {
		return this.md5;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public InputStream getTmpFileInputStream() throws IOException {
		if (this.tmpFileItem != null) {
			return this.tmpFileItem.getInputStream();
		}
		return null;
	}
	
	public void save() throws FileSaveException {
		try {
			this.fileManager.saveFile(this.md5, this.tmpFileItem.getInputStream());
		} catch (IOException e) {
			throw new FileSaveException(e);
		}
	}
	
	public void drop() {
		this.tmpFileItem = null;
	}
	
	public FileManager getFileManager() {
		return this.fileManager;
	}
}
