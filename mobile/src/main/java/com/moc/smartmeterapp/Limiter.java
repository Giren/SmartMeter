package com.moc.smartmeterapp;

import com.moc.smartmeterapp.model.Limit;

import java.util.ArrayList;
import java.util.List;

public class Limiter {
	
	private List<Limit> limits;
	
	public Limiter() {
		limits = new ArrayList<Limit>();
	}
	
	public void setValue(float value) {
		Limit.ILimitEventHandler eventHandler;

		for(Limit l : limits) {
			if((eventHandler = l.getEventHandler()) != null) {
				if(value >= l.getMin() && value <= l.getMax()) {
					if(l.limitReached == false) {
						eventHandler.onLimitReached(l, value);
						l.limitReached = true;
					}
				} else if(l.limitReached) {
					if(value < l.getMin() || value > l.getMax() ) {
						eventHandler.onLimitLeave(l, value);
						l.limitReached = false;
					}
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
