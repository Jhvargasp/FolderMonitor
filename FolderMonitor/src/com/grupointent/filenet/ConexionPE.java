package com.grupointent.filenet;

import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;

import filenet.vw.api.VWException;
import filenet.vw.api.VWSession;
import java.io.PrintStream;
import java.util.Properties;

public class ConexionPE
{

    public ConexionPE()
    {
    }

    public static Object[] getSession(String uri, String usr, String pwd, String context, String wasp, String region, String osName)
    {
        com.filenet.api.core.Connection conn;
        conn = com.filenet.api.core.Factory.Connection.getConnection(uri);
        UserContext uc = UserContext.get();
        javax.security.auth.Subject subject = UserContext.createSubject(conn, usr, pwd, context);
        uc.pushSubject(subject);
        Properties props = System.getProperties();
        if(wasp != null && wasp.length() > 0)
        {
            props.setProperty("wasp.location", wasp);
        }
        System.setProperty("filenet.pe.bootstrap.ceuri", uri);
//        VWSession session = null;
        Object obj[]=null;
        VWSession session;
		try {
			session = new VWSession(region);
			obj = new Object[2];
	        obj[0] = session;
	        com.filenet.api.core.Domain domain = com.filenet.api.core.Factory.Domain.fetchInstance(conn, null, null);
	        com.filenet.api.core.ObjectStore os = com.filenet.api.core.Factory.ObjectStore.fetchInstance(domain, osName, null);
	        System.out.println("FileNet CE Conected!!");
	        obj[1] = os;
		} catch (VWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return obj;
//        Exception e;
//        e;
//        e.printStackTrace();
//        return null;
    }
}
