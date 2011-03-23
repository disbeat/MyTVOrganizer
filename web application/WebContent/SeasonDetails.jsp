<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML+RDFa 1.0//EN" "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd">


<%@ page import="data.list.*" %>
<jsp:useBean id="seasonBean" scope="page" class="data.list.Season"></jsp:useBean>
<%
	String seasonID = request.getParameter("seasonID");
	String showID = request.getParameter("showID");
	if (seasonID == null)
	{
%>
		<jsp:forward page="showError.jsp"></jsp:forward>
<%
	}
	seasonBean.loadSeason(seasonID);
	
	if (showID != null)
	{
		Show showBean = new Show();
		showBean.loadShow(showID);
		session.setAttribute("showID", showID);
		session.setAttribute("showName", showBean.getName());
	}
		
	session.setAttribute("seasonID", seasonID);
	session.setAttribute("seasonNumber", seasonBean.getName());
	session.removeAttribute("episodeID");
	session.removeAttribute("episodeNumber");
	session.removeAttribute("actorID");
%>

<%@page import="java.net.URLEncoder"%>
<jsp:include page="header.jsp"></jsp:include>
	<center>
	<table border="0">
	<tr>
	<td>
	<div id="seasonHeader">
		<h1>Season: <%= seasonBean.getName() %></h1>
	</div>
	</td></tr>
	
	<tr><td>
	<div id="episodesList">
		<h2>List of Episodes</h2>
		<ul>
		<%
		for(Episode episode : seasonBean.getEpisodes())
		{	
		%>
		<span rel="mtvo:episode"><span typeof="mtvo:Episode">
			<li><a href="EpisodeDetails.jsp?episodeID=<%= URLEncoder.encode(episode.getEpisodeID(), "utf8") %>"><span property="mtvo:title"><%= episode.getTitle() %></span></a></li>
		</span></span>
		<%
		}
		%>
		</ul>
	</div>
	</td></tr>
	</table>
	</center>
<jsp:include page="footer.jsp"></jsp:include>