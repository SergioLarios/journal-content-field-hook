package com.vass.search.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.vass.search.WebContentStrs;

import static com.vass.search.WebContentStrs.*;

import java.util.Locale;

public class WCLocaleUtil {

	public static WebContentStrs translate(Locale l) {
		
		WebContentStrs result = new WebContentStrs();
		result.setChoose(tl(l, CHOOSE));
		result.setCurrPos(tl(l, CURR_POS));
		result.setGroup(tl(l, GROUP));
		result.setId(tl(l, ID));
		result.setLastMod(tl(l, LAST_MOD));
		result.setPageTitle(tl(l, PAGE_TITLE));
		result.setPlaceHolder(tl(l, PLACE_HOLDER));
		result.setSearch(tl(l, SEARCH));
		result.setStrct(tl(l, STRCUTURE));
		result.setTitle(tl(l, TITLE));
		result.setTooltip(tl(l, TOOLTIP));
		result.setUser(tl(l, USER));
		result.setResPage(tl(l, RES_PAGE));
		result.setNoStrucutre(tl(l, NO_STRUCUTRE));
		
		return result;
	}
	
	private static String tl(Locale l, String key) {
		return LanguageUtil.get(l, key);
	}

}
