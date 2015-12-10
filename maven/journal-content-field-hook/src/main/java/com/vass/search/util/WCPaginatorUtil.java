package com.vass.search.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.vass.search.WebContentPage;
import com.vass.search.WebContentPagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WCPaginatorUtil {

	public static WebContentPagination pagiFirst(int delta, int page) {
		
		WebContentPagination result = new WebContentPagination();
		result.setDelta(delta);
		result.setPage(page);
		result.setStart(delta * (page - 1));
		result.setEnd(result.getStart() + delta - 1);
		
		return result;
	}

	public static WebContentPagination pagiSecond(WebContentPagination pagi, int total, Locale locale) {
		
		// total
		
		pagi.setTotal(total);
		
		// numPages
		
		int numPages = 1;
		if (pagi.getTotal() > pagi.getDelta()) {
			
			numPages = pagi.getTotal() / pagi.getDelta();
			
			if ((pagi.getTotal() % pagi.getDelta()) != 0) {
				numPages = numPages + 1;
			}
			
		}
		
		pagi.setNumPages(numPages);
		
		// isFirst
		
		pagi.setFirst(pagi.getPage() == 1);
		
		// isLast
		
		pagi.setLast(pagi.getPage() == pagi.getNumPages());
		
		// check end
		
		if (pagi.isLast() && pagi.getEnd() > pagi.getTotal()) {
			pagi.setEnd(pagi.getTotal() -1);
		}
		
		// Deltas
		
		int range1 = 4;
		int range1M = 5;
		int range2 = 3;
		int range2M = 15;
		List<Integer> deltas = new ArrayList<Integer>();
		
		for (int i = 1; i <= range1; i++) {
			deltas.add(i * range1M);
		}
		
		for (int i = 1; i <= range2; i++) {
			deltas.add( (i * range2M) + (range1 * range1M) );
		}
		
		pagi.setDeltas(deltas);
		
		// pageList
		
		List<WebContentPage> pageList = new ArrayList<WebContentPage>();
		
		WebContentPage firstPage = new WebContentPage(
			pagi.isFirst() ? -1 : 1, // page
			StringPool.LESS_THAN + StringPool.LESS_THAN, //name
			pagi.isFirst() ? true : false); //disabled
		
		WebContentPage previous = new WebContentPage(
			pagi.isFirst() ? -1 : pagi.getPage() - 1, // page
			StringPool.LESS_THAN, //name
			pagi.isFirst() ? true : false); //disabled
		
		WebContentPage next = new WebContentPage(
			pagi.isLast() ? -1 : pagi.getPage() + 1, // page
			StringPool.GREATER_THAN, //name
			pagi.isLast() ? true : false); //disabled
		
		WebContentPage lastPage = new WebContentPage(
			pagi.isLast() ? -1 : pagi.getNumPages(), // page
			StringPool.GREATER_THAN + StringPool.GREATER_THAN, //name
			pagi.isLast() ? true : false); //disabled
		
		pageList.add(firstPage);
		pageList.add(previous);
		
		int maxRange = 2;
		int rFrom = pagi.getPage() - maxRange;
		int rFromPre = rFrom - 1;
		int rTo = pagi.getPage() + maxRange;
		int rToPost = rTo + 1;
		
		for (int i = rFrom; i <= rTo; i++) {
			
			if (i == rFrom && rFromPre > 0 ) {
				pageList.add(new WebContentPage(
					rFromPre, StringPool.PERIOD + StringPool.PERIOD, false));
			}
			
			if (i > 0 && i <= pagi.getNumPages()) {
				pageList.add(new WebContentPage(
					i, String.valueOf(i), i == pagi.getPage()));
			}
			
			if (i == rTo && rToPost <= pagi.getNumPages()) {
				pageList.add(new WebContentPage(
					rToPost, StringPool.PERIOD + StringPool.PERIOD, false));
			}
			
		}
		
		pageList.add(next);
		pageList.add(lastPage);
		
		pagi.setPageList(pageList);
		
		// currPos
		
		int currP1 = (pagi.getEnd() - pagi.getStart()) + 1;
		int currP2 = pagi.getTotal();
		int currP3 = pagi.getStart() + 1;
		int currP4 = pagi.getEnd() + 1;
		
		String currPos = LanguageUtil.format(locale, WCS_CURR_POS, 
				new Object[]{currP1, currP2, currP3, currP4});
		
		pagi.setCurrPos(currPos);
		
		// totalPages
		
		String totalPages = LanguageUtil.format(locale, WCS_TOTAL_PAGES, 
				new Object[]{pagi.getNumPages()});
		
		pagi.setTotalPages(totalPages);
		
		return pagi;
	}
	
	private static final String WCS_CURR_POS = "wcs.curr-pos";
	private static final String WCS_TOTAL_PAGES = "wcs.totalPag";
	
}
