package data;

// TODO persist
public class Rating
{
	private String source;
	private int voteCount;
	private float value;
	
	public Rating(){}
	
	public void setVoteCount(int voteCount)
	{
		this.voteCount = voteCount;
	}
	public void setValue(float value)
	{
		this.value = value;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	public int getVoteCount()
	{
		return voteCount;
	}
	public String getSource()
	{
		return source;
	}
	public float getValue()
	{
		return value;
	}
}
