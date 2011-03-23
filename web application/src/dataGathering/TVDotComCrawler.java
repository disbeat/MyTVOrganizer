package dataGathering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.list.Actor;
import data.list.Role;
import data.list.Show;

public class TVDotComCrawler
{
	final private static int PAGE_BUFFER_SIZE = 10000;
	// por exemplo http://www.tv.com/shows/browse.html?l=A
	final private static String ALL_SHOWS_URL = "http://www.tv.com/shows/browse.html?l=";
	// por exemplo http://www.tv.com/people/browse.html?l=S
	final private static String ALL_PEOPLE_URL = "http://www.tv.com/people/browse.html?l=";
	// por exemplo <div class="page_count">1 - 100 of 162</div>
	final private static String PAGE_COUNT_PATTERN = "<div class=\"page_count\">(.*?)</div>";
	final private static String PAGE_NUMBER_PATTERN = "<div class=\"pagination\">.*?<ul>(.*?)</ul>";
	// group 1 => url to show, 2, => show name, 3 => date, 4 => rating
	final private static String SHOW_LISTING_PATTERN = "<td class=\".*?\"><a href=\"(.*?)\">(.*?)</a>.*?<td class=\"item_detail\">(.*?)</td>.*?<td class=\"score\">(.*?)</td>";
	// group 1 => url to person, 2, => person name, 3 => url to latest project, 4 => latest project, 5 => rating
	final private static String PEOPLE_LISTING_PATTERN = "<td class=\".*?\"><a href=\"(.*?)\">(.*?)</a>.*?<td class=\"item_detail\"><a href=\"(.*?)\">(.*?)</a>.*?<td class=\"score\">(.*?)</td>";
	// <li>1&#160;</li><li><a href="http://www.tv.com/shows/browse/all.html?l=9&era=&pg=1&tag=pagination;2">2</a></li>
	final private static String NUMBER_OF_PAGES_PATTERN = "<li>.*?<a.*?>(.*?)</a>";
	
	final private static String VOTES_NUMBER_PATTERN = "<span class=\"num_votes\">(.*?)</span>";
	
	private Hashtable<Integer, Actor> actors = new Hashtable<Integer, Actor>();

	public TVDotComCrawler(){}
	
	public synchronized void addActor(Actor actor, Role role)
	{
		int id = actor.getId();
		if(actors.get(id) == null)
			actors.put(id, actor);
		else
			actors.get(id).addRole(role);
	}
	
