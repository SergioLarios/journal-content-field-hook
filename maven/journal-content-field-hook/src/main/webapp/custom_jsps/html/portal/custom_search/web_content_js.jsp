<script type="text/javascript" src="/html/js/aui/aui/aui.js"></script>
<script type="text/javascript">

YUI().use('node', function (Y) { Y.on("domready", function(){

	var fnSelWC = '_56_selectWebContent';
	var fnCloseWindow = '_56_closeWCWindow';
	var fomSel = 'form[name="selectWebContent"]';
	var deltaSel = 'div.delta-wrapper select.delta-pages';
	var deltaInpSel = 'input[name="delta"]';
	var pageInpSel = 'input[name="page"]';
	var searchInpSel = 'input[name="searchStr"]';
	var pagesSel = 'ul.lfr-pagination-buttons';
	var btnSel = 'button.selector-button';
	var structureSel = 'select[name="structure"]';
	var form = Y.one(fomSel);
	
	var resetForm = function(ev) {
		form.one(pageInpSel).set('value', 1);
		form.one(searchInpSel).set('value', '');
		form.submit();
	}
	
	// Delta change

	form.one(deltaSel).on('change', resetForm);
	
	// Strucutre change

	form.one(structureSel).on('change', resetForm);
	
	// Page change

	form.all(pagesSel).each(function (node) {
		node.on('click', function(ev) {
			ev.preventDefault();
			var page = ev.target.getData('page');
			if (page > 0) {
				form.one(pageInpSel).set('value', page);
				form.submit();
			}
		});
	});
	
	// Choose
	
	form.all(btnSel).each(function (node) {
		node.on('click', function(ev) {
			ev.preventDefault();
			
			var gId = ev.target.getData('group-id');
			var cId = ev.target.getData('content-id');
			var title = ev.target.getData('title');
			var urlTitle = ev.target.getData('url-title');
			
			if (window.parent) {
				window.parent[fnSelWC](gId, cId, title, urlTitle);
				window.parent[fnCloseWindow]();
			}
			
		});
	});
	
})});

</script>