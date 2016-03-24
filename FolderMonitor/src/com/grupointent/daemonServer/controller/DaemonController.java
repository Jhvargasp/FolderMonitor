package com.grupointent.daemonServer.controller;

import com.grupointent.daemonServer.beans.DaemonBean;
import com.grupointent.daemonServer.beans.DaemonInterface;
import com.grupointent.genericapp.context.ApplicationContextProvider;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.ModelMap;

public class DaemonController {

	private List daemonList;
	private final String JSP = "/generic/principal";
	private static Logger log = Logger.getLogger(DaemonController.class);
	static Object obj;

	public DaemonController() {
		log.debug("Ready Controller....");
		ApplicationContext actx = ApplicationContextProvider.appContext;
		daemonList = (List) actx.getBean("daemonList");
		startDefault();
	}

	private void startDefault() {
		DaemonBean bean = null;
		for (Iterator iterator = daemonList.iterator(); iterator.hasNext();) {
			DaemonBean b = (DaemonBean) iterator.next();
			bean = b;
		}

		try {
			Class c = Class.forName(bean.getClassToLaunch());
			if (obj == null) {
				DaemonInterface o = (DaemonInterface) c.newInstance();
				o.start();
				obj = o;
			} else {
				DaemonInterface o = (DaemonInterface) obj;
				o.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String viewDaemons(ModelMap model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		model.addAttribute("list", daemonList);
		return "/generic/list";
	}

	public String startDaemon(String dId, ModelMap model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		DaemonBean bean = null;
		for (Iterator iterator = daemonList.iterator(); iterator.hasNext();) {
			DaemonBean b = (DaemonBean) iterator.next();
			if (dId.equalsIgnoreCase(b.getName())) {
				bean = b;
			}
		}

		try {
			Class c = Class.forName(bean.getClassToLaunch());
			if (obj == null) {
				DaemonInterface o = (DaemonInterface) c.newInstance();
				o.start();
				obj = o;
			} else {
				DaemonInterface o = (DaemonInterface) obj;
				o.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("list", daemonList);
		return "/generic/list";
	}

	public String stopDaemon(String dId, ModelMap model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		DaemonBean bean = null;
		for (Iterator iterator = daemonList.iterator(); iterator.hasNext();) {
			DaemonBean b = (DaemonBean) iterator.next();
			if (dId.equalsIgnoreCase(b.getName())) {
				bean = b;
			}
		}

		try {
			Class c = Class.forName(bean.getClassToLaunch());
			if (obj != null) {
				DaemonInterface o = (DaemonInterface) obj;
				o.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("list", daemonList);
		return "/generic/list";
	}

}
