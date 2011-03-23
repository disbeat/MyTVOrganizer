package dataGathering;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.list.Actor;
import data.list.Cast;
import data.list.Role;
import data.list.Show;

public class GetActorsTask implements Runnable
{
	private static final String PERSON_PATTERN = "<li class=\"person.*?\">.*?<h4 class=\"full_name\">.*?<a href=\"(.*?)\">(.*?)</a>.*?<div class=\"role\">(.*?)</div>";
	private static final String GENDER_PATTERN = "<dt>Gender:</dt>.*?<dd>(.*?)</dd>";
	private static final String BIRTHDAY_PATTERN = "<dt>Birthday:</dt>.*?<dd>(.*?)</dd>";
	private static final String BIRTHPLACE_PATTERN = "<dt>Birthplace:</dt>.*?<dd>(.*?)</dd>";
	private static final String BIO_PATTERN = "<div class=\"summary\">.*?<span class=\"long\">(.*?)<br";
	
	private TVDotComCrawler crawler;
	private Show show;
	
	public GetActorsTask(TVDotComCrawler crawler, Show show)
	{
		this.crawler = crawler;
		this.show = show;
	}
	
	@Override
	public void run()
	{
//		System.out.println("Thread #" + Thread.currentThread().getId() + ": Starting work for show '" + show.getName() + "'");
		String showURL = show.getShowLink();
		String castURL = showURL.replace("summary.html", "cast.html");
		String castPage;
		try
		{
			castPage = crawler.getPage(castURL);
		} catch (IOException e)
		{
			System.out.println("Error on getting cast page for show: '" + show.getName() + "'");
			return;
		}
		
		Cast cast = new Cast();
		Role role;
		Actor actor;
		int id;
		String actorUrl, actorName, roleName;
		Pattern pattern = Pattern.compile(PERSON_PATTERN);
		Matcher matcher = pattern.matcher(castPage);
		while(matcher.find())
		{
			actorUrl = matcher.group(1).trim();
			actorName = matcher.group(2).trim();
			roleName = matcher.group(3).trim();
			//System.out.println("Link to person: '" + actorUrl + "'");
			//System.out.println("Full name: '" + actorName + "'");
			System.out.println("Thread #" + Thread.currentThread().getId() + " Role Name: '" + roleName + "'");
			
			role = new Role();
			role.setRoleName(roleName);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " BEFORE ID");
			id = parseActorId(actorUrl);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " AFTER ID"); 
			
			actor = new Actor();
//			System.out.println("Thread #" + Thread.currentThread().getId() + " BEFORE COMPLETE");
			completeActorInfo(actor, actorUrl);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " AFTER COMPLETE");
			actor.setName(actorName);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " 1");
			actor.setId(id);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " 2");
			role.setPlayedBy(actor);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " 3");
			actor.addRole(role);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " BEFORE ADD ACTOR");
			crawler.addActor(actor, role);
//			System.out.println("Thread #" + Thread.currentThread().getId() + " AFTER ADD ACTOR");

//			System.out.println("Thread #" + Thread.currentThread().getId() + " Adding role '" + roleName + "'");
			cast.addRole(role);
		}
		
		show.setCast(cast);
		System.out.println("Thread #" + Thread.currentThread().getId() + ": Done for show '" + show.getName() + "'");
	}
	
	private void completeActorInfo(Actor actor, String url)
	{
		String actorPage = null;
		try
		{
			actorPage = crawler.getPage(url);
		} catch (IOException e)
		{
			System.out.println("Error on actor: " + actor.getName());
			//return;
		}
		
		actor.setGender(crawler.getMatch(GENDER_PATTERN, actorPage, 1, 1));
		actor.setBirthDay(crawler.getMatch(BIRTHDAY_PATTERN, actorPage, 1, 1));
		actor.setBirthPlace(crawler.getMatch(BIRTHPLACE_PATTERN, actorPage, 1, 1));
		actor.setBiography(crawler.getMatch(BIO_PATTERN, actorPage, 1, 1));
		
//		System.out.println("Gender: " + actor.getGender());
//		System.out.println("Birthday: " + actor.getBirthDay());
//		System.out.println("Birthplace: " + actor.getBirthPlace());
//		System.out.println("Bio: " + actor.getBiography());
	}
	
	private int parseActorId(String actorURL)
	{
		Pattern pattern = Pattern.compile("person/(.*?)/summary.html");
		Matcher matcher = pattern.matcher(actorURL);
		matcher.find();
		return Integer.parseInt(matcher.group(1));
	}
}
