import java.io.Serializable;

public class Message implements Serializable {

	private int xUID;
	private int distance;
	private int round;
	
	public Message(){
		this.round = 0;
		this.distance = 0;
	}

	public int getxUID() {
		return xUID;
	}

	public void setxUID(int xUID) {
		this.xUID = xUID;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	@Override
	public String toString() {
		return "Message [xUID=" + getxUID() + ", distance=" + getDistance() + ", round=" + getRound() + "]";
	}
}
