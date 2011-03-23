package data;

//TODO persist
public class Role
{
	private Actor playedBy;
	private String roleName;
	
	public Role(){}
	
	public Actor getPlayedBy()
	{
		return playedBy;
	}
	public String getRoleName()
	{
		return roleName;
	}
	public void setPlayedBy(Actor playedBy)
	{
		this.playedBy = playedBy;
	}
	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}
}
