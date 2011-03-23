<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>


<%@ page import="data.list.*, ontology.*, java.util.*" %>

<%
	Actor actorBean = new Actor();

	String actorID = request.getParameter("actorID");
	if (actorID == null)
	{
%> 
		
<%@page import="com.hp.hpl.jena.query.ResultSet" %>
<%@page import="com.hp.hpl.jena.query.QuerySolution" %><jsp:forward page="showError.jsp"></jsp:forward>
<%
	}
	actorBean.loadActor(actorID);
	
	session.setAttribute("actorID", actorID);
	session.setAttribute("actorName", actorBean.getName());	
	session.removeAttribute("seasonID");
	session.removeAttribute("seasonNumber");
	session.removeAttribute("episodeID");
	session.removeAttribute("episodeNumber");
	session.removeAttribute("actorID");

%>

<%@page import="java.net.URLEncoder"%>
<jsp:include page="header.jsp"></jsp:include>
<div typeof="mtvo:Actor">
	<table border="0" align="center">
	<tr>
	<td>
	<div id="showHeader">
		<h1 align="center"><span property="mtvo:name">Actor: <%= actorBean.getName() %></span></h1>
	</div>
	</td></tr>
	
	<tr><td>
	<div id="showDetails">
		<table id="detailsTable" border="0" align="center">
			<tr> <td>Birthday:</td>	    <td><span property="mtvo:birthday"><%=actorBean.getBirthDay() %></span></td> </tr>
			<tr> <td>Birth Place:</td>      <td><span property="mtvo:birthPlace"><%= actorBean.getBirthPlace() %></span></td> </tr>
			<tr> <td>Gender:</td>		<td><span property="mtvo:gender"><%= actorBean.getGender() %></span></td> </tr>
			<tr> <td>Biography:</td>		<td><span property="mtvo:biography"><%String bio = actorBean.getBiography(); if (bio != null && bio.indexOf("<a") != -1) bio = bio.substring(0, bio.indexOf("<a")); out.print(bio); %></span></td> </tr>
		</table>
	</div>
	</td></tr>
	<tr><td>
	<div rel="mtvo:plays">
	<span typeof="mtvo:Role">
		<h2>List of Roles</h2>
		<ul>		
		<%
			Database d = new Database();
			List<String[]> shows = d.getShowsOfActor(actorID);
		for( String[] show : shows ) {	
		
		%>
					
			<li><a href="ShowDetails.jsp?showID=<%= URLEncoder.encode(show[0], "utf8") %>"><span property="mtvo:roleName"><%= show[2] %></span>  in  show: <%= show[1] %></a></li>
<%	
		}
		%>		
		</ul>
		</span>
	</div>
	</td></tr>
	 
	</table>
</div>
<jsp:include page="footer.jsp"></jsp:include>