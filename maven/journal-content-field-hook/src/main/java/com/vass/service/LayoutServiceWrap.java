package com.vass.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.LayoutLocalService;
import com.liferay.portal.service.LayoutLocalServiceWrapper;
import com.vass.lar.WcLarUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;


public class LayoutServiceWrap extends LayoutLocalServiceWrapper {

	private static Log _log = LogFactoryUtil.getLog(LayoutServiceWrap.class);
	
	@Override
	public void importLayouts(long userId, long groupId, boolean privateLayout,
			Map<String, String[]> parameterMap, File file)
			throws PortalException, SystemException {
		super.importLayouts(userId, groupId, privateLayout, parameterMap, file);
		
		long tStart = (new Date()).getTime();
		
		_log.info(START_MSG);
		
		int contents = WcLarUtil.processLar(file, groupId);
		
		long secs = ((new Date()).getTime() - tStart) / 1000;
		
		_log.info(String.format(END_MSG, contents, secs));
		
	}
	
	@Override
	public void importLayouts(long userId, long groupId, boolean privateLayout,
			Map<String, String[]> parameterMap, InputStream is)
			throws PortalException, SystemException {
		
		File file = createTemp(TEMP_PREFFIX + groupId);
		
		if (Validator.isNotNull(file)) {
			
			file = createFile(file, is);
			
			try {
				is = new FileInputStream(file);
				
				super.importLayouts(userId, groupId, privateLayout, parameterMap, is);
				
				is = new FileInputStream(file);
				
				long tStart = (new Date()).getTime();
				
				_log.info(START_MSG);
				
				int contents = WcLarUtil.processLar(is, groupId);
				
				long secs = ((new Date()).getTime() - tStart) / 1000;
				
				_log.info(String.format(END_MSG, contents, secs));
				
				file.delete();
				
			} catch (FileNotFoundException e) {
				_log.error(e);
			}
			
		}
		else {
			_log.error(ERROR_MSG_TEMP);
			super.importLayouts(userId, groupId, privateLayout, parameterMap, is);
		}
		
	}
	
	public LayoutServiceWrap(LayoutLocalService layoutLocalService) {
		super(layoutLocalService);
	}
	
	private static File createTemp(String suffix) {
		try {
			return File.createTempFile(suffix, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static File createFile(File file, InputStream is) {
		
		try {

			OutputStream salida = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				salida.write(buf, 0, len);
			}
			salida.close();
			is.close();
			
			return file;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private static final String START_MSG = "* Iniciando * el proceso de revisión de los campos -Web Content- "
			+ "en la importación";
	private static final String END_MSG = "* Finalizado * el proceso de revisión de los campos -Web Content-"
			+ " en la importación de %d contenidos en un total del %d segundos";
	private static final String ERROR_MSG_TEMP = 
			"ERROR: No se pudo crear un archivo temporal para la revisión del campo - Web Content -";
	private static final String TEMP_PREFFIX = "import_";
	
}
