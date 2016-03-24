<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="tagdisplay"%>
<html>
	<head>
		
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/js/index.js"></script>
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/js/chartsUtils.js"></script>

		<title>${view.name}</title>
		<meta http-equiv="Content-Type"
			content="text/html; charset=ISO-8859-1">
		<title><fmt:message key="menu.title" /></title>
		<script type="text/javascript">
		function start(id){
				document.forms['frm1'].elements['dId'].value=id;
				document.forms['frm1'].action="./startDaemon.html";
				document.forms['frm1'].submit();
		}
		function stop(id){
				document.forms['frm1'].elements['dId'].value=id;
				document.forms['frm1'].action="./stopDaemon.html";
				document.forms['frm1'].submit();
		}
		</script>
	</head>
	<form action="" id="frm1" method="post">
	<input name="dId" id="dId" type="hidden">
	</form>
	
	<body onload="document.body.style.cursor='default'";>
		
		<tagdisplay:table sort="list" name="${list}"
			requestURI="./daemonList.html" id="document" export="false"
			pagesize="10" cellpadding="5" excludedParams="">

			<tagdisplay:column media='html'>
				<html:image src="${pageContext.request.contextPath}/img/ViewDocInfo16.gif" alt="Start"
					onclick="javascript:start('${document.name}');" /> 
					<html:image src="${pageContext.request.contextPath}/img/ViewDocInfo16.gif" alt="Stop"
					onclick="javascript:stop('${document.name}');" />
			</tagdisplay:column>

			<tagdisplay:column title="Name" sortable="true"
				property="name" />
			<tagdisplay:column title="Time" sortable="true"
				property="timeEnlapsed" />
			<tagdisplay:column title="Class" sortable="true"
				property="classToLaunch" />



		</tagdisplay:table>
		<br>



		<script type="text/javascript">
		document.body.style.cursor='default';
		</script>
	</body>

</html>
