package startups.impl;

import java.io.File;
import java.util.ResourceBundle;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import startups.StartupInt;

public class StartDocx2Pdf implements StartupInt {
	static final ResourceBundle bundle = ResourceBundle.getBundle("docxpdf");
	private static final Logger log = Logger.getLogger(StartDocx2Pdf.class);
	public static final String FOLDER = bundle.getString("folderMonitor");

	public void run() throws Exception {
		long pollingInterval = 5000L;

		File folder = new File(FOLDER);
		if (!folder.exists()) {
			throw new RuntimeException("Directory not found: " + FOLDER);
		}
		log.debug("OBSERVER OVER " + folder);

		FileAlterationObserver observer = new FileAlterationObserver(folder);
		FileAlterationMonitor monitor = new FileAlterationMonitor(5000L);

		FileAlterationListener listener = new StartDocx2PdfFileAlterationListener();

		observer.addListener(listener);
		monitor.addObserver(observer);
		monitor.start();
	}
}
