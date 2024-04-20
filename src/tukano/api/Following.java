package tukano.api;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;



@Entity
public class Following {

	@Id
	String followedUserId;
	List<String> userFollows = new ArrayList<String>();
	List<String> userFollowers = new ArrayList<String>();
	
	
	public Following() {}
	
	public Following(String followedUserId) {
		this.followedUserId = followedUserId;
	}
	
	public String getUserId() {
		return followedUserId;
	}
	
	public void setUserId(String followedUserId) {
		this.followedUserId = followedUserId;
	}
	
	public List<String> getFollows() {
		return userFollows;
	}
	
	public void setFollows(List<String> userFollows) {
		this.userFollows = userFollows;
	}
	
	public List<String> getFollowers() {
		return userFollowers;
	}
	
	public void setFollowers(List<String> followers) {
		this.userFollowers = followers;
	}
	

}