package x.commons.fileupload;

import java.io.IOException;
import java.io.InputStream;


class QuotaInputStream extends InputStream {
	private long quotaSize = 0;
	private long size = 0;
	private InputStream in;
	
	QuotaInputStream(InputStream in, long quotaSize) {
		this.in = in;
		this.quotaSize = quotaSize;
	}
	
	@Override
	public int read() throws IOException {
		int byteData = in.read(); // 每次读一个字节
		if (byteData >= 0) {
			size ++; // 已读字节数+1
			if (size > quotaSize) {
				throw new OverQuotaException();
			}
		}
		return byteData;
	}
}
