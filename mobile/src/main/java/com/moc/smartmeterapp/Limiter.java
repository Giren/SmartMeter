package com.moc.smartmeterapp;

import java.util.ArrayList;
import java.util.List;

public class Limiter {
	
	private List<Limit> limits;
	
	public Limiter() {
		limits = new ArrayList<Limit>();
	}
	
	public void setValue(float value) {
		for(Limit l : limits) {
			if(value >= l.getMin() && value <= l.getMax()) {
				if(l.limitReached == false) {
					l.getEventHandler().onLimitReached(l, value);
					l.limitReached = true;
				}
			} else if(l.limitReached) {
				if(value < l.getMin() || value > l.getMax() ) {
					l.getEventHandler().onLimitLeave(l, value);
					l.limitReached = false;
				}
			}
		}
	}

	public void addLimit(Limit limit) {
		limits.add(limit);
	}

	public void removeLimit(Limit limit) {
		limits.remove(limit);
	}

	public void removeLimit(int index) {
		limits.remove(index);
	}

	public void removeAllLimits() {
		limits.clear();
	}
	
	public List<Limit> getLimits() {
		return limits;
	}
}
