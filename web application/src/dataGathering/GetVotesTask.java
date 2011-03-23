package dataGathering;

import java.io.IOException;

import data.list.Show;

public class GetVotesTask implements Runnable
{
	final private static String VOTES_NUMBER_PATTERN = "<span class=\"num_votes\">(.*?)</span>";	
	
	private TVDotComCrawler crawler;
	private Show show;
	
	public GetVotesTask(TVDotComCrawler crawler, Show show)
	{
		this.crawler = crawler;
		this.show = show;
	}
	
	@Override
	public void run()
	{
		int votes = getNumberOfVotes();
		show.setTvDotComVotes(votes);
		System.out.println("Thread #" + Thread.currentThread().getId() + ": Votes = " + votes + " for show " + show.getName());
		
		// get cast url
		// for all pages
			// scrap actor url
				// scrap actor info
	
	}
	
	private int getNumberOfVotes()
	{
		try
		{
			String page = crawler.getPage(show.getShowLink());
			String temp = crawler.getMatch(VOTES_NUMBER_PATTERN, page, 1, 1);
			if(temp != null)
			{
				return crawler.parseNumberOfVotes(temp);
			}
			
		} catch (IOException e)
		{
			System.out.println("Error on show link: '" + show.getShowLink() + "'");
		}
		
		return 0;
	}
}
