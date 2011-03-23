<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML+RDFa 1.0//EN" "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd">

<%@ page import="data.list.Show" %>

<%@page import="java.net.URLEncoder"%><jsp:useBean id="showLister" scope="page" class="data.list.ShowLister"></jsp:useBean>

<%
	String letter = request.getParameter("letter");
	
	if (letter == null)
		letter = "A";

	session.setAttribute("letter", letter);
	session.removeAttribute("showID");
	session.removeAttribute("showName");
	session.removeAttribute("seasonID");
	session.removeAttribute("seasonNumber");
	session.removeAttribute("episodeID");
	session.removeAttribute("episodeNumber");
	session.removeAttribute("actorID");	
	
	showLister.loadShows(letter);
%>

<jsp:include page="header.jsp"></jsp:include>
<center>
<h2>TV Shows</h2>
</center>
<br />
<br />
<%
	for (char l = 'A'; l <= 'Z'; l++)
	{		
%>		
		<a href="ShowsList.jsp?letter=<%=  l %>"><%= l %></a>
		
<%
	}
%>

<table align="center" width="100%" border="1">
	<tr>
		<th>Show Name</th>
	</tr>
<%
	for (int s = 0; s < showLister.size(); s++)
	{		
		Show show = showLister.getShow(s);
		String link = URLEncoder.encode(show.getUri(), "utf8");
%>
	
	<tr>	
		<td><a href="ShowDetails.jsp?showID=<%= link %>"><%= show.getName() %></a></td>
	</tr>
	
		
<%
	}
%>
	
</table>

<jsp:include page="footer.jsp"></jsp:include>