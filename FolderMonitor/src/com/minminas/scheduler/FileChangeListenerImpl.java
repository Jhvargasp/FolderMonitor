package com.minminas.scheduler;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.*;
import com.filenet.api.core.*;
import com.filenet.api.property.Properties;
import com.filenet.api.query.*;
import com.filenet.api.util.Id;
import com.grupointent.filemonitor.FileChangeListener;
import com.grupointent.filenet.ConexionPE;
import filenet.vw.api.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import org.apache.log4j.Logger;

public class FileChangeListenerImpl implements FileChangeListener {

	private static Logger log = Logger.getLogger(FileChangeListenerImpl.class);
	private static ResourceBundle bundle = ResourceBundle.getBundle("filenet");

	public FileChangeListenerImpl() {
	}

	public void fileChanged(File file) {
		log.debug((new StringBuilder()).append(file.getName()).append(" changed!").toString());
		try {
			asociaDocumentos(file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e);
		}
	}

	private void asociaDocumentos(String name) throws Exception {
		String folder = name;
		File f = new File(folder);
		File list[] = f.listFiles();
		File afile[] = list;
		int i = afile.length;
		for (int j = 0; j < i; j++) {
			File file = afile[j];
			asociaDocumentoFilenet(file.getAbsolutePath());
		}

	}

	public static void main(String args[]) {
		String p = bundle.getString("querySQL");
		p = p.replaceAll("\\{FILENAME\\}", "111");
		FileChangeListenerImpl imp = new FileChangeListenerImpl();
		imp.asociaDocumentoFilenet("c:/logs/2014000019.pdf");
	}

	private void asociaDocumentoFilenet(String absolutePath) {
		log.debug("Obteniendo conexion Content");
		new ConexionPE();
		Object obj[] = ConexionPE.getSession(bundle.getString("url"), bundle.getString("usr"), bundle.getString("pwd"),
				bundle.getString("context"), bundle.getString("wasp"), bundle.getString("region"),
				bundle.getString("objectStore"));
		ObjectStore store = (ObjectStore) obj[1];
		VWSession vwsess = (VWSession) obj[0];
		String name = (new File(absolutePath)).getName().split("\\.")[0];
		log.debug(name);
		List ls = buscarDocPorRadicado(store, name);
		if (ls != null && ls.size() > 0) {
			Document doc;
			Iterator iterator = ls.iterator();
			while (iterator.hasNext()) {
				doc = (Document) iterator.next();
				updateContent(vwsess, name, doc, store, absolutePath);
				Double size = doc.get_ContentSize();
				if (size == null || size.doubleValue() == 0.0D) {
					log.debug((new StringBuilder()).append("El documento esta sin contenido:").append(name).toString());
				} else {
					log.debug(
							(new StringBuilder()).append("El documento ya tiene contenido, se procede a actualizacion:")
									.append(name).toString());
				}
			}

		} else {
			log.debug((new StringBuilder()).append("No existe un documento con Radicado:").append(name).toString());
		}
		try {
			vwsess.logoff();
		} catch (VWException e) {
			log.debug(e);
		}
	}

	private void avanzarFlujo(VWSession vwSession, String name, Document d) {
		log.debug((new StringBuilder()).append("Buscando flujo para avanzar SALIENTE:").append(name).toString());
		try {
			VWRoster vwRoster = vwSession.getRoster(bundle.getString("rosterName"));
			String sql = bundle.getString("queryRoster");
			sql = sql.replaceAll("\\{FILENAME\\}", name);
			VWRosterQuery rQuery = vwRoster.createQuery(null, null, null, 0, sql, null, 4);
			log.debug("VALIDA ROSTER");
			if (rQuery.hasNext()) {
				while (rQuery.hasNext()) {
					VWRosterElement rElem = (VWRosterElement) rQuery.next();
					VWWorkObject wo = rElem.fetchWorkObject(true, true);
					updateDocumentStep(wo, d);
					log.debug((new StringBuilder()).append("validando paso ").append(wo.getStepName()).toString());
					if ("Asociar documento comunicaciones".equalsIgnoreCase(wo.getStepName())
							|| "Asociar documento saliente".equalsIgnoreCase(wo.getStepName())) {
						log.debug("Flujo encontrado en paso -Asociar documento comunicaciones-");
						wo.doLock(true);
						wo.doDispatch();
						log.debug("Flujo Avanzado");
					} else {
						log.debug("Flujo encontrado, pero no esta en paso requerido <Asociar documento comunicacion"
								+ "es>");
					}
				}
			}
		} catch (Exception e) {
			log.debug(e);
		}
		log.debug("Busqueda finalizada");
	}

