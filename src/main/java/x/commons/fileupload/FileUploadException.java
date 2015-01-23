package x.commons.fileupload;

@SuppressWarnings("serial")
public class FileUploadException extends Exception {

	public FileUploadException(Exception e) {
		super(e);
	}
	
	public FileUploadException(String s) {
		super(s);
	}
}
