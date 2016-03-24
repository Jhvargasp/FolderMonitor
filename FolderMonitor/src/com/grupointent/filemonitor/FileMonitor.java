package com.grupointent.filemonitor;

import java.io.*;
import java.net.URL;
import java.util.*;

// Referenced classes of package com.grupointent.filemonitor:
//            FileChangeListener

public class FileMonitor {
	class FileMonitorTask extends TimerTask {

		FileChangeListener listener;
		File monitoredFile;
		long lastModified;
		final FileMonitor this$0;

		public void run() {
			long lastModified = monitoredFile.lastModified();
			if (lastModified != this.lastModified) {
				this.lastModified = lastModified;
				fireFileChangeEvent(listener, monitoredFile);
			}
		}

		public FileMonitorTask(FileChangeListener listener, File file) throws FileNotFoundException {
			this$0 = FileMonitor.this;
			//super();
			this.listener = listener;
			lastModified = 0L;
			monitoredFile = file;
			if (!monitoredFile.exists()) {
				URL fileURL = listener.getClass().getClassLoader().getResource(file.toString());
				if (fileURL != null) {
					monitoredFile = new File(fileURL.getFile());
				} else {
					throw new FileNotFoundException((new StringBuilder("File Not Found: ")).append(file).toString());
				}
			}
			lastModified = monitoredFile.lastModified();
		}
	}

	private static final FileMonitor instance = new FileMonitor();
	private Timer timer;
	private Hashtable timerEntries;

	public static FileMonitor getInstance() {
		return instance;
	}

	private FileMonitor() {
		timer = new Timer(true);
		timerEntries = new Hashtable();
	}

	public void addFileChangeListener(FileChangeListener listener, String fileName, long period)
			throws FileNotFoundException {
		addFileChangeListener(listener, new File(fileName), period);
	}

	public void addFileChangeListener(FileChangeListener listener, File file, long period)
			throws FileNotFoundException {
		removeFileChangeListener(listener, file);
		FileMonitorTask task = new FileMonitorTask(listener, file);
		timerEntries.put((new StringBuilder(String.valueOf(file.toString()))).append(listener.hashCode()).toString(),
				task);
		timer.schedule(task, period, period);
	}

	public void removeFileChangeListener(FileChangeListener listener, String fileName) {
		removeFileChangeListener(listener, new File(fileName));
	}

	public void removeFileChangeListener(FileChangeListener listener, File file) {
		FileMonitorTask task = (FileMonitorTask) timerEntries
				.remove((new StringBuilder(String.valueOf(file.toString()))).append(listener.hashCode()).toString());
		System.out.println(task);
		if (task != null) {
			task.cancel();
		}
	}

	protected void fireFileChangeEvent(FileChangeListener listener, File file) {
		listener.fileChanged(file);
	}

}
