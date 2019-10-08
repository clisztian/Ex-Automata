package jxmaps;

import java.awt.Color;

import javax.swing.JToolTip;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

class MyWaypoint implements Waypoint {

	private Color mypaint;
	private GeoPosition myPosition;
	private String myInfo;

	
	public MyWaypoint(Color mypaint, GeoPosition myPosition, String info) {
		
		this.mypaint = mypaint;
		this.myPosition = myPosition;
		this.myInfo = info;

	}
	
	@Override
	public GeoPosition getPosition() {
		return this.myPosition;
	}

	public Color getMypaint() {
		return mypaint;
	}

	public void setMypaint(Color mypaint) {
		this.mypaint = mypaint;
	}

	public String getMyInfo() {
		return myInfo;
	}

	public void setMyInfo(String myInfo) {
		this.myInfo = myInfo;
	}
	

	
}
