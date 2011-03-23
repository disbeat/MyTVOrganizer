package dataGathering;

import java.io.IOException;
import java.util.List;

import data.list.Episode;
import data.list.Season;
import data.list.Show;

public class GetVideoLinkTask implements Runnable
{
	SideReelCrawler crawler;
	private Show show;
	
	GetVideoLinkTask(Show show, SideReelCrawler crawler) { this.crawler = crawler; this.show = show; };
	
	@Override
	public void run()
	{
		String page, url, fullUrl = null;
		System.out.println(Thread.currentThread().getId() + " Show: '" + show.getName() + "'");
		try
		{
			url = crawler.PAGE_SITE + show.getName().replace(' ', '_');
			page = crawler.getPage(url);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("No page for show: '" + show.getName() + "'");
			return;
		}
		
		int offSet = 0;
		for(Season season : show.getSeasons())
		{
			List<Episode> episodes = season.getEpisodes();
			System.out.println("\tSeason: '" + season.getName() + "'");
			int seasonNumber;
			try {
				seasonNumber = Integer.parseInt(season.getName());
			} catch(java.lang.NumberFormatException e) {
				System.out.println("Parse error for '" + season.getName() + "'");
				offSet += episodes.size();
				continue;
			}
			
			for(int i=0; i < episodes.size(); i++)
			//for(Episode episode : season.getEpisodes())
			{
				int episodeNumber = Integer.parseInt(episodes.get(i).getNumber());
				episodeNumber -= offSet;
				System.out.println("\t\tEpisode: '" + episodeNumber + "'");
				// The_Big_Bang_Theory/season-3/episode-4
				try
				{
					fullUrl = url + "/season-" + seasonNumber + "/episode-" + episodeNumber;
					page = crawler.getPage(fullUrl);
					episodes.get(i).setVideoLink(fullUrl);
				} catch (IOException e)
				{
					System.out.println("\t\tError for '" + fullUrl + "'");
				}
				offSet += episodes.size();
			}
		}
	}
}