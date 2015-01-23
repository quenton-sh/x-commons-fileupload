package x.commons.fileupload;

@SuppressWarnings("serial")
public class FileSaveException extends Exception {

	public FileSaveException() {
		super();
	}

	public FileSaveException(Exception e) {
		super(e);
	}

	public FileSaveException(String s) {
		super(s);
	}
}
