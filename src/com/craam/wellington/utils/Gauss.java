package com.craam.wellington.utils;

/*   Portado de Fortran para Java por: * Wellington Cruz
 *   CRAAM - Centro de Radio Astronomia e Astrofísica MAckenzie
 * 
 * 
 *   gauss.f: Points and weights for Gaussian quadrature                 
 *   taken from: "Projects in Computational Physics" by Landau and Paez  c
 *   copyrighted by John Wiley and Sons, New York
 *   written by: Oregon State University Nuclear Theory Group   
 *   Guangliang He & Rubin H. Landau
 *   code copyrighted by RH Landau
 *   
 *   supported by: US National Science Foundation, Northwest Alliance
 *                 for Computational Science and Engineering (NACSE),
 *                 US Department of Energy
 *   
 *   
 */

public class Gauss {

	final Double eps = 3e-16;
	final Double zero = 0.;
	final Double one = 1.;
	final Double two = 2.;
	final Double half = .5;
	final Double quarter = .25;
	
	protected int points;
	protected Double a,b;
	protected Double t,t1,pp,p1,p2,p3,aj;
	protected Double x[], w[];

	public Gauss(int points, Double a, Double b){
		
		this.points = points;
		this.a = a;
		this.b = b;
		
		this.x = new Double[points+1];
		this.w = new Double[points+1];
		
		for(int i=1; i <= (points+1)/2; i++){
			t = Math.cos(Math.PI*(i-quarter)/(points+half));
			do {
				p1 = one;
				p2 = zero;
				aj = zero;
				for(int j=1; j<=points; j++){
					p3 = p2;
					p2 = p1;
					aj = aj + one;
					p1 = ((two*aj-one)*t*p2-(aj-one)*p3)/aj;
				}
				
				pp = points*(t*p1-p2)/(Math.pow(t, 2)-one);
	            t1 = t;
		        t = t1-p1/pp;
		        x[i] = -t;
		        x[points+1-i]=t;
		        w[i] = two/((one-t*t)*Math.pow(pp, 2));
		        w[points+1-i]=w[i];
		        
			} while (Math.abs(t-t1) > eps);
			
		}
	}
	
	public void RescaleGrid(){ // rescale the grid points // JOB=0
		
		for(int i=1;i<=points;i++){
                x[i]=x[i]*(b-a)/two+(b+a)/two;
                w[i]=w[i]*(b-a)/two;
		}
	}

	public void ScaleToZeroB(){ //  scale to (0,b) with 50% points inside (0,ab/(a+b)) // JOB=1
		
		Double xi;
		for(int i=1;i<=points;i++){
				xi=x[i];
				x[i]=a*b*(one+xi)/(b+a-(b-a)*xi);
				w[i]=w[i]*two*a*b*b/((b+a-(b-a)*xi)*(b+a-(b-a)*xi));
		}
	}
	
	public void ScaleToAInf(){ // scale to (a,inf) with 50% points inside (a,b+2a) // JOB=2
		
		Double xi;
		for(int i=1;i<=points;i++){
				xi=x[i];
				x[i]=(b*xi+b+a+a)/(one-xi);
				w[i]=w[i]*two*(a+b)/((one-xi)*(one-xi));
		}
	}

	public Double[] getGrid(){
		return x;
	}
	
	public Double[] getWeights(){
		return w;
	}
	

}
