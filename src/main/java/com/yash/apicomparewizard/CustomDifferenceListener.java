package com.yash.apicomparewizard;

import java.util.ArrayList;
import java.util.List;

import net.javacrumbs.jsonunit.core.listener.Difference;
import net.javacrumbs.jsonunit.core.listener.DifferenceContext;
import net.javacrumbs.jsonunit.core.listener.DifferenceListener;

public class CustomDifferenceListener implements DifferenceListener {
	 private final List<Difference> differences = new ArrayList<>();

	@Override
	public void diff(Difference difference, DifferenceContext context) {
		 differences.add(difference);
	}
	
	 public List<Difference> getDifferences() {
	        return differences;
	    }

}
