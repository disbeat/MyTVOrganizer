package dataGathering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import data.list.Episode;
import data.list.Season;
import data.list.Show;

public class SideReelCrawler
{
	public final String PAGE_SITE = "http://www.sidereel.com/";
	
	public SideReelCrawler(){}
	
	public void multi(List<Show> allShows, int numberOfThreads)
	{
		// create a thread pool with n threads
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for(int i=0; i < allShows.size(); i++)
        {
    		//System.out.println("Sending show: '" + allShows.get(i).getName() + "'");
    		executorService.submit(new GetVideoLinkTask(allShows.get(i), this));
        }
        // prevent other tasks from being added to the queue
        executorService.shutdown();
        try
		{
			boolean result = executorService.awaitTermination(20, TimeUnit.MINUTES);
			if(result == false)
				System.out.println("Something went wrong!!!");
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void addEpisodesVideoLinks(List<Show> allShows)
	{
		String page, url, fullUrl = null;
		for(Show show : allShows)
		{
			if(show.getName().startsWith("pok"));
			
			System.out.println("Show: '" + show.getName() + "'");
			url = PAGE_SITE + show.getName().replace(' ', '_');
//			try
//			{
//				url = PAGE_SITE + show.getName().replace(' ', '_');
//				page = getPage(url);
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				System.out.println("No page for show: '" + show.getName() + "'");
//				continue;
//			}
//			
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
//					try
//					{
						fullUrl = url + "/season-" + seasonNumber + "/episode-" + episodeNumber;
						//checkUrl(fullUrl);
						//page = getPage(fullUrl);
						episodes.get(i).setVideoLink(fullUrl);
//					} catch (IOException e)
//					{
//						System.out.println("\t\tError for '" + fullUrl + "'");
//					}
				}
				offSet += episodes.size();
			}
		}
			
	}
	
	private void checkUrl(String url) throws IOException
	{
		URL tvUrl = new URL(url);
		URLConnection connection = tvUrl.openConnection();
	}
	
	// dado um link devolve a página correspondente como uma string
	public String getPage(String url) throws IOException
	{
		String page = null;
		URL tvUrl = new URL(url);
		URLConnection connection = tvUrl.openConnection();
		connection.setRequestProperty("User-Agent", "");
		BufferedReader br = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
		StringBuffer buffer = new StringBuffer(1000);
		String line = null;
		while ((line = br.readLine()) != null)
			buffer.append(line);
		br.close();
		page = buffer.toString();
		return page;
	}
}
