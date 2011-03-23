<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="java.util.List" %>
<jsp:useBean id="searchManager" scope="page" class="data.list.SearchManager"></jsp:useBean>

<%
	session.removeAttribute("actorLetter");
	session.removeAttribute("letter");
	session.removeAttribute("showID");
	session.removeAttribute("showName");
	session.removeAttribute("seasonID");
	session.removeAttribute("seasonNumber");
	session.removeAttribute("episodeID");
	session.removeAttribute("episodeNumber");
	session.removeAttribute("actorID");

	String query = request.getParameter("query");

	if (query == null)
		query = "";
	
	searchManager.seach(query);
	
	List<String> links = searchManager.getLinks();
	List<String> names = searchManager.getNames();	
%>
<jsp:include page="header.jsp"></jsp:include>
<table border="0" width="100%">
<% 
	for (int i = 0; i < links.size(); i++)
	{
%>
<tr><td><a href="<%=links.get(i) %>"><%=names.get(i) %></a>
</td>
</tr>
<% 
	}
%>
</table>
	





<jsp:include page="footer.jsp"></jsp:include>
