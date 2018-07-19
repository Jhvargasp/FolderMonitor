package startups.impl;

import actions.Actions;
import impl.MinMinas.MinMinasProcesaFirmadosImpl;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

public class MinMinasDocsFirmadosFileAlterationListener extends FileAlterationListenerAdaptor {
	final Actions actions = new MinMinasProcesaFirmadosImpl();

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
			this.actions.doCreate(file.getCanonicalPath());
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
