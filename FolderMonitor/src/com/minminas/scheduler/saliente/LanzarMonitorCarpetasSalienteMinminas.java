package com.minminas.scheduler.saliente;

import com.grupointent.daemonServer.beans.DaemonInterface;
import com.grupointent.filemonitor.FileMonitor;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

// Referenced classes of package com.minminas.scheduler.saliente:
//            FileChangeListenerImpl

public class LanzarMonitorCarpetasSalienteMinminas
    implements DaemonInterface
{

    private static Logger log = Logger.getLogger(LanzarMonitorCarpetasSalienteMinminas.class);
    String bundleStr;
    static FileChangeListenerImpl IMPL = new FileChangeListenerImpl();

    public LanzarMonitorCarpetasSalienteMinminas()
    {
        bundleStr = "com.minminas.scheduler.confSalienteMinMinas";
    }

    public void start()
    {
        FileMonitor mon = FileMonitor.getInstance();
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleStr);
            log.debug("Monitor started!!!");
            log.debug((new StringBuilder("Carpeta Saliente monitoreada:")).append(bundle.getString("FILEID")).toString());
            mon.addFileChangeListener(IMPL, bundle.getString("FILEID"), 1000L);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        FileMonitor mon = FileMonitor.getInstance();
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleStr);
            log.debug("Monitor stoped!!!");
            mon.removeFileChangeListener(IMPL, bundle.getString("FILEID"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
