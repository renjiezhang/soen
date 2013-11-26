package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Calendar;

public class LogTool {
	String path;
	FileWriter fw;

	public LogTool(String path) {
		this.path = path;
	}

	synchronized public void log(String msg) {
		try {
			if (fw == null) {

				File dir = new File(this.path.substring(0,
						this.path.lastIndexOf("/")));
				if (!dir.exists())
					dir.mkdirs();

				fw = new FileWriter(this.path);

			}
			fw.append(String.format("[%1$tD %1$tT] %2$s\n",
					Calendar.getInstance(), msg));
			fw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void close() {
		if (this.fw != null)
			try {
				this.fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
