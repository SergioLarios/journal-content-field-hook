package com.vass.reindex.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.vass.lar.WcLarUtil;
import com.vass.reindex.ReindexForm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ReindexUtil {

	private static Log _log = LogFactoryUtil.getLog(ReindexUtil.class);
	
	public static Company getCompany(long companyId) throws Exception {
		
		Company company = null;
		
		if (companyId < 0) {
			company = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
		}
		else {
			company = CompanyLocalServiceUtil.getCompany(companyId);
		}
		
		return company;
	}
	
	public static List<Group> getNormalGroups(Company com) {
		
		List<Group> result = new ArrayList<Group>();
		
		try {
			
			List<Group> groups =GroupLocalServiceUtil.getGroups(
					com.getCompanyId(), Group.class.getName(), 0);
			
			for (Group group : groups) {
				
				if (!CONTROL_PANEL.equals(group.getFriendlyURL()) &&
					( (group.isStaged() && !group.hasStagingGroup()) ||
						(!group.isStaged() && !group.hasStagingGroup()))) {
				
					result.add(group);
					
				}	
				
			}
			
			return result;
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		
		return result;
	}
	
	public static List<Group> getLiveGroups(Company com) {
		
		List<Group> result = new ArrayList<Group>();
		
		try {
			
			List<Group> groups = GroupLocalServiceUtil.getGroups(
					com.getCompanyId(), Group.class.getName(), 0);
			
			for (Group group : groups) {
				if (group.isStaged() && group.hasStagingGroup()) {
					group.setName(group.getStagingGroup().getName().
							replace(STAGING, StringPool.BLANK));
					result.add(group);
				}
			}
			
			Group gGroup = com.getGroup();
			if (gGroup.isStaged() && gGroup.hasStagingGroup()) {
				gGroup.setName(gGroup.getStagingGroup().getName().
						replace(STAGING, StringPool.BLANK));
				result.add(gGroup);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		return result;
	}
	
	public static void executeAction(HttpServletRequest request, Company com) {
		
		// Params
		
		long normalGroup = ParamUtil.getLong(request, NORMAL_GROUP, -1);
		long liveGroup = ParamUtil.getLong(request, LIVE_GROUP, -1);
		ReindexForm form = null;
		
		// Reindex
		
		if (normalGroup > 0) {
			
			form = reindexGroup(normalGroup, com);
			
		}
		else if (liveGroup > 0) {
			
			form = reindexGroup(liveGroup, com);
			
		}
		else {
			form = new ReindexForm();
		}
		
		// Attributes
		
		request.setAttribute(FORM, form);
		
	}
	
	private static ReindexForm reindexGroup(long executedGroup, Company com) {
		
		ReindexForm result = new ReindexForm();
		
		result.setExecuted(true);
		result.setExecutedGroup(executedGroup);
		
		try {
			
			List<DDMStructure> gStrucutre = DDMStructureLocalServiceUtil.getStructures(executedGroup, 
					ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class));
			Map<String, JournalArticle> jaCache = new HashMap<String, JournalArticle>();
			long globalGroupId = WcLarUtil.getGlobalIdIfStaged(executedGroup, com);
			
			for (DDMStructure ddmStructure : gStrucutre) {
				
				if (hasWcField(ddmStructure)) {
					
					List<JournalArticle> strcArticles = JournalArticleLocalServiceUtil.
							getStructureArticles(executedGroup, ddmStructure.getStructureKey());
					
					for (JournalArticle ja : strcArticles) {
						
						WcLarUtil.updateJaWcontent(
								executedGroup, globalGroupId, ja.getUrlTitle(), jaCache, false);
						
					}
					
				}
				
			}

			result.setSuccess(true);
			
		} catch (Exception e) {
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			result.setSuccess(false);
			result.setErrMsg(sw.toString());
			
			_log.error(e);
			
		}
		
		return result;
	}

	private static boolean hasWcField(DDMStructure strc) throws Exception {
		
		String xml = strc.getCompleteXsd();
		
		Document contentDoc = SAXReaderUtil.read(xml, false);
		
		List<Node> nodes = contentDoc.selectNodes(DDM_WEB_CONTENT);
		
		if (nodes.size() > 0) {
			return true;
		} else {
			return false;
		}
		
	}

	private static final String CONTROL_PANEL = "/control_panel";
	private static final String STAGING = " (Staging)";
	private static final String NORMAL_GROUP = "normal_group";
	private static final String LIVE_GROUP = "live_group";
	private static final String FORM = "form";
	private static final String DDM_WEB_CONTENT = "//dynamic-element[@type='ddm-web-content']";
	
}
