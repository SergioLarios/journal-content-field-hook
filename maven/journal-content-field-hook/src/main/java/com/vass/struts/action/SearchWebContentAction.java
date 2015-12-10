package com.vass.struts.action;

import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.vass.search.WebContentPagination;
import com.vass.search.WebContentSearchResults;
import com.vass.search.WebContentStrs;
import com.vass.search.util.WCLocaleUtil;
import com.vass.search.util.WCPaginatorUtil;
import com.vass.search.util.WCSearchUtil;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchWebContentAction extends BaseStrutsAction {
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Params
		
		long groupId = ParamUtil.getLong(request, GROUP_ID);
		int delta = ParamUtil.getInteger(request, DELTA, DEF_DELTA);
		int page = ParamUtil.getInteger(request, PAGE, DEF_PAGE);
		String searchStr = ParamUtil.getString(request, SEARCH_STR, StringPool.BLANK);
		long structure = ParamUtil.getLong(request, STRUCTURE, -1);
		boolean hideStructure = ParamUtil.getBoolean(request, HIDE_STRUCTURE);
		Locale locale = LocaleUtil.fromLanguageId(
				ParamUtil.getString(request, LOCALE, DEF_LOCALE));

		// Objects
		
		WebContentPagination pagination = WCPaginatorUtil.pagiFirst(delta, page);
		
		long globalGroupId = WCSearchUtil.getGlobalGroupId(groupId);
		Map<Long, String> structureNames = WCSearchUtil.getStructureNames(groupId, globalGroupId, locale);
		
		WebContentSearchResults resulsts = WCSearchUtil.search(
				groupId, globalGroupId, searchStr, structure,
				structureNames, pagination.getStart(), pagination.getEnd(), locale);

		pagination = WCPaginatorUtil.pagiSecond(pagination, resulsts.getTotal(), locale);
		
		WebContentStrs messages = WCLocaleUtil.translate(locale);
		
		// Attributes
		
		request.setAttribute(GROUP_ID, groupId);
		request.setAttribute(SEARCH_STR, searchStr);
		request.setAttribute(RESULTS, resulsts);
		request.setAttribute(PAGINATION, pagination);
		request.setAttribute(MESSAGES, messages);
		request.setAttribute(LOCALE, locale);
		request.setAttribute(STRUCTURE, structure);
		request.setAttribute(STRUCTURE_NAMES, structureNames);
		request.setAttribute(HIDE_STRUCTURE, hideStructure);
		
		return JSP_PATH;
	}
	
	private static final String JSP_PATH = "/portal/custom_search/web_content.jsp";
	
	private static final String GROUP_ID = "groupId";
	private static final String DELTA = "delta";
	private static final String PAGE = "page";
	private static final String LOCALE = "locale";
	private static final String PAGINATION = "pagination";
	private static final String SEARCH_STR = "searchStr";
	private static final String HIDE_STRUCTURE = "hideStructure";
	private static final String STRUCTURE = "structure";
	private static final String STRUCTURE_NAMES = "structureNames";
	private static final String RESULTS = "resulsts";
	private static final String MESSAGES = "messages";
	
	private static final int DEF_DELTA = 10;
	private static final int DEF_PAGE = 1;
	private static final String DEF_LOCALE = "en_US";

}