	private void avanzarFlujoCorrespondencia(VWSession vwSession, String name, Document d) {
		log.debug((new StringBuilder()).append("Buscando flujo para avanzar ENTRANTE:").append(name).toString());
		log.debug("ENTRA ROSTER CORRESPONDENCIA");
		try {
			VWRoster vwRoster = vwSession.getRoster(bundle.getString("rosterNameCorrespondencia"));
			String sql = bundle.getString("queryRoster");
			sql = sql.replaceAll("\\{FILENAME\\}", name);
			VWRosterQuery rQuery = vwRoster.createQuery(null, null, null, 0, sql, null, 4);
			if (rQuery.hasNext()) {
				while (rQuery.hasNext()) {
					VWRosterElement rElem = (VWRosterElement) rQuery.next();
					VWWorkObject wo = rElem.fetchWorkObject(true, true);
					log.debug((new StringBuilder()).append("validando paso ").append(wo.getStepName()).toString());
					updateDocumentStep(wo, d);
					if ("Asociar documento comunicaciones".equalsIgnoreCase(wo.getStepName())
							|| "Asociar documento saliente".equalsIgnoreCase(wo.getStepName())) {
						log.debug("Flujo encontrado en paso -Asociar documento comunicaciones-");
						wo.doLock(true);
						wo.doDispatch();
						log.debug("Flujo Avanzado");
					} else {
						log.debug("Flujo encontrado, pero no esta en paso requerido <Asociar documento comunicacion"
								+ "es>");
					}
				}
			}

		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		try {
			avanzarFlujo(vwSession, name, d);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		try {
			avanzarFlujoQGestores(vwSession, name, d);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		log.debug("Busqueda finalizada");
	}

	private void avanzarFlujoQGestores(VWSession vwSession, String name, Document d) {
		log.debug((new StringBuilder()).append("Buscando flujo para avanzar ENTRANTE:").append(name).toString());
		log.debug("ENTRA ROSTER Gestores");
		try {
			VWRoster vwRoster = vwSession.getRoster(bundle.getString("rosterNameCorrespondencia"));
			String sql = bundle.getString("queryRoster");
			sql = sql.replaceAll("\\{FILENAME\\}", name);
			VWRosterQuery rQuery = vwRoster.createQuery(null, null, null, 0, sql, null, 4);
			if (rQuery.hasNext()) {
				while (rQuery.hasNext()) {
					VWRosterElement rElem = (VWRosterElement) rQuery.next();
					VWWorkObject wo = rElem.fetchWorkObject(true, true);
					log.debug((new StringBuilder()).append("validando paso ").append(wo.getStepName()).toString());
					updateDocumentStep(wo, d);
					if ("Correspondencia sin imagen".equalsIgnoreCase(wo.getStepName())) {
						log.debug("Flujo encontrado en paso -Correspondencia sin imagen-");
						wo.doLock(true);
						// change 23 marzo 2016
						try {
							wo.setFieldValue("ImagenEsDummie", false, false);
							log.debug("ImagenEsDummie setted in flow!");
						} catch (Exception ex1) {
							log.debug("Problem setting ImagenEsDummie in flow");
							ex1.printStackTrace();
						}
						wo.doDispatch();
						log.debug("Flujo Avanzado");
					} else {
						log.debug("Flujo encontrado, pero no esta en paso requerido <Asociar documento comunicacion"
								+ "es>");
					}
				}
			} else {
				log.debug("ENTRA ROSTER SALIENTE");
				avanzarFlujo(vwSession, name, d);
			}
		} catch (Exception e) {
			avanzarFlujo(vwSession, name, d);
			log.debug(e.getMessage());
		}
		log.debug("Busqueda finalizada");
	}

	private void updateDocumentStep(VWWorkObject wo, Document d) throws Exception {
		log.debug((new StringBuilder()).append("Buscando attachment ").append(bundle.getString("datafieldName"))
				.toString());
		if (wo.hasFieldName(bundle.getString("datafieldName"))) {
			VWAttachment att = (VWAttachment) wo.getFieldValue(bundle.getString("datafieldName"));
			VWAttachment attachment = new VWAttachment();
			attachment.setAttachmentName(d.get_Name());
			attachment.setId(d.get_VersionSeries().get_Id().toString());
			attachment.setType(3);
			attachment.setLibraryName(bundle.getString("objectStore"));
			attachment.setLibraryType(att.getLibraryType());
			wo.doLock(true);
			wo.setFieldValue(bundle.getString("datafieldName"), attachment, false);
			wo.doSave(true);
			log.debug("VALIDANDO attachment  ");
		}
		log.debug("Buscando attachment finalizado ");
	}

	private void borrarFs(String absolutePath) {
		log.debug((new StringBuilder()).append("Se procede con el borrado del documento:").append(absolutePath)
				.toString());
		(new File(absolutePath)).delete();
	}

	private void updateContent(VWSession vwSession, String name, Document doc, ObjectStore store, String absolutePath) {
		try {
			log.debug((new StringBuilder()).append("checkout>").append(doc.get_IsReserved()).toString());
			if (!doc.get_IsReserved().booleanValue()) {
				try {
					doc.checkout(ReservationType.EXCLUSIVE, null, doc.getClassName(), null);
					doc.save(RefreshMode.REFRESH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			log.debug("reserv");
			Document reservation = (Document) doc.get_Reservation();
			File internalFile = new File(absolutePath);
			log.debug((new StringBuilder()).append("Archivo existe?").append(internalFile.exists()).toString());
			ContentTransfer ctObject = com.filenet.api.core.Factory.ContentTransfer.createInstance();
			FileInputStream fileIS = new FileInputStream(internalFile.getAbsolutePath());
			ContentElementList contentList = com.filenet.api.core.Factory.ContentTransfer.createList();
			ctObject.setCaptureSource(fileIS);
			ctObject.set_ContentType("application/pdf");
			contentList.add(ctObject);
			reservation.set_ContentElements(contentList);
			// change 23/03/2016
			if (reservation.getProperties().isPropertyPresent("ImagenEsDummie")) {
				reservation.getProperties().putValue("ImagenEsDummie", false);
				log.debug("ImagenEsDummie..setted");
			} else {
				log.debug("ImagenEsDummie..not exist.. can not be setted");
			}
			try {
				reservation.setUpdateSequenceNumber(null);
				reservation.save(RefreshMode.REFRESH);
				log.debug("saved");
			} catch (Exception e) {
				log.debug(e.getMessage());
				e.printStackTrace();
			}
			try {
				reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
				reservation.setUpdateSequenceNumber(null);
				reservation.save(RefreshMode.REFRESH);
			} catch (Exception e) {
				log.debug(e.getMessage());
				e.printStackTrace();
			}
			log.debug("checkin");
			avanzarFlujoCorrespondencia(vwSession, name, doc);
			borrarFs(absolutePath);
		} catch (Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	private List buscarDocPorRadicado(ObjectStore store, String name) {
		SearchSQL sqlObject = new SearchSQL();
		List ls = new ArrayList();
		sqlObject.setMaxRecords(100);
		String sql = bundle.getString("querySQL");
		sql = sql.replaceAll("\\{FILENAME\\}", name);
		sqlObject.setQueryString(sql);
		log.debug(sql);
		SearchScope searchScope = new SearchScope(store);
		RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
		if (!rowSet.isEmpty()) {
			Document d;
			Iterator it = rowSet.iterator();
			while (it.hasNext()) {
				d = null;
				RepositoryRow element = (RepositoryRow) it.next();
				Id idEl = element.getProperties().getIdValue("Id");
				log.debug(idEl.toString());
				d = com.filenet.api.core.Factory.Document.fetchInstance(store, idEl, null);
				d.refresh();
				ls.add(d);
				log.debug(d.get_ContentSize());
				log.debug(d.getProperties());
				log.debug((new StringBuilder()).append("Documento >").append(d).toString());
			}

		}
		return ls;
	}
}
