<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html> 

<html class="aui"> 
	<head>
		<title>${messages.pageTitle}</title>
		<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
		<%@ include file="/html/portal/custom_search/web_content_style.jsp" %>
	</head>
	<body>
		
		<form action="/c/portal/custom-search-wc" method="post" name="selectWebContent">
			
			<%-- ********** Hidden inputs ********** --%>
			
			<input type="hidden" name="groupId" value="${groupId}"/>
			<input type="hidden" name="locale" value="${locale}"/>
			<input type="hidden" name="page" value="${pagination.page}"/>
			
			<%-- ********** Navbar ********** --%>
			
			<div class="navbar">
				<div class="navbar-inner">
					<div class="container">
					
						<%-- Keywords --%>
					
						<div class="navbar-search pull-right form-search">
							<div class="input-append">
								<div class="advanced-search">
									<input type="text" placeholder="${messages.placeHolder}" name="searchStr" 
										class="search-query span9" value="${searchStr}" />
									<button type="submit" class="btn">${messages.search}</button>
									<span class="toggle-advanced">
										<i class="icon-question-sign"></i>
										<span class="tooltip">${messages.tooltip}</span>
									</span> 
								</div>
							</div>
						</div>
						
						<%-- Structures --%>
						
						<c:if test="${hideStructure}">
						
							<input type="hidden" name="structure" value="${structure}" />
						
						</c:if>
						
						<c:if test="${!hideStructure}">
						
							<div class="navbar-search pull-right strc-form">
								<label for="structure">${messages.strct}</label>
								<select name="structure" id="strucutre">
									<option value="-1">-</option>
									<c:if test="${0 != structure}">
										<option value="0">${messages.noStrucutre}</option>
									</c:if>
									<c:if test="${0 == structure}">
										<option value="0" selected="selected">${messages.noStrucutre}</option>
									</c:if>
									<c:forEach var="entry" items="${structureNames}">
										<c:if test="${entry.key != structure}">
											<option value="${entry.key}">${entry.value}</option>
										</c:if>
										<c:if test="${entry.key == structure}">
											<option value="${entry.key}" selected="selected">${entry.value}</option>
										</c:if>
									</c:forEach>
								</select>
							</div>
						
						</c:if>
						
					</div>
				</div>
			</div>
			
			<div class="separator"><!-- --></div>
			
			<%-- ********** Table ********** --%>
			
			<div>
			
				<table class="table table-bordered table-hover table-striped">
				
					<%-- Header --%>
				
					<thead class="table-columns">
						<tr>
							<th class="table-first-header">${messages.id}</th>
							<th>${messages.title}</th>
							<th>${messages.group}</th>
							<th>${messages.strct}</th>
							<th>${messages.user}</th>
							<th>${messages.lastMod}</th>
							<th class="table-last-header"></th>
						</tr>
					</thead>
					
					<%-- Body --%>
					
					<tbody class="table-data">
						<c:forEach var="content" items="${resulsts.results}">
							<tr>
								<td class="table-cell first">${content.contentId}</td>
								<td class="table-cell">${content.title}</td>
								<td class="table-cell">${content.groupName}</td>
								<td class="table-cell">${content.strucutreName}</td>
								<td class="table-cell">${content.user}</td>
								<td class="table-cell">${content.modifiedDateStr}</td>
								<td class="table-cell last">
									<button type="button" class="btn selector-button" 
											data-group-id="${content.groupId}"
											data-content-id="${content.contentId}"
											data-title="${content.title}"
											data-url-title="${content.urlTitle}">
										${messages.choose}
									</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				
				</table>
				
				
				<div>
					
					<%-- Delta --%>
					
					<div class="delta-wrapper">
						<label for="deltaPages" class="delta-label">${messages.resPage}</label>
						<select name="delta" id="deltaPages" class="delta-pages">
							
							<c:forEach var="i" items="${pagination.deltas}">
								<c:if test="${i != pagination.delta}">
									<option value="${i}">${i}</option>
								</c:if>
								<c:if test="${i == pagination.delta}">
									<option value="${i}" selected="selected">${i}</option>
								</c:if>
							</c:forEach>
							
						</select>
						<div class="curr-pos">
							${pagination.currPos}
						</div>
						<div class="curr-pos">
							${pagination.totalPages}
						</div>
					</div>
					
					<%-- Paginator --%>
					
					<div class="pager-wrapper">
						<ul class="pager lfr-pagination-buttons">
							<c:forEach var="iPag" items="${pagination.pageList}">
								<li ${iPag.disabled ? 'class=\"disabled\"' : ''}>
									<a href="#" data-page="${iPag.page}">${iPag.name}</a>
								</li>
							</c:forEach>
						</ul>
					</div>
					
				</div>
			
			</div>
			
		</form>
		
		<%@ include file="/html/portal/custom_search/web_content_js.jsp" %>
	
	</body>

</html>
