package startups.impl;

import actions.Actions;
import impl.docxpdf.MinMinasActionImpl;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

public class StartDocx2PdfFileAlterationListener extends FileAlterationListenerAdaptor {
	final Actions actions = new MinMinasActionImpl();

	public void onFileChange(File file) {
		try {
			System.out.println("File change: " + file.getCanonicalPath());
			this.actions.doUpdate(file.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void onFileCreate(File file) {
		try {
			System.out.println("File created: " + file.getCanonicalPath());
			if (file.getParent().equalsIgnoreCase(new File(StartDocx2Pdf.FOLDER).getAbsolutePath())) {
				System.out.println("try...");
				this.actions.doCreate(file.getCanonicalPath());
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void onFileDelete(File file) {
		try {
			System.out.println("File removed: " + file.getCanonicalPath());

			System.out.println("File still exists in location: " + file.exists());
			this.actions.doDelete(file.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
}
