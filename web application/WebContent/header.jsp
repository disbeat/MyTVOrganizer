<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML+RDFa 1.0//EN" "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd">

<%@page import="java.net.URLEncoder"%>

<%@page import="ontology.PersistentOntology"%>

<html version="XHTML+RDFa 1.0" xmlns="http://www.w3.org/1999/xhtml"
		xmlns:foaf="http://xmlns.com/foaf/0.1/"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
		xmlns:mtvo="<%= PersistentOntology.NS %>"
	>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MyTVOrganizer</title>
</head>
<body>

<table border="0" align="center" width="100%">
<col width="20%">
<col width="80%" height="100%">
<tr>
	<td id="tdheader" colspan="2">
		<h1 align="center">My TV Organizer</h1>
	</td>
</tr>
<tr>
	<td id="tdleftmenu" valign="top" align="center">
		<ul>
			<li><a href="ShowsList.jsp">TV Shows</a></li>
			<li><a href="ActorsList.jsp">Actors</a></li>
		</ul>
		<br/>
		<h3>SEARCH</h3>
		<form action="SearchPage.jsp" method="get">
			<table border=0  align="center">
				<tr>
				<td><textarea cols="20" rows="3" name="query"></textarea></td>
				</tr>
				<tr><td align="center"><input type="submit" value="SEARCH" /></td></tr>
			</table>
		</form>
	</td>
	
	
	<td id="tdcontainer" bgcolor="lightgrey"> 
	<%
		String letter = (String)session.getAttribute("letter");
		if ( letter != null) {					
	%>
			<a href="ShowsList.jsp?letter=<%= letter %>">ShowsList: <%= letter %></a>
	<%
		}
		String showID = (String)session.getAttribute("showID");
		if ( showID != null) {
			String showName = (String)session.getAttribute("showName");		
	%>
			<a href="ShowDetails.jsp?showID=<%= URLEncoder.encode(showID, "utf8") %>">TVShow: <%= showName %></a>
	<%
		}
		String seasonID = (String)session.getAttribute("seasonID");
		if ( seasonID != null) {
			String seasonNumber = (String)session.getAttribute("seasonNumber");
	%>
		 | <a href="SeasonDetails.jsp?seasonID=<%= URLEncoder.encode(seasonID, "utf8") %>">Season <%= seasonNumber %></a>
	<%
		}
		String episodeID = (String)session.getAttribute("episodeID");
		if ( episodeID != null) {
			String episodeNumber = (String)session.getAttribute("episodeNumber");
	%>
		 | <a href="EpisodeDetails.jsp?showID=<%= URLEncoder.encode(episodeID, "utf8") %>">Episode <%= episodeNumber %></a>
	<%
		} 
	%>