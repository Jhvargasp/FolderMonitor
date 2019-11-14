package impl.MinMinas;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.PropertyTemplateSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.grupointent.genericapp.context.ApplicationContextProvider;
import com.intent.admin.filenetPE.UtilProcessEngine;
import filenet.vw.api.VWAttachment;
import filenet.vw.api.VWDataField;
import filenet.vw.api.VWException;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.security.auth.Subject;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class FileNet
{

    private static Logger log = Logger.getLogger(FileNet.class);
    private static ObjectStore os = null;
    private static VWSession ws = null;
    public static ResourceBundle bundle = ResourceBundle.getBundle("filenet");

    public FileNet()
    {
    }

    public static ObjectStore getCESession()
    {
        if(os == null)
        {
            String user = bundle.getString("usr");
            String pwd = bundle.getString("pwd");
            Properties props = System.getProperties();
            props.setProperty("wasp.location", bundle.getString("wasp"));
            Connection conn = com.filenet.api.core.Factory.Connection.getConnection(bundle.getString("url"));
            UserContext uc = UserContext.get();
            Subject subject = UserContext.createSubject(conn, user, pwd, bundle.getString("context"));
            uc.pushSubject(subject);
            Domain domain = com.filenet.api.core.Factory.Domain.fetchInstance(conn, null, null);
            ObjectStoreSet set = domain.get_ObjectStores();
            ObjectStore objectstore;
            for(Iterator it = set.iterator(); it.hasNext();)
            {
                objectstore = (ObjectStore)it.next();
            }

            os = com.filenet.api.core.Factory.ObjectStore.fetchInstance(domain, bundle.getString("objectStore"), null);
            log.debug("FileNet CE Conected!!");
        }
        return os;
    }

    public static VWSession getVWSession()
    {
        if(ws == null)
        {
            VWSession session = null;
            try
            {
                Connection conn = com.filenet.api.core.Factory.Connection.getConnection(bundle.getString("url"));
                UserContext uc = UserContext.get();
                Subject subject = UserContext.createSubject(conn, bundle.getString("usr"), bundle.getString("pwd"), bundle.getString("context"));
                Properties props = System.getProperties();
                props.setProperty("wasp", bundle.getString("wasp"));
                System.setProperty("filenet.pe.bootstrap.ceuri", bundle.getString("url"));
                uc.pushSubject(subject);
                session = new VWSession(bundle.getString("region"));
                log.debug((new StringBuilder("sess is null?:")).append(session == null).toString());
                log.debug("Connected to P8 ");
                ws = session;
            }
            catch(Exception e)
            {
                log.debug(e);
                e.printStackTrace();
                Object obj = null;
            }
        }
        return ws;
    }

    public static void main(String args[])
    {
        String c = "c:/ceadmind_2016000297_OFICIO1391098650850.pdf";
        System.out.println(c.split("_")[2].split("\\.")[0]);
        subirAvanzar("c:/ceadmind_2016000297_OFICIO1391098650850.pdf");
    }

    public static void subirAvanzar(String file)
    {
        VWSession ws = getVWSession();
        UtilProcessEngine pe = new UtilProcessEngine(ws);
        String fname = (new File(file)).getName();
        String wobNum = fname.split("_")[1];
        String userLogin = fname.split("_")[0];
        String rad = fname.split("_")[3];
        rad = rad.split("\\.")[0];
        try
        {
            log.debug((new StringBuilder("Buscando flujo pendiente firma 0x")).append(wobNum).toString());
            VWQueue q = ws.getQueue("AsociateDocuments");
            VWQueueQuery vq = q.createQuery(null, null, null, 0, (new StringBuilder("F_WobNum = 0x")).append(wobNum).toString(), null, 1);
            if(vq.hasNext())
            {
                VWWorkObject wo = (VWWorkObject)vq.next();
                processWo(file, pe, userLogin, rad, wo);
            } else
            {
                log.debug("Cant get using 0");
                vq = q.createQuery(null, null, null, 1, (new StringBuilder("F_WobNum = 0x")).append(wobNum).toString(), null, 1);
                if(vq.hasNext())
                {
                    VWWorkObject wo = (VWWorkObject)vq.next();
                    processWo(file, pe, userLogin, rad, wo);
                } else
                {
                    log.debug("Cant get using 1");
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void processWo(String file, UtilProcessEngine pe, String userLogin, String rad, VWWorkObject wo)
        throws VWException, Exception
    {
        log.debug("Processing flow....");
        VWAttachment att = (VWAttachment)wo.getFieldValue("Correspondencia");
        Document document = null;
        if(att.getId() == null)
        {
            log.debug("Crear att");
            document = createDocumentAndContent("/Configuraciones/Temporal/Temporal pdf creados", "Document", rad, file);
        } else
        {
            log.debug("Update att");
            VersionSeries series = (VersionSeries)os.fetchObject("VersionSeries", att.getId(), null);
            String id = "";
            if(att.getVersion() == null)
            {
                id = ((Document)series.get_CurrentVersion()).get_Id().toString();
            } else
            {
                id = ((Document)series.get_ReleasedVersion()).get_Id().toString();
            }
            document = updateDocumentAndContent(id, file);
        }
        att.setLibraryName(bundle.getString("objectStore"));
        att.setLibraryType(3);
        att.setType(3);
        att.setAttachmentName(document.getProperties().getStringValue("DocumentTitle"));
        VersionSeries vs = document.get_VersionSeries();
        att.setId(vs.get_Id().toString());
        att.setVersion(((Document)document.get_ReleasedVersion()).get_Id().toString());
        try
        {
            log.debug("Locking");
            wo.doLock(true);
            log.debug("Locked");
            log.debug("End Lock");
        }
        catch(Exception e)
        {
            log.debug(e);
        }
        log.debug("Set correspondencia");
        wo.setFieldValue("Correspondencia", att, false);
        wo.setFieldValue("Radicado", rad, false);
        wo.doSave(false);
        log.debug("Avanzar flujo");
        wo.doDispatch();
        log.debug("Borrando documento firmado");
        String fname = (new File(file)).getName();
        (new File(file)).delete();
        try
        {
            ResourceBundle bundle2 = ResourceBundle.getBundle("docxpdf");
            String FOLDER2 = bundle2.getString("folderMonitor");
            fname = fname.replaceAll((new StringBuilder("_")).append(rad).toString(), "");
            String fullFname = (new StringBuilder(String.valueOf(FOLDER2))).append(File.separator).append(fname).toString();
            log.debug((new StringBuilder("Borrando documento intermedio: ")).append(fullFname).toString());
            (new File(fullFname)).delete();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        log.debug("Fin subir avanzar");
        String enlaces[] = (String[])wo.getDataField("Enlace").getValue();
        if(enlaces != null)
        {
            String as[];
            int j = (as = enlaces).length;
            for(int i = 0; i < j; i++)
            {
                String enlace = as[i];
                if(enlace.length() > 0)
                {
                    finalizarFlujo(enlace, pe, rad, userLogin);
                }
            }

        }
    }

    private static Document updateDocumentAndContent(String id, Object urlContent)
        throws Exception
    {
        Document doc = (Document)os.fetchObject("Document", id, null);
        if(!doc.get_IsReserved().booleanValue())
        {
            doc = (Document)doc.get_VersionSeries().get_CurrentVersion();
            doc.checkout(ReservationType.EXCLUSIVE, null, null, null);
            doc.save(RefreshMode.REFRESH);
        }
        Document obj = doc;
        obj = (Document)doc.get_Reservation();
        ContentElementList list = com.filenet.api.core.Factory.ContentElement.createList();
        int idx = 0;
        ContentTransfer tr;
        for(Iterator iterator = doc.get_ContentElements().iterator(); iterator.hasNext(); list.add(tr))
        {
            Object object = iterator.next();
            ContentTransfer tr1 = (ContentTransfer)object;
            tr = com.filenet.api.core.Factory.ContentTransfer.createInstance();
            tr.setCaptureSource(tr1.accessContentStream());
            tr.set_RetrievalName(tr1.get_RetrievalName());
            tr.set_ContentType(tr1.get_ContentType());
        }

        obj.set_ContentElements(list);
        obj.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
        obj.save(RefreshMode.REFRESH);
        return obj;
    }

    private static Document createDocumentAndContent(String folderParent, String className, String docTitle, String urlContent)
        throws Exception
    {
        log.debug((new StringBuilder("Is Connected!!?")).append(getCESession()).toString());
        Folder folder = (Folder)os.fetchObject("Folder", folderParent, null);
        Document doc = com.filenet.api.core.Factory.Document.createInstance(os, className);
        PropertyTemplateSet set = os.get_PropertyTemplates();
        doc.getProperties().putValue("DocumentTitle", docTitle);
        if(doc.getProperties().isPropertyPresent("ContainmentName"))
        {
            doc.getProperties().putValue("ContainmentName", docTitle);
        }
        if(urlContent != null)
        {
            ContentElementList list = com.filenet.api.core.Factory.ContentElement.createList();
            ContentTransfer cs = com.filenet.api.core.Factory.ContentTransfer.createInstance(os);
            InputStream is = null;
            log.debug("Setting content");
            if(urlContent instanceof String)
            {
                cs.set_ContentType("application/pdf");
                cs.set_RetrievalName((new StringBuilder(String.valueOf(docTitle))).append(".pdf").toString());
                log.debug((new StringBuilder("Set Retrieval name ")).append(docTitle).append(".pdf").toString());
                is = new FileInputStream(urlContent);
                cs.setCaptureSource(is);
                list.add(cs);
            }
            doc.set_ContentElements(list);
        }
        doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
        try
        {
            doc.save(RefreshMode.REFRESH);
        }
        catch(Exception e)
        {
            log.debug(e);
        }
        AutoUniqueName an = AutoUniqueName.AUTO_UNIQUE;
        folder.file(doc, an, docTitle, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE).save(RefreshMode.REFRESH);
        return doc;
    }

    private static void finalizarFlujo(String enlace, UtilProcessEngine pe, String rad, String login)
    {
        finalizarFlujo("Gestores", "Asignar Correspondencia", enlace, pe, rad, login);
        finalizarFlujo("Inbox(0)", "Tramitar Correspondencia", enlace, pe, rad, login);
    }

    private static void finalizarFlujo(String nombreCola, String nombrePaso, String enlace, UtilProcessEngine pe, String rad, String login)
    {
        pe.setQueueName(nombreCola);
        String filter = (new StringBuilder("Radicado ='")).append(enlace).append("'").toString();
        try
        {
            List ls = pe.getStepsProcesses(filter);
            System.out.println((new StringBuilder(String.valueOf(filter))).append(" ").append(ls.size()).toString());
            if(ls != null)
            {
                for(Iterator iterator = ls.iterator(); iterator.hasNext();)
                {
                    VWStepElement element = (VWStepElement)iterator.next();
                    if(element.getStepName().equalsIgnoreCase(nombrePaso))
                    {
                        try
                        {
                            element.doLock(true);
                        }
                        catch(Exception exception) { }
                        if(element.getStepResponses() != null && element.getStepResponses().length > 0)
                        {
                            element.setSelectedResponse("Fin de tr\341mite");
                            element.doDispatch();
                            System.out.println((new StringBuilder("Avanzado ")).append(enlace).toString());
                            
                        }
                    } else
                    {
                        System.out.println("Flujo no se encuentra en el paso de Asignar Correspondencia");
                    }
                }

            }
            agregarHistorico(enlace, (new StringBuilder("Flujo finalizado por radicado: ")).append(rad).toString(), login);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private static void agregarHistorico(String rad, String comment, String user)
    {
        Session session = null;
        try
        {
            ApplicationContext actx = ApplicationContextProvider.appContext;
            SessionFactory factory = (SessionFactory)actx.getBean("sessionFactory");
            session = factory.openSession();
            String query = (new StringBuilder("SELECT IDTASK FROM dbo.Tbl_DocumentTaskId where NUMRAD='")).append(rad).append("'").toString();
            String idTask = (String)session.createSQLQuery(query).list().get(0);
            String exec = "{call SP_INSERTARHISTORIA(?,?,?,?,?,'','')}";
            Date today = new Date();
            CallableStatement callableStatement = session.connection().prepareCall(exec);
            System.out.println(today);
            callableStatement.setTimestamp(1, new Timestamp(today.getTime()));
            callableStatement.setString(2, user);
            callableStatement.setString(3, comment);
            callableStatement.setString(4, idTask);
            callableStatement.setString(5, "PERSONALIZADO");
            callableStatement.execute();
            callableStatement.close();
            System.out.println("DONE...");
        }
        catch(Exception ex)
        {
            System.out.println("ERROR...");
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        try
        {
            session.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

}
