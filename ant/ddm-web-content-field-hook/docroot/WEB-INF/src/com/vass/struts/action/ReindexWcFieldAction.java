package com.vass.struts.action;

import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.vass.reindex.ReindexStrs;
import com.vass.reindex.util.ReindexLocaleUtil;
import com.vass.reindex.util.ReindexUtil;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReindexWcFieldAction extends BaseStrutsAction {
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Params
		
		Locale locale = LocaleUtil.fromLanguageId(
				ParamUtil.getString(request, LOCALE, DEF_LOCALE));
		long companyId = ParamUtil.getLong(request, COMPANY_ID, -1);
		
		// Objects
		
		ReindexStrs messages = ReindexLocaleUtil.translate(locale);
		
		Company com = ReindexUtil.getCompany(companyId);
		List<Group> normalGroups = ReindexUtil.getNormalGroups(com);
		List<Group> liveGroups = ReindexUtil.getLiveGroups(com);
		
		// Execute action
		
		ReindexUtil.executeAction(request, com);
		
		// Attributes
		
		request.setAttribute(MESSAGES, messages);
		request.setAttribute(NORMAL_GROUPS, normalGroups);
		request.setAttribute(LIVE_GROUPS, liveGroups);
		
		return JSP_PATH;
	}
	
	private static final String JSP_PATH = "/portal/wc_field_reindex/wcf_reindex.jsp";
	
	private static final String LOCALE = "locale";
	private static final String MESSAGES = "messages";
	private static final String COMPANY_ID = "companyId";
	
	private static final String NORMAL_GROUPS = "normalGroups";
	private static final String LIVE_GROUPS = "liveGroups";
	
	private static final String DEF_LOCALE = "en_US";



}
