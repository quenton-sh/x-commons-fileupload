package x.commons.fileupload;

import org.apache.commons.fileupload.FileItemFactory;

public class FileUploadProcessorFactory {

	private int maxFileSize = Integer.MAX_VALUE;
	
	private FileItemFactory fileItemFactory;
	private FileManager fileManager;
	
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void setFileItemFactory(FileItemFactory fileItemFactory) {
		this.fileItemFactory = fileItemFactory;
	}

	public FileUploadProcessor newFileUploadProcessor() {
		FileUploadProcessor obj = new FileUploadProcessor();
		obj.setFileManager(fileManager);
		obj.setMaxFileSize(maxFileSize);
		obj.setTmpFileItem(this.fileItemFactory.createItem(null, null, false, null));
		return obj;
	}

}
