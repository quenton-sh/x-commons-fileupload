package x.commons.fileupload;

@SuppressWarnings("serial")
class OverQuotaException extends RuntimeException {

	public OverQuotaException() {
		super();
	}
	
	public OverQuotaException(String s) {
		super(s);
	}
}
