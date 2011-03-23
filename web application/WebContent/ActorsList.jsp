<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="data.list.*" %>
<%@page import="java.net.URLEncoder"%>

<jsp:useBean id="actorLister" scope="page" class="data.list.ActorLister"></jsp:useBean>
<%
	String letter = request.getParameter("letter");
	
	if (letter == null)
		letter = "A";

	session.setAttribute("actorLetter", letter);
	session.removeAttribute("letter");
	session.removeAttribute("showID");
	session.removeAttribute("showName");
	session.removeAttribute("seasonID");
	session.removeAttribute("seasonNumber");
	session.removeAttribute("episodeID");
	session.removeAttribute("episodeNumber");
	session.removeAttribute("actorID");	
	
	actorLister.loadActors(letter);
%>

<jsp:include page="header.jsp"></jsp:include>
<center>
<h2>Actors</h2>
</center>
<br />
<br />
<%
	for (char l = 'A'; l <= 'Z'; l++)
	{		
%>		
		<a href="ActorsList.jsp?letter=<%=  l %>"><%= l %></a>
		
<%
	}
%>
<table align="center" width="100%" border="1">
	<tr>
		<th>Show Name</th>
	</tr>
<%
	for (int s = 0; s < actorLister.size(); s++)
	{		
		Actor actor = actorLister.getActor(s);
		String link = URLEncoder.encode(actor.getUri(), "utf8");
%>
	
	<tr>	
		<td><a href="ActorDetails.jsp?actorID=<%= link %>"><%= actor.getName() %></a></td>
	</tr>
	
		
<%
	}
%>
	
</table>



<jsp:include page="footer.jsp"></jsp:include>