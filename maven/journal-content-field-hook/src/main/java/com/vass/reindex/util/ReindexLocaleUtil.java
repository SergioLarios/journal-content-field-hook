package com.vass.reindex.util;

import static com.vass.reindex.ReindexStrs.*;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.vass.reindex.ReindexStrs;

import java.util.Locale;

public class ReindexLocaleUtil {

	public static ReindexStrs translate(Locale l) {
		
		ReindexStrs result = new ReindexStrs();
		result.setPageTitle(tl(l, PAGE_TITLE));
		result.setHeader(tl(l, HEADER));
		result.setExplanation(tl(l, EXPLANATION));
		result.sethNormal(tl(l, H_NORMAL));
		result.setpNormal(tl(l, P_NORMAL));
		result.setLabelNormal(tl(l, LABEL_NORMAL));
		result.sethStaging(tl(l, H_STAGING));
		result.setpStaging(tl(l, P_STAGING));
		result.setLabelStaging(tl(l, LABEL_STAGING));
		result.setSave(tl(l, SAVE));
		result.setSelecSite(tl(l, SELECT_SITE));
		result.setFormError(tl(l, FORM_ERROR));
		result.setFormSuccess(tl(l, FORM_SUCESS));
		
		return result;
	}
	
	private static String tl(Locale l, String key) {
		return LanguageUtil.get(l, key);
	}

}
