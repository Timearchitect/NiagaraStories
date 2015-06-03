package se.mah.k3;

public interface Health {
	float MIN_HEALTH=0,MAX_HEALTH=999;
	void setHealth(float _amount);
	void damage(float _amount);
	void dead();


}
