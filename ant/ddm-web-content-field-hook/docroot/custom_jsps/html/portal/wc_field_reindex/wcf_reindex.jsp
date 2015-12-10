<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html> 

<html class="aui"> 
	<head>
		<title>${messages.pageTitle}</title>
		<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
		<%@ include file="/html/portal/wc_field_reindex/wcf_reindex_style.jsp" %>
	</head>
	<body>
	
		<div class="container">
		
			<%-- ******** Header ******** --%>
		
			<h1>${messages.header}</h1>
			
			<p>${messages.explanation}</p>
			
			<form action="/c/portal/reindex-wc-field" method="post" name="selectWebContent" class="form">
				
				<c:if test="${form.executed}">
				
					<c:if test="${form.success}">
						<div class="alert alert-success">${messages.formSuccess} ${form.executedGroup}</div>
					</c:if>
					<c:if test="${!form.success}">
						<div class="alert alert-error">
							<p>${messages.formError} ${form.executedGroup}</p>
							<p>${form.errMsg}</p>
						</div>
					</c:if>
				
				</c:if>
				
				<input type="hidden" name="cmd" value="form_executed">
				
				<%-- ******** Normal ******** --%>
				
				<fieldset class="fieldset">
					
					<div class="taglib-header ">
						<h3 class="header-title"> <span> ${messages.hNormal} </span> </h3>
					</div>
					
					<p>${messages.pNormal}</p>
					
					<div class="control-group form-inline">
						<label for="normal_group" class="control-label"> ${messages.labelNormal} </label>
						<select id="normal_group" class="aui-field-select" name="normal_group">
							<option value="-1">${messages.selecSite}</option>
							<c:forEach var="group" items="${normalGroups}">
								<option value="${group.groupId}">${group.name} [${group.friendlyURL}]</option>
							</c:forEach>
						</select>
					</div>
				
				</fieldset>
				
				<%-- ******** Staging ******** --%>
				
				<fieldset class="fieldset">
				
					<div class="taglib-header ">
						<h3 class="header-title"> <span> ${messages.hStaging} </span> </h3>
					</div>
					
					<p>${messages.pStaging}</p>
					
					<div class="control-group form-inline">
						<label for="live_group" class="control-label"> ${messages.labelStaging} </label>
						<select id="live_group" class="aui-field-select" name="live_group">
							<option value="-1">${messages.selecSite}</option>
							<c:forEach var="group" items="${liveGroups}">
								<option value="${group.groupId}">${group.name} [${group.friendlyURL}]</option>
							</c:forEach>
						</select>
					</div>
				
				</fieldset>
				
				<%-- ******** Submit ******** --%>
				
				<div class="button-holder ">
					<button type="submit" class="btn btn-primary"> ${messages.save} </button>
				</div>
				
			</form>
		
		</div>
		
	</body>

</html>
