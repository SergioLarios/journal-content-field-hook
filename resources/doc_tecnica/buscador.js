function openWindow(groupId, languageId, callBack) {

	var windowId = 'selectWebContentPopup';
	var baseUri = '/c/portal/custom-search-wc';
	var scUri = baseUri + '?groupId=' + groupId + '&locale=' + languageId;
	// Si se quiere una estructura por defecto
	// scUri = scUri + '&structure=' + strcKey
	// Y si ademas de una estructura por defecto no se quiere dejar seleccionar ninguna otra
	// scUri = scUri + '&hideStructure=true'
	var fnSelWC = Liferay.Util.getPortletNamespace('56') + 'selectWebContent';
	var fnCloseWindow = Liferay.Util.getPortletNamespace('56') + 'closeWCWindow';

	Liferay.Util.openWindow({
		id: windowId,
		title: Liferay.Language.get('wcs.page-title'),
		uri: scUri
	});

	Liferay.provide(window, fnCloseWindow,
		function () {
			window.Liferay.Util.Window.getById(windowId).destroy();
		},
		['liferay-util-window']
	);

	Liferay.provide(window, fnSelWC, callBack);

}

openWindow(
	themeDisplay.getScopeGroupId(), 
	themeDisplay.getLanguageId(), 
	function (groupId, contentId, title, urlTitle) {
		console.log(groupId, contentId, title, urlTitle);
	}
);