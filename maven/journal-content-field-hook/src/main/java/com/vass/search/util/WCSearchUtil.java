package com.vass.search.util;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.service.TrashEntryLocalServiceUtil;
import com.vass.search.WebContentResult;
import com.vass.search.WebContentSearchResults;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WCSearchUtil {
	
	private static Log _log = LogFactoryUtil.getLog(WCSearchUtil.class);
	
	public static WebContentSearchResults search(long groupId, long globalGroupId, String searchStr, long structure,
			Map<Long, String> structureNames, int start, int end, Locale locale) {
		
		List<WebContentResult> list = new ArrayList<WebContentResult>();
		int total = 0;
		
		try {
			
			// No search
			
			searchStr = searchStr.trim();
			
			if (Validator.isBlank(searchStr)) {
				
				Disjunction idsDisjunction = createDisjuncion(groupId, globalGroupId, structure);
				DynamicQuery trashDc = getTrashDc(groupId, globalGroupId);
				list.addAll(queryAll(idsDisjunction, trashDc, StringPool.BLANK, start, end +1, locale));
				total = total + queryAllCount(idsDisjunction, trashDc, StringPool.BLANK);
				
			}
			
			// With search String
			
			else {
				
				list = checkIsId(groupId, globalGroupId, searchStr, locale);
				
				if (list.isEmpty()) {
					
					Disjunction idsDisjunction = createDisjuncion(groupId, globalGroupId, structure);
					DynamicQuery trashDc = getTrashDc(groupId, globalGroupId);
					list.addAll(queryAll(idsDisjunction, trashDc, searchStr, start, end, locale));
					total = total + queryAllCount(idsDisjunction, trashDc, searchStr);
					
				}
				else {
					total = list.size();
				}
				
			}
			
			// Translate GroupId & StructureId
			
			translateFields(list, globalGroupId, structureNames, locale);
			
		} catch (Exception e) {
			_log.error(ERROR_MSG, e);
		}
		
		return new WebContentSearchResults(list, total);
	}
	
	public static long getGlobalGroupId(long groupId) throws Exception {
		
		long result = 0;
		
		if (groupId > 0) {
			Company com = CompanyLocalServiceUtil.getCompanyById(
					GroupLocalServiceUtil.getGroup(groupId).getCompanyId());
			
			if (com.getGroup().isStaged()) {
				result = com.getGroup().getStagingGroup().getGroupId();
			}
			else {
				result = com.getGroupId();
			}
			
		}
		
		return result;
	}

	public static Map<Long, String> getStructureNames(long groupId, long globalGroupId, Locale locale) throws Exception {
		
		Map<Long, String> result = new HashMap<Long, String>();

		long clsNID = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class);
		List<DDMStructure> groupStrucutres = DDMStructureLocalServiceUtil.getStructures(groupId, clsNID);
		List<DDMStructure> globalStrucutres = DDMStructureLocalServiceUtil.getStructures(globalGroupId, clsNID);
		
		for (DDMStructure strc : groupStrucutres) {
			result.put(GetterUtil.getLong(strc.getStructureKey()), strc.getName(locale));
		}
		
		for (DDMStructure strc : globalStrucutres) {
			result.put(GetterUtil.getLong(strc.getStructureKey()), strc.getName(locale));
		}
		
		return result;
	}
	
	@SuppressWarnings(UNCHECKED)
	private static List<WebContentResult> queryAll(Disjunction disjunction, DynamicQuery trashDc,
			String sStr, int start, int end, Locale locale) throws Exception {
		
		List<WebContentResult> result = new ArrayList<WebContentResult>();
		
		try {
			
			List<JournalArticle> jaList = new ArrayList<JournalArticle>();
			
			DynamicQuery dynamicQuery = createDQ(disjunction, trashDc, sStr, start, end);
			jaList.addAll(JournalArticleLocalServiceUtil.dynamicQuery(dynamicQuery));
			
			for (JournalArticle ja : jaList) {
				result.add(createWCResult(ja, locale));
			}
			
			
		} catch (Exception e) { _log.error(ERROR_MSG, e); }
		
		return result;
		
	}
	
	private static int queryAllCount(Disjunction disjunction, DynamicQuery trashDc, String sStr) 
			throws Exception {
		
		int result = 0;
		
		try {
			
			DynamicQuery dynamicQuery = createDQ(disjunction, trashDc, sStr, -1, -1);
			result = result + GetterUtil.getInteger(JournalArticleLocalServiceUtil.
					dynamicQueryCount(dynamicQuery));
			
		} catch (Exception e) { _log.error(ERROR_MSG, e); }
		
		
		return result;
	}
	
	private static DynamicQuery createDQ(Disjunction disjunction, DynamicQuery trashDc, 
			String sStr, int start, int end) throws Exception {
		
		if (Validator.isNull(disjunction)) {
			disjunction = RestrictionsFactoryUtil.disjunction();
			disjunction.add(PropertyFactoryUtil.forName(FIELD_ID).eq(-1L));
		}
		
		DynamicQuery dynamicQuery= DynamicQueryFactoryUtil.forClass(JournalArticle.class);
		dynamicQuery.add(disjunction);
		dynamicQuery.setProjection(ProjectionFactoryUtil.property(FIELD_ID));
		
		boolean isNotCount = (start >= 0 && end > 0);
		
		DynamicQuery mainQuery= DynamicQueryFactoryUtil.forClass(JournalArticle.class);
		mainQuery.add(PropertyFactoryUtil.forName(FIELD_ID).in(dynamicQuery));
		if (Validator.isNotNull(trashDc)) {
			mainQuery.add(PropertyFactoryUtil.forName(FIELD_RPK).notIn(trashDc));
		}
		if (!Validator.isBlank(sStr)) {
			mainQuery.add(PropertyFactoryUtil.forName(FIELD_TITLE).like(
					StringPool.PERCENT + sStr + StringPool.PERCENT));
		}
		if (isNotCount) {
			mainQuery.addOrder(OrderFactoryUtil.desc(FIELD_MODIFIED_DATE));
			mainQuery.setLimit(start, end);
		}
		
		return mainQuery;
	}
	
	@SuppressWarnings(UNCHECKED)
	private static Disjunction createDisjuncion(long groupId, long grlobalId, long structure) throws Exception {
		
		DynamicQuery subQuery=DynamicQueryFactoryUtil.forClass(JournalArticle.class);
		Disjunction disjunctionGId = RestrictionsFactoryUtil.disjunction();
		subQuery.setProjection(
			ProjectionFactoryUtil.projectionList().add(
				ProjectionFactoryUtil.property(FIELD_RPK)
			).add(
				ProjectionFactoryUtil.max(FIELD_VERSION)
			).add(
				ProjectionFactoryUtil.groupProperty(FIELD_RPK)
			)
		);
		disjunctionGId.add(PropertyFactoryUtil.forName(FIELD_GROUP_ID).eq(groupId));
		disjunctionGId.add(PropertyFactoryUtil.forName(FIELD_GROUP_ID).eq(grlobalId));
		subQuery.add(disjunctionGId);
		if (structure >= 0) {
			String strId = (structure == 0) ? StringPool.BLANK : String.valueOf(structure);
			subQuery.add(PropertyFactoryUtil.forName(FIELD_STRUCTURE_ID).eq(strId));
		}
		
		List<Object[]> list= JournalArticleLocalServiceUtil.dynamicQuery(subQuery);
		
		if (list.isEmpty()) { return null; }
		
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();
		
		for (Object[] row : list) {
			Criterion c1 = PropertyFactoryUtil.forName(FIELD_RPK).eq(row[0]);
			Criterion c2 = PropertyFactoryUtil.forName(FIELD_VERSION).eq(row[1]);
			disjunction.add(RestrictionsFactoryUtil.and(c1, c2));
		}
		
		return disjunction;
	}

	private static DynamicQuery getTrashDc(long groupId, long globalGroupId) throws Exception {
		
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();
		
		List<TrashEntry> gEntries = TrashEntryLocalServiceUtil.getEntries(
				groupId, JournalArticle.class.getName());
		List<TrashEntry> globalEntries = TrashEntryLocalServiceUtil.getEntries(
				globalGroupId, JournalArticle.class.getName());
		
		if (gEntries.isEmpty() && globalEntries.isEmpty()) {
			return null;
		}
		
		for (TrashEntry tEntry : gEntries) {
			disjunction.add(RestrictionsFactoryUtil.eq(FIELD_RPK, tEntry.getClassPK()));
		}
		
		for (TrashEntry tEntry : globalEntries) {
			disjunction.add(RestrictionsFactoryUtil.eq(FIELD_RPK, tEntry.getClassPK()));
		}
		
		DynamicQuery result= DynamicQueryFactoryUtil.forClass(JournalArticle.class);
		result.setProjection(ProjectionFactoryUtil.property(FIELD_RPK));
		result.add(disjunction);
		
		return result;
		
	}
	
	private static List<WebContentResult> checkIsId(long groupId, long globalGroupId, 
			String searchStr, Locale locale) throws Exception {
		
		List<WebContentResult> result = new ArrayList<WebContentResult>();
		
		try {
			JournalArticle groupJA = JournalArticleLocalServiceUtil.getArticle(groupId, searchStr);
			result.add(createWCResult(groupJA, locale));
		} catch (Exception e) {}
		
		try {
			JournalArticle globalJA = JournalArticleLocalServiceUtil.getArticle(globalGroupId, searchStr);
			result.add(createWCResult(globalJA, locale));
		} catch (Exception e) {}
		
		return result;
	}
	
	private static WebContentResult createWCResult(JournalArticle ja, Locale locale) throws Exception {
		
		WebContentResult result = new WebContentResult(
			ja.getArticleId(), 
			ja.getTitle(locale),
			ja.getUrlTitle(),
			ja.getGroupId(), 
			GetterUtil.getLong(ja.getStructureId()), 
			null, 
			null, 
			ja.getUserName(), 
			SF.format(ja.getModifiedDate()));
	
		return result;
		
	}
	
	private static void translateFields(List<WebContentResult> list, Long globalGroupId, 
			Map<Long, String> structureNames, Locale locale) throws Exception {
		
		for (WebContentResult resultJa : list) {
			
			WebContentResult result = (WebContentResult) resultJa;
			
			result.setGroupName(translateGroup(result.getGroupId(), globalGroupId));
			result.setStrucutreName(translateStructureName(result.getStrucutreId(), structureNames, locale));
		}
		
	}
	
	private static String translateGroup(Long resultGrupId, Long globalGroupId) {
		if (Validator.equals(resultGrupId, globalGroupId)) {
			return GLOBAL;
		}
		else {
			return String.valueOf(resultGrupId);
		}
	}
	
	private static String translateStructureName(Long strucutreId, Map<Long, String> structureNames, Locale locale) {
		return GetterUtil.getString(structureNames.get(strucutreId), LanguageUtil.get(locale, BASIC_STRUCTURE));
	}
	
	private static final String UNCHECKED = "unchecked";
	private static final String GLOBAL = "GLOBAL";
	private static final String ERROR_MSG = "Error al filtrar contenidos web";
	private static final String BASIC_STRUCTURE = "wcs.no-strucutre-2";
	private static final String FIELD_MODIFIED_DATE = "modifiedDate";
	private static final String FIELD_RPK = "resourcePrimKey";
	private static final String FIELD_ID = "id";
	private static final String FIELD_VERSION = "version";
	private static final String FIELD_GROUP_ID = "groupId";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_STRUCTURE_ID = "structureId";
	private static final SimpleDateFormat SF = new SimpleDateFormat("hh:mm:ss dd/MM");

}
