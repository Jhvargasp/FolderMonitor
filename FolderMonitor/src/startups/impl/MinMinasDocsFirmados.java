package startups.impl;

import java.io.File;
import java.util.ResourceBundle;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import startups.StartupInt;

public class MinMinasDocsFirmados
  implements StartupInt
{
  static final ResourceBundle bundle = ResourceBundle.getBundle("docxpdf");
  public static final String FOLDER = bundle
    .getString("folderMonitorFirmados");
  private static final Logger log = Logger.getLogger(MinMinasDocsFirmados.class);
  
  public void run()
    throws Exception
  {
    long pollingInterval = 5000L;
    
    File folder = new File(FOLDER);
    if (!folder.exists()) {
      throw new RuntimeException("Directory not found: " + FOLDER);
    }
    log.debug("OBSERVER OVER " + folder);
    FileAlterationObserver observer = new FileAlterationObserver(folder);
    FileAlterationMonitor monitor = new FileAlterationMonitor(
      5000L);
    
    FileAlterationListener listener = new MinMinasDocsFirmadosFileAlterationListener();
    
    observer.addListener(listener);
    monitor.addObserver(observer);
    monitor.start();
  }
}
