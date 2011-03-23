package dataGathering;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CrawlerThread extends Thread
{
	private String page;
	
	public CrawlerThread (String page)
	{
		this.page = page;
	}	
	
	public void run()
	{
		try
		{
			String fullPage = getPage(page);
			//System.out.println(this.getId() + ": " + fullPage);
			System.out.println(this.getId() + ": " + page);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// dado um link devolve a página correspondente como uma string
	private String getPage(String link) throws IOException
	{
		URL tvUrl = new URL(link);
		URLConnection connection = tvUrl.openConnection();
		connection.setRequestProperty("User-Agent", "");
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer(10000);
	    String line = null;
	    while ((line = br.readLine()) != null)
			buffer.append(line);
	    br.close();
	    return buffer.toString();
	}
}
