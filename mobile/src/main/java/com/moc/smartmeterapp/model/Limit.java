package com.moc.smartmeterapp.model;

import java.io.Serializable;

public class Limit implements Serializable {
	
	public interface ILimitEventHandler {
		void onLimitReached(Limit limit,  float value);
		void onLimitLeave(Limit limit,  float value);
	}
	
	private ILimitEventHandler eventHandler;
	
	private int min;
	private int max;
	private int color;
	public boolean limitReached = false;
	
	public Limit(int min, int max, int color) {
		this.min = min;
		this.max = max;
		this.color = color;
	}
	
	public Limit() {
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public ILimitEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(ILimitEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public boolean equals(Limit limit) {
		if(limit != null) {
			if(max == limit.getMax() && min == limit.getMin() && color == limit.getColor()) {
				return true;
			}
		}

		return false;
	}
}