	public void addActorsInfo(List<Show> allShows, int numberOfThreads)
	{
		// create a thread pool with n threads
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for(int i=0; i < allShows.size(); i++)
        {
    		//System.out.println("Sending show: '" + allShows.get(i).getName() + "'");
    		executorService.submit(new GetActorsTask(this, allShows.get(i)));
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
	
	public void addVotesInfo(List<Show> allShows, int numberOfThreads, int minimumVotes)
	{
		// create a thread pool with n threads
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for(int i=0; i < allShows.size(); i++)
        {
        	if(allShows.get(i).getIntegerRating() >= 7.5)
        	{
        		//System.out.println("Sending show: '" + allShows.get(i).getName() + "'");
        		executorService.submit(new GetVotesTask(this, allShows.get(i)));
        	}
        }
        // prevent other tasks from being added to the queue
        executorService.shutdown();
        try
		{
			boolean result = executorService.awaitTermination(10, TimeUnit.MINUTES);
			if(result == false)
				System.out.println("Something went wrong!!!");
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getTotalNumberOfShows() throws IOException
	{
		ArrayList<String> allURLs = createAllShowsURLs();
		int totalShows = 0;
		for (String url : allURLs)
			totalShows += getNumberOfShows(url);
		return totalShows;
	}
	
	public int getTotalNumberOfPeople() throws IOException
	{
		ArrayList<String> allURLs = createAllPeopleURLs();
		int totalPeople = 0;
		for (String url : allURLs)
			totalPeople += getNumberOfPeople(url);
		return totalPeople;
	} 
	
	public int parseNumberOfVotes(String str)
	{
		str = str.trim();
		str = str.replaceAll(",", "");
		int index = str.indexOf(" ");
		str = str.substring(0, index);
		int res = 0;
		try {
			res = Integer.parseInt(str);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private int getNumberOfVotes(Show show)
	{
		try
		{
			String page = getPage(show.getShowLink());
			String temp = getMatch(VOTES_NUMBER_PATTERN, page, 1, 1);
			if(temp != null)
			{
				return parseNumberOfVotes(temp);
			}
			
		} catch (IOException e)
		{
			System.out.println("Error on show link: '" + show.getShowLink() + "'");
		}
		
		return 0;
	}
	
//	private void completeShowInfo(Show show)
//	{
//		try
//		{
//			String page = getPage(show.getShowLink());
//			String castURL = getMatch(CAST_PATTERN, page, 1, 1);
//			if(castURL == null)
//			{
//				System.out.println("No cast for show: '" + show.getName() + "'!!!!!!!" );
//				return;
//			}
//			
//			scrapCastInfo(show, castURL);
//			
//		} catch (IOException e)
//		{
//			System.out.println("Error on show link: '" + show.getShowLink() + "'");
//		}
//	}
//	
//	private void scrapCastInfo(Show show, String castURL)
//	{
//		try
//		{
//			String page = getPage(castURL);
//			Pattern pattern = Pattern.compile(SHOW_LISTING_PATTERN);
//			Matcher matcher = pattern.matcher(page);
//			System.out.println("		### Page 1 ###");
//			while (matcher.find())
//			{
//				
//			}
//			
//		} catch (IOException e)
//		{
//			System.out.println("Error on show cast link: '" + castURL + "'");
//		}
//	}
	
	// vai buscar o nome e link de todas as series limitando as series com um minimo de votos
	public ArrayList<Show> scrapAllShows(int minimumVotes) throws IOException
	{
		long start = System.currentTimeMillis();
		ArrayList<Show> shows = new ArrayList<Show>();
		ArrayList<String> allURLs = createAllShowsURLs();
		String page, temp;
		for (String s : allURLs)
		{
			page = getPage(s);
			temp = getMatch(PAGE_NUMBER_PATTERN, page, 1, 1);
			// System.out.println(s + ": " + temp);
			// get shows from page 1
			Pattern pattern = Pattern.compile(SHOW_LISTING_PATTERN);
			Matcher matcher = pattern.matcher(page);
			System.out.println("		### Page 1 ###");
			while (matcher.find())
			{
				Show newShow = parseShowInfo(matcher);
				int votes = getNumberOfVotes(newShow);
				
				if(votes < minimumVotes)
					continue;
				else
					System.out.println("Show: " + newShow.getName() + "\nVotes: " + votes + "\n");
				
//				completeShowInfo(newShow);
				shows.add(newShow);
				/*System.out.println("Name: " + matcher.group(2));
				System.out.println("URL: " + matcher.group(1));
				System.out.println("Date: " + matcher.group(3).trim());
				System.out.println("Rating: " + matcher.group(4));*/
				//getSummary(matcher.group(1));
			}

			// se há mais do que uma página
			if (temp != null)
			{
				// get number of pages
				int nPages = parseNumberOfPages(temp);
				System.out.println("		Number of pages: " + nPages);
				for (int i = 1; i < nPages; i++)
				{
					page = getPage(s + "&pg=" + i);
					System.out.println("		### Page " + (i + 1) + " ###");
					pattern = Pattern.compile(SHOW_LISTING_PATTERN);
					matcher = pattern.matcher(page);
					while (matcher.find())
					{
						Show newShow = parseShowInfo(matcher);
						int votes = getNumberOfVotes(newShow);
						
						if(votes < minimumVotes)
							continue;
						else
							System.out.println("Show: " + newShow.getName() + "\nVotes: " + votes + "\n");
						
//						completeShowInfo(newShow);
						shows.add(newShow);
						
						/*System.out.println("Name: " + matcher.group(2));
						System.out.println("URL: " + matcher.group(1));
						System.out.println("Date: " + matcher.group(3).trim());
						System.out.println("Rating: " + matcher.group(4));*/
						//getSummary(matcher.group(1));
					}
				}
			}
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - start));
		return shows;
	}
	
	private Show parseShowInfo(Matcher matcher)
	{
		Show show = new Show();
		show.setName(matcher.group(2));
		show.setShowLink(matcher.group(1));
		show.setDate(matcher.group(3).trim());
		show.setTvDotComRating(matcher.group(4));
		return show;
	}
	
	// vai buscar o nome e link de todas as series
	public ArrayList<Show> scrapAllShows() throws IOException
	{
		return scrapAllShows(0);
	}
	
	public void scrapAllPeople() throws IOException
	{
		long start = System.currentTimeMillis();
		ArrayList<String> allURLs = createAllPeopleURLs();
		String page, temp;
		for (String s : allURLs)
		{
			page = getPage(s);
			temp = getMatch(PAGE_NUMBER_PATTERN, page, 1, 1);
			// System.out.println(s + ": " + temp);
			// get shows from page 1
			Pattern pattern = Pattern.compile(PEOPLE_LISTING_PATTERN);
			Matcher matcher = pattern.matcher(page);
			System.out.println("		### Page 1 ###");
			while (matcher.find())
			{
				//System.out.println(matcher.group(2));
				System.out.println("Name: " + matcher.group(2));
				System.out.println("URL: " + matcher.group(1));
				System.out.println("URL to latest project: " + matcher.group(3));
				// if URL to latest project => ?tag=;summary então não há informação
				System.out.println("Latest project: " + matcher.group(4));
				System.out.println("Rating: " + matcher.group(5));
			}

			// se há mais do que uma página
			if (temp != null)
			{
				// get number of pages
				int nPages = parseNumberOfPages(temp);
				System.out.println("		Number of pages: " + nPages);
				for (int i = 1; i < nPages; i++)
				{
					page = getPage(s + "&pg=" + i);
					System.out.println("		### Page " + (i + 1) + " ###");
					pattern = Pattern.compile(PEOPLE_LISTING_PATTERN);
					matcher = pattern.matcher(page);
					while (matcher.find())
					{
						//System.out.println(matcher.group(2));
						System.out.println("Name: " + matcher.group(2));
						System.out.println("URL: " + matcher.group(1));
						System.out.println("URL to latest project: " + matcher.group(3));
						// if URL to latest project => ?tag=;summary então não há informação
						System.out.println("Latest project: " + matcher.group(4));
						System.out.println("Rating: " + matcher.group(5));
					}
				}
			}
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - start));
	}

	// retira o número máximo de páginas
	private int parseNumberOfPages(String s)
	{
		Pattern pattern = Pattern.compile(NUMBER_OF_PAGES_PATTERN);
		Matcher matcher = pattern.matcher(s);
		int highestNumber = 1;
		while (matcher.find())
		{
			try
			{
				highestNumber = Integer.parseInt(matcher.group(1));
			} catch (NumberFormatException e){
				highestNumber = Integer.parseInt(matcher.group(1).substring(3)
						.trim());
			}
		}
		return highestNumber;
	}

	// devolve o número de series dado o URL necessário
	private int getNumberOfShows(String url) throws IOException
	{
		String page = getPage(url);
		return parseTotalNumber(getMatch(PAGE_COUNT_PATTERN, page, 1, 1));
	}
	
	// devolve o número de pessoas dado o URL necessário
	private int getNumberOfPeople(String url) throws IOException
	{
		String page = getPage(url);
		return parseTotalNumber(getMatch(PAGE_COUNT_PATTERN, page, 1, 1));
	}

	// retira o número desejado de uma string do tipo "1 - 100 of 162"
	private int parseTotalNumber(String s)
	{
		int i = s.indexOf("of");
		return Integer.parseInt(s.substring(i + 2).trim());
	}
	
	public void multiThreadedTest()
	{
		ArrayList<String> allURLs = createAllShowsURLs();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		Thread t;
		for(String s : allURLs) 
		{
			t = new CrawlerThread(s);
			threads.add(t);
			t.start();
		}
		for(Thread temp : threads) 
		{
			try { 
				temp.join(); 
			} catch (InterruptedException e)
			{ 
				e.printStackTrace(); 
			} 
		}	 
	}

	// cria um arraylist com todos os URLs necessários para obter todas as series
	private ArrayList<String> createAllShowsURLs()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add(ALL_SHOWS_URL + '9');
		char c = 'A';
		while (c <= 'Z')
		{
			result.add(ALL_SHOWS_URL + c);
			c++;
		}
		return result;
	}
	
	// cria um arraylist com todos os URLs necessários para obter todas as pessoas
	private ArrayList<String> createAllPeopleURLs()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add(ALL_PEOPLE_URL + '9');
		char c = 'A';
		while (c <= 'Z')
		{
			result.add(ALL_PEOPLE_URL + c);
			c++;
		}
		return result;
	}

	// dado um link devolve a página correspondente como uma string
	public String getPage(String url) throws IOException
	{
		String page = null;
		// se a página já existe devolve o conteudo
//		String page = pages.get(url);
//		if (page != null)
//			return page;

		URL tvUrl = new URL(url);
		URLConnection connection = tvUrl.openConnection();
		connection.setRequestProperty("User-Agent", "");
		BufferedReader br = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
		StringBuffer buffer = new StringBuffer(PAGE_BUFFER_SIZE);
		String line = null;
		while ((line = br.readLine()) != null)
			buffer.append(line);
		br.close();
		page = buffer.toString();
//		// adiciona às páginas conhecidas
//		pages.put(url, page);
		return page;
	}

	// devolve a ocorrência nMatch da expressão regular regularExp da string strToSearch devolve null caso não exista tal ocorrência
	private String getMatch(String regularExp, String strToSearch, int nMatch)
	{
		Pattern pattern = Pattern.compile(regularExp);
		Matcher matcher = pattern.matcher(strToSearch);
		for (int i = 1; i <= nMatch; i++)
		{
			if (matcher.find() == false)
				return null;
		}
		return matcher.group();
	}

	// igual à anterior mas devolve um grupo especifico
	public String getMatch(String regularExp, String strToSearch, int nMatch,
			int group)
	{
		Pattern pattern = Pattern.compile(regularExp);
		Matcher matcher = pattern.matcher(strToSearch);
		for (int i = 1; i <= nMatch; i++)
		{
			if (matcher.find() == false)
				return null;
		}
		return matcher.group(group);
	}
}
