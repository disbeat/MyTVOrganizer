<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="data.list.*" %>
<jsp:useBean id="episodeBean" scope="page" class="data.list.Episode"></jsp:useBean>
<%
	String episodeID = request.getParameter("episodeID");
	if (episodeID == null)
	{
%>
		<jsp:forward page="showError.jsp"></jsp:forward>
<%
	}
	episodeBean.loadEpisode(episodeID);

	String seasonID = request.getParameter("seasonID");
	String seasonNumber = request.getParameter("seasonNumber");
	
	if (seasonID != null)
	{
		session.setAttribute("seasonID", seasonID);
		Season season = new Season();
		season.loadSeason(seasonID);
		session.setAttribute("seasonNumber", season.getName());
	}
	session.setAttribute("episodeID", episodeID);
	session.setAttribute("episodeNumber", episodeBean.getNumber());
	session.removeAttribute("actorID");
%>
<jsp:include page="header.jsp"></jsp:include>
	<center>
	<table border="0">
	<tr>
	<td>
	<div id="episodeHeader">
		<h1><%= episodeBean.getTitle() %></h1>
	</div>
	</td></tr>
	
	<tr><td>
	<div id="episodeDetails" typeof="mtvo:episode">
		<table id="detailsTable" border=0>
			<tr> <td>Number:</td>     	<td><span property="mtvo:number"><%= episodeBean.getNumber() %></span></td> </tr>
			<tr> <td>Air Date:</td>		<td><span property="mtvo:airDate"><%= episodeBean.getAirDate() %></span></td> </tr>
			<tr> <td>Video:</td>		<td><span property="mtvo:video"><a href="<%= episodeBean.getVideoLink() %>">Download video</a></span></td> </tr>
		</table>
	</div>
	</td></tr>
	</table>
	</center>
<jsp:include page="footer.jsp"></jsp:include>