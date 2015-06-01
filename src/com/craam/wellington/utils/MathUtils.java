package com.craam.wellington.utils;

import cern.jet.math.tdouble.Bessel;
import com.craam.wellington.utils.FractionalBessel;

public final class MathUtils {

	public static final Double Bessel(int n, Double x){
		return Bessel.jn(n, x); 
	}
	
	public static final Double BesselPr(int n, Double x){
		int n1 = n + 1;
		Double b1 = Bessel.jn(n1, x);
		Double b  = Bessel.jn(n, x);
		return -b1 + n / x * b;
		
	}
	
	public static final Double BesselKFrac(Double n, Double x){
		FractionalBessel bessel = new FractionalBessel();
		return bessel.knu(n, x);
	}
}
