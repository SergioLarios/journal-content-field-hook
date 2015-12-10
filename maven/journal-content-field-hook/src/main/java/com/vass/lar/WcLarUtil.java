package com.vass.lar;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactoryUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.journal.NoSuchArticleException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WcLarUtil {

	private static Log _log = LogFactoryUtil.getLog(WcLarUtil.class);
	
	public static int processLar(InputStream stream, long targetGroupId) {
		try {
			return processLar(ZipReaderFactoryUtil.getZipReader(stream), targetGroupId);
		} catch (Exception e) {
			return 0;
		}
		
	}
	
	public static int processLar(File file, long targetGroupId) {
		try {
			return processLar(ZipReaderFactoryUtil.getZipReader(file), targetGroupId);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int processLar(ZipReader zipReader, long targetGroupId) {
		
		int result = 0;
		
		try {
			
			Document doc = SAXReaderUtil.read(zipReader.getEntryAsString(MANIFEST), false);
			Element root = doc.getRootElement();
			
			long originalGroupId = getGId(root);
			
			Company com = CompanyLocalServiceUtil.getCompany(
					GroupLocalServiceUtil.getGroup(targetGroupId).getCompanyId());
			long targetGlobalGroupId = getGlobalIdIfStaged(targetGroupId, com);
			
			// Articles
			
			List<String> parthArticles = zipReader.getFolderEntries(String.format(P_ARTICLES, originalGroupId));
			result = parthArticles.size();
			
			Map<String, JournalArticle> jaCache = new HashMap<String, JournalArticle>();
			
			for (String path : parthArticles) {
				processJaXml(zipReader.getEntryAsString(path), targetGroupId, targetGlobalGroupId, jaCache);
			}
			
			// Structures

			List<String> parthStructures = zipReader.getFolderEntries(String.format(P_STRUCTURES, originalGroupId));
			
			for (String pStrc : parthStructures) {
				processStrcXml(zipReader.getEntryAsString(pStrc), targetGroupId, targetGlobalGroupId, jaCache);
			}
			
			zipReader.close();
			
		} catch (Exception e) {
			_log.error(ERROR_MSG, e);
		}
		
		return result;
		
	}
	
	public static long getGlobalIdIfStaged(long gId, Company com) throws Exception {
		
		Group group = GroupLocalServiceUtil.getGroup(gId);
		Group globalG = com.getGroup();
		
		// Live group
		if (group.hasStagingGroup() && group.isStaged()) {
			
			return globalG.getGroupId();
			
		}
		// Normal or staging group
		else {
			
			// Global with staging disabled
			if (!globalG.hasStagingGroup()) {
				return globalG.getGroupId();
			}
			// Stagin enabled
			else {
				return globalG.getStagingGroup().getGroupId();
			}
			
		}
		
	}
	
	private static long getGId(Element root) throws Exception {
		
		Node gNode = root.selectSingleNode(P_LAYOUT_SM);
		
		return GetterUtil.getLong(((Element)gNode).attributeValue(A_G_ID));
		
	}
	
	private static void processJaXml(String xml, long targetGroupId, long targetGlobalGroupId,
			Map<String, JournalArticle> jaCache) throws Exception {
		
		Document doc = SAXReaderUtil.read(xml, false);
		Element root = doc.getRootElement();
		
		String content = root.selectSingleNode(XP_CONTENT).getText();
		
		Document contentDoc = SAXReaderUtil.read(content, false);
		
		List<Node> nodes = contentDoc.selectNodes(XP_DDM_WC);
		
		if (nodes.size() > 0) {
			
			String urlTitle = root.selectSingleNode(XP_URL_TITLE).getText();

			updateJaWcontent(targetGroupId, targetGlobalGroupId, urlTitle, jaCache, true);
			
		}
		
	}
	
	private static void processStrcXml(String xml, long targetGroupId, 
			long targetGlobalGroupId, Map<String, JournalArticle> jaCache) {
		
		try {
			
			Document doc = SAXReaderUtil.read(xml, false);
			Element root = doc.getRootElement();
			
			String xsd = root.selectSingleNode(XP_XSD).getText();
			
			Document xsdDoc = SAXReaderUtil.read(xsd, false);
			
			List<Node> nodes = xsdDoc.selectNodes(XP_DDM_WC);
			
			if (nodes.size() > 0) {
				
				String struckey = GetterUtil.getString(root.selectSingleNode(XP_STRUCTURE_KEY).getText());
				long clsnId = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName());
				
				DDMStructure structure = DDMStructureLocalServiceUtil.getStructure(targetGroupId, clsnId, struckey);
				
				xsdDoc = SAXReaderUtil.read(structure.getCompleteXsd(), false);
				nodes = xsdDoc.selectNodes(XP_DDM_WC);
				
				for (Node node : nodes) {
					
					List<Node> childrenNodes = node.selectNodes(XP_PREDEFINED);
					
					for (Node childNode : childrenNodes) {
						
						if (!Validator.isBlank(childNode.getText())) {
							String newJson = getNewJson(childNode, 
									targetGroupId, targetGlobalGroupId, jaCache, true);
							childNode.setText(StringPool.BLANK);
							((Element) childNode).addCDATA(newJson);
						}
						
					}
					
				}
				
				structure.setXsd(xsdDoc.asXML());
				
				DDMStructureLocalServiceUtil.updateDDMStructure(structure);
				
			}
			
		} catch (Exception e) {
			_log.error(ERROR_MSG, e);
		}
		
	}
	
	public static void updateJaWcontent(long targetGroupId, long targetGlobalGroupId, String urlTitle,
			Map<String, JournalArticle> jaCache, boolean hasBeenImported) throws Exception {
		
		JournalArticle jaToEdit = getTargetArticle(urlTitle, targetGroupId, targetGlobalGroupId, jaCache);
		
		Document docToEdit = SAXReaderUtil.read(jaToEdit.getContent(), false);
		List<Node> nodesToEdit = docToEdit.selectNodes(XP_DDM_WC);
		
		processWcFields(nodesToEdit, targetGroupId, targetGlobalGroupId, jaCache, hasBeenImported);
		
		jaToEdit.setContent(docToEdit.asXML());
		
		JournalArticleLocalServiceUtil.updateJournalArticle(jaToEdit);
	}
	
	private static void processWcFields(List<Node> nodes, long targetGroupId, long targetGlobalGroupId,
			Map<String, JournalArticle> jaCache, boolean hasBeenImported) throws Exception {
		
		for (Node node : nodes) {
			
			List<Node> childrenNodes = node.selectNodes(XP_DYNAMIC_CONTENT);
			
			for (Node childNode : childrenNodes) {
				
				String newJson = getNewJson(childNode, 
						targetGroupId, targetGlobalGroupId, jaCache, hasBeenImported);
				childNode.setText(StringPool.BLANK);
				((Element) childNode).addCDATA(newJson);
				
			}
			
		}
		
	}
	
	private static String getNewJson(Node childNode, long targetGroupId, long targetGlobalGroupId,
			Map<String, JournalArticle> jaCache, boolean hasBeenImported) {
		
		try {
			
			String nText = childNode.getText();
			
			if (!Validator.isBlank(nText)) {
				
				JSONObject origArticle = JSONFactoryUtil.createJSONObject(nText);
				
				JournalArticle tArticle = getTargetArticle(
						origArticle.getString(J_URL_TITLE), targetGroupId, targetGlobalGroupId, jaCache);
				
				origArticle.put(J_HAS_BEEN_IMPORTED, hasBeenImported ? true : origArticle.getBoolean(J_HAS_BEEN_IMPORTED));
				
				if (Validator.isNotNull(tArticle)) {
					origArticle.put(J_GROUPD_ID, tArticle.getGroupId());
					origArticle.put(J_CONTENT_ID, tArticle.getArticleId());
					origArticle.put(J_TARGET_CONTENT_AVAILABLE, true);
				}
				else {
					origArticle.put(J_TARGET_CONTENT_AVAILABLE, false);
				}
				
				return origArticle.toString();
				
			}
			else {
				return childNode.getText();
			}
			
		
		} catch (Exception e) {
			_log.error(String.format(ERROR_MSG_FIELD, childNode.asXML(), e.getMessage()));
			return childNode.getText();
		}
	}
	
	private static JournalArticle getTargetArticle(String urlTitle, long targetGroupId, 
			long targetGlobalGroupId, Map<String, JournalArticle> jaCache) {
		
		try {

			String key = targetGroupId + urlTitle;
			String keyGlobal = targetGroupId + urlTitle;
			
			JournalArticle cachedJa = jaCache.get(key);
			JournalArticle cachedJaGlobal = jaCache.get(keyGlobal);
			
			if (Validator.isNotNull(cachedJa)) {
				return cachedJa;
			}
			
			if (Validator.isNotNull(cachedJaGlobal)) {
				return cachedJaGlobal;
			}
			
			try {
				cachedJa = JournalArticleLocalServiceUtil.
						getArticleByUrlTitle(targetGroupId, urlTitle);
				jaCache.put(key, cachedJa);
				
				return cachedJa;
				
			} catch (NoSuchArticleException e) {}
			
			if (Validator.isNull(cachedJa)) {
				
				try {
					
					cachedJaGlobal = JournalArticleLocalServiceUtil.
							getArticleByUrlTitle(targetGlobalGroupId, urlTitle);
					
					jaCache.put(keyGlobal, cachedJaGlobal);
					
					return cachedJaGlobal;
					
				} catch (NoSuchArticleException e) {}
				
			}
			
		}
		catch (Exception e) { 
			_log.error(e);
		}
		
		return null;
	}
	
	private static final String ERROR_MSG = "Error al procesar el lar para revisar los campos -Web content-";
	private static final String ERROR_MSG_FIELD = "Error al procesar el campo : %s  -> %s";
	private static final String MANIFEST = "/manifest.xml";
	private static final String P_LAYOUT_SM = "Layout/staged-model";
	private static final String P_ARTICLES = "/group/%d/" + JournalArticle.class.getName();
	private static final String P_STRUCTURES = "/group/%d/" + DDMStructure.class.getName();
	private static final String A_G_ID = "group-id";
	private static final String XP_CONTENT = "__content";
	private static final String XP_URL_TITLE = "__urlTitle";
	private static final String XP_DDM_WC = "//dynamic-element[@type='ddm-web-content']";
	private static final String XP_DYNAMIC_CONTENT  = "dynamic-content";
	private static final String XP_XSD  = "__xsd";
	private static final String XP_STRUCTURE_KEY  = "__structureKey";
	private static final String XP_PREDEFINED  = "meta-data/entry[@name='predefinedValue']";
	private static final String J_GROUPD_ID = "groupId";
	private static final String J_CONTENT_ID = "contentId";
	private static final String J_URL_TITLE = "urlTitle";
	private static final String J_HAS_BEEN_IMPORTED = "hasBeenImported";
	private static final String J_TARGET_CONTENT_AVAILABLE = "targetContentAvailable";
	
}
