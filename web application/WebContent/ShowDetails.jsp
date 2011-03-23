<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>



<%@ page import="data.list.*, ontology.*, java.util.*" %>
<%
	Show showBean = new Show();

	String showID = request.getParameter("showID");
	if (showID == null)
	{
%> 
		
<%@page import="com.hp.hpl.jena.query.ResultSet" %>
<%@page import="com.hp.hpl.jena.query.QuerySolution" %><jsp:forward page="showError.jsp"></jsp:forward>
<%
	}
	showBean.loadShow(showID);
	
	session.setAttribute("showID", showID);
	session.setAttribute("showName", showBean.getName());
	session.removeAttribute("seasonID");
	session.removeAttribute("seasonNumber");
	session.removeAttribute("episodeID");
	session.removeAttribute("episodeNumber");
	session.removeAttribute("actorID");

%>

<%@page import="java.net.URLEncoder"%>
<jsp:include page="header.jsp"></jsp:include>
<div typeof="mtvo:TVShow">
	<table border="0" align="center">
	<tr>
	<td>
	<div id="showHeader">
		<h1>TV Show: <span property="mtvo:name"><%= showBean.getName() %></span></h1>
	</div>
	</td></tr>
	
	<tr><td>
	<div id="showDetails">
		<table id="detailsTable" border="0">
			<tr> <td>Image:</td>	    <td><span property="mtvo:picture"><img src="<%=showBean.getImage()%>" alt="Image of the show" /></span></td> </tr>
			<tr> <td>Air Day:</td>      <td><span property="mtvo:airDay"><%= showBean.getAirDay() %></span></td> </tr>
			<tr> <td>Air Time:</td>      <td><span property="mtvo:airTime"><%= showBean.getAirTime() %></span></td> </tr>			
			<tr> <td>Started:</td>		<td><span property="mtvo:premiered"><%= showBean.getStarted() %></span></td> </tr>
			<tr> <td>Classification:</td>		<td><span property="mtvo:classification"><%= showBean.getClassification() %></span></td> </tr>
			<tr> <td>Status:</td>		<td><span property="mtvo:status"><%= showBean.getStatus() %></span></td> </tr>
			<tr> <td>Genre:</td>		<td><span property="mtvo:genre"><%
			List<String> genres = showBean.getGenres();
			for (String genre: genres)
				out.print(genre+"<br />");
			%></span></td> </tr>
			
						
		</table>
	</div>
	</td></tr>
	<tr><td>
	<div id="seasonsList">
		<h2 align="center">List of Seasons</h2>		
		<%for( Season season : showBean.getSeasons() ) {	
		
		%>
			<span rel="mtvo:season">
			<span typeof="mtvo:Season">
			<h3 align="center"><a href="SeasonDetails.jsp?seasonID=<%= URLEncoder.encode(season.getSeasonID(), "utf8") %>">Season <span property="mtvo:number"><%= season.getName() %></span></a></h3> 			
			<ul>			
			<%
			for(Episode episode : season.getEpisodes())
			{	
			%> 
				
				<li><span rel="mtvo:episode"><span typeof="mtvo:Episode"><a href="EpisodeDetails.jsp?episodeID=<%= URLEncoder.encode(episode.getEpisodeID(), "utf8") %>&seasonID=<%= URLEncoder.encode(season.getSeasonID(), "utf8") %>&seasonNumber=<%= season.getName() %>"><span property="mtvo:title"><%= episode.getTitle() %></span></a></span></span></li>
				
			<%
			}
			%>
			</ul>
			</span>
			</span>
<%
		}
		%>		
	</div>
	</td></tr>
	 
	</table>
	<br/><br/>
	<h2 align="center">Actors of the Show</h2><br />
	<table align="center" border="1">
	<tr>
		<th>Actor</th>
		<th>Role Played</th>
	</tr>
	<%
		Database d = new Database();
	
		
		List<String[]> actors = d.getActorsOfShow(showID);
		String [] actorsNames = new String[actors.size()];
		int index = 0;
		for(String[] actor : actors)
		{
			actorsNames[index++] = actor[2].trim();
	%>
	<tr>
	<td>
		<a href="ActorDetails.jsp?actorID=<%= URLEncoder.encode(actor[0], "utf8") %>"><%= actor[2] %> </a>
	</td>
	<td>
		<%= actor[1] %>
	</td>
	</tr>
	<%  } %>
	</ul>		
	</table>	
		
	<h3 align="center">Recomended shows:</h3>
	<div align="center">
	<ul>
	<%
		List<String> genders = showBean.getGenres();
		ArrayList<String> uris = new ArrayList<String>();
		if (genders.size() > 0 ) {
			int counter = 0;
			String genderWord = "";
			for (String w : genders)
				genderWord += " "+w;
			ResultSet rs = d.getRecomendedShows(genderWord, actorsNames);
			while(rs.hasNext())
			{		
				QuerySolution qs = rs.next();
				
				String showRel = qs.get("showID").toString();
				String showNameRel =  qs.get("showName").toString();
				if ( ! showRel.equalsIgnoreCase(showID) && !Database.hasWord(uris, showRel))
				{
					counter++;
					uris.add(showRel);
				%>
					<li><a href="ShowDetails.jsp?showID=<%=URLEncoder.encode(showRel, "utf8")%>"><%= showNameRel %></a></li>
				<%
				}
			}
				
			if (counter == 0)
			{
				rs = d.searchGenre(genderWord);
				
				while(rs.hasNext())
				{		
					QuerySolution qs = rs.next();
					
					String showRel = qs.get("showID").toString();
					String showNameRel =  qs.get("showName").toString();
					if (! showRel.equalsIgnoreCase(showID) && !Database.hasWord(uris, showRel))
					{
						counter++;				
						uris.add(showRel);
					%>
						<li><a href="ShowDetails.jsp?showID=<%=URLEncoder.encode(showRel, "utf8")%>"><%= showNameRel %></a></li>
					<%
					}
				}
			}
		}
			
			
	%>
	</ul>
	</div>
</div>
<jsp:include page="footer.jsp"></jsp:include>