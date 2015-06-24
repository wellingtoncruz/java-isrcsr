package com.craam.wellington.isrcsr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.craam.wellington.statics.Constants;
import com.craam.wellington.utils.MathUtils;

public class GenericSource {

	//Setado pelo usuário
	protected Double size; // Tamanho da fonte
	protected Double height; // Altura da fonte	
	
	protected Enviroment enviroment;

	//Calculado internamente
	protected Double radius;
	protected Double area;
	protected Double omega;
	protected Double volume;
	protected Double density;

	protected Integer viewAngle; 
	protected Boolean useRazin = true;
	
	public GenericSource(Enviroment enviroment){
		this.enviroment = enviroment;
	}
	
	public Double getSize(){
		return size;
	}

	public Double getHeight(){
		return height;
	}

	public Double getRadius() {
		return radius;
	}

	public Double getArea() {
		return area;
	}

	public Double getOmega() {
		return omega;
	}

	public Double getVolume() {
		return volume;
	}

	public Double getDensity() {
		return density;
	}
	
	public Enviroment getEnviroment(){
		return enviroment;
	}

	public Boolean getUseRazin() {
		return useRazin;
	}

	public GenericSource setHeight(Double height){
		this.height = height;
		if(this.area != null) this.setVolume();
		return this;
	}
	
	protected GenericSource setVolume(){
		this.volume = this.area * this.height;
		return this;
	}

	public GenericSource setViewAngle(int viewAngle){
		this.viewAngle = viewAngle;
		return this;
	}
	
	public void setUseRazin(Boolean useRazin) {
		this.useRazin = useRazin;
	}
	
	public class Frequency {
		
		protected Integer frequencyIndex;
		protected Double frequencyValue;
		protected Double frequencyHarmonic;
		
		protected RefractionPolarization refractionPolarization;
		protected Radiation radiation;
		
		protected Double energyTreshould, microbunchTreshould;
		protected Double cos, sin;
		
		protected Double ordEmissivityLow=0., extraordEmissivityLow=0., ordEmissivityHigh=0., extraordEmissivityHigh=0.;
		protected Double ordAbsorptionLow=0., extraordAbsorptionLow=0., ordAbsorptionHigh=0., extraordAbsorptionHigh=0.;
		protected Double ordEmissivity, extraordEmissivity;
		

		public Frequency(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double energyTreshould, Double microbunchTreshould){
			
			this.cos = Math.cos(viewAngle * Constants.DEGREE_TO_RAD);
			this.sin = Math.sin(viewAngle * Constants.DEGREE_TO_RAD);
			
			this.frequencyIndex = index;
			this.frequencyValue = frequencyValue;
			this.frequencyHarmonic = frequencyValue / getEnviroment().getGyroFrequency();
			this.refractionPolarization = new RefractionPolarization();
			this.energyTreshould = energyTreshould;
			this.microbunchTreshould = microbunchTreshould;
			
			// Faz a integração sobre a energia
			Double el,eu,em;

			for(int i=energyLower; i<=energuUpper; i++){
				el = Math.pow(10, (0.05*(i-1)-2));
				eu = Math.pow(10, (0.05*i-2));
				em = Math.pow(10, (0.05*(i-0.5)-2));
				Double de = eu-el;
				Double gamma = em / Constants.ELECTRON_REST_ENERGY + 1;
	
				radiation = new Radiation(gamma);

				if(em < microbunchTreshould){
					
					// Low Energy Electrons
					ordEmissivityLow += getEmissivity(em, de, energyEspectrum, radiation.getOrdEmissivityIndex());
					ordAbsorptionLow +=  getAbsorption(em, de, energyEspectrum, gamma, radiation.getOrdEmissivityIndex());
					extraordEmissivityLow += getEmissivity(em, de, energyEspectrum, radiation.getExtraordEmissivityIndex());
					extraordAbsorptionLow +=  getAbsorption(em, de, energyEspectrum, gamma, radiation.getExtraordEmissivityIndex());
					
				} else {
					
					// High Energy Electrons
					ordEmissivityHigh += getEmissivity(em, de, energyEspectrum, radiation.getOrdEmissivityIndex());
					ordAbsorptionHigh +=  getAbsorption(em, de, energyEspectrum, gamma, radiation.getOrdEmissivityIndex());
					extraordEmissivityHigh += getEmissivity(em, de, energyEspectrum, radiation.getExtraordEmissivityIndex());
					extraordAbsorptionHigh +=  getAbsorption(em, de, energyEspectrum, gamma, radiation.getExtraordEmissivityIndex());
				}
			}
			
			ordEmissivity = ordEmissivityLow + ordEmissivityHigh;
			extraordEmissivity = extraordEmissivityHigh + extraordEmissivityLow;

		}
	
		public Double getFrequencyValue(){
			return frequencyValue;
		}

		public Double getFrequencyHarmonic(){
			return frequencyHarmonic;
		}

		public RefractionPolarization getRefractionPolarization(){
			return refractionPolarization;
		}
		
		public Double getEmissivity(Double em, Double de, Double energyEspectrum, Double emissivityIndex){
			return (emissivityIndex * Math.pow(em, -energyEspectrum) * de * getEnviroment().getMagneticField() * Constants.EMISSIVITY_FACTOR);
		}
			
		public Double getAbsorption(Double em, Double de, Double energyEspectrum, Double gamma, Double emissivityIndex) {
			return (emissivityIndex * Math.pow(em, -energyEspectrum) * de / Math.pow(frequencyHarmonic, 2) / getEnviroment().getMagneticField() * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1));
		}
			
	
		protected class RefractionPolarization {
	
			private Double ordRefractionIndex, extraordRefractionIndex;
			private Double ordPolarizationCoefficient, extraordPolarizationCoefficient;
			
			private RefractionPolarization(){
				
				// Refraction Index
				Double refractionNum = 2 * Math.pow(getEnviroment().getFrequencyRatio(),2) * (Math.pow(getEnviroment().getFrequencyRatio(),2) - Math.pow(getFrequencyHarmonic(),2));
				Double ordRefractionDnum = +Math.sqrt(Math.pow(getFrequencyHarmonic(),4) * Math.pow(sin,4) + 4 * Math.pow(getFrequencyHarmonic(),2)*Math.pow((Math.pow(getEnviroment().getFrequencyRatio(),2)-Math.pow(getFrequencyHarmonic(),2)), 2) * Math.pow(cos,2)) - 2 * Math.pow(getFrequencyHarmonic(),2) * (Math.pow(getEnviroment().getFrequencyRatio(),2) - Math.pow(getFrequencyHarmonic(),2)) - Math.pow(getFrequencyHarmonic(), 2) * Math.pow(sin, 2);
				Double extraordRefractionDnum = -Math.sqrt(Math.pow(getFrequencyHarmonic(),4) * Math.pow(sin,4) + 4 * Math.pow(getFrequencyHarmonic(),2)*Math.pow((Math.pow(getEnviroment().getFrequencyRatio(),2)-Math.pow(getFrequencyHarmonic(),2)), 2) * Math.pow(cos,2)) - 2 * Math.pow(getFrequencyHarmonic(),2) * (Math.pow(getEnviroment().getFrequencyRatio(),2) - Math.pow(getFrequencyHarmonic(),2)) - Math.pow(getFrequencyHarmonic(), 2) * Math.pow(sin, 2);
	
				this.ordRefractionIndex = 1 + refractionNum / ordRefractionDnum;
				this.extraordRefractionIndex = 1 + refractionNum / extraordRefractionDnum;
	
				// Polarization Index
				Double polarizationNum = 2 * getFrequencyHarmonic() * (Math.pow(getEnviroment().getFrequencyRatio(),2) - Math.pow(getFrequencyHarmonic(),2)) * cos;
				Double ordPolarizationDnum = +Math.sqrt(Math.pow(getFrequencyHarmonic(), 4) * Math.pow(sin,4) + 4 * Math.pow(getFrequencyHarmonic(), 2) * Math.pow((Math.pow(getEnviroment().getFrequencyRatio(), 2) - Math.pow(getFrequencyHarmonic(), 2)), 2)* Math.pow(cos, 2)) - Math.pow(getFrequencyHarmonic(),2) *  Math.pow(sin,2);
				Double extraordPolarizationDnum = -Math.sqrt(Math.pow(getFrequencyHarmonic(), 4) * Math.pow(sin,4) + 4 * Math.pow(getFrequencyHarmonic(), 2) * Math.pow((Math.pow(getEnviroment().getFrequencyRatio(), 2) - Math.pow(getFrequencyHarmonic(), 2)), 2)* Math.pow(cos, 2)) - Math.pow(getFrequencyHarmonic(),2) *  Math.pow(sin,2);
	
			    this.ordPolarizationCoefficient = -polarizationNum / ordPolarizationDnum;
			    this.extraordPolarizationCoefficient = -polarizationNum / extraordPolarizationDnum;
			}
			
	
			public Double getOrdRefractionIndex(){
				return ordRefractionIndex;
			}
			public Double getExtraordRefractionIndex(){
				return extraordRefractionIndex;
			}
			public Double getOrdPolarizationCoefficient(){
				return ordPolarizationCoefficient;
			}
			public Double getExtraordPolarizationCoefficient(){
				return extraordPolarizationCoefficient;
			}
	
		}
	
		protected class Radiation {
			
			Double gamma, gammaTreshould;
			Double ordEmissivityIndex = 0.;
			Double extraordEmissivityIndex = 0.;
			Double refraction = 0.;
			Double polarization= 0.;
	
			protected Radiation(Double gamma){
				this.gamma = gamma;
				this.gammaTreshould = energyTreshould / Constants.ELECTRON_REST_ENERGY + 1;

				
				if(this.gamma < this.gammaTreshould){

					if(getFrequencyHarmonic() > getEnviroment().getFrequencyRatio()){
						this.refraction = Math.sqrt(refractionPolarization.getOrdRefractionIndex());
						this.polarization = refractionPolarization.getOrdPolarizationCoefficient();
						this.ordEmissivityIndex = this.GyroSincrotron();
					}
					
					if(getUseRazin()){
						if(getFrequencyHarmonic() > (Math.sqrt(Math.pow(getEnviroment().getFrequencyRatio(),2) + 0.25) + 0.5)){
							this.refraction = Math.sqrt(refractionPolarization.getExtraordRefractionIndex());
							this.polarization = refractionPolarization.getExtraordPolarizationCoefficient();
							this.extraordEmissivityIndex = this.GyroSincrotron();
						}
					}
					
				
				} else if(this.gamma >= this.gammaTreshould){
						
					if(getUseRazin()){
						if(getFrequencyHarmonic() > (Math.sqrt(Math.pow(getEnviroment().getFrequencyRatio(),2) + 0.25) + 0.5)){
							Double ssyValue = this.Synchrotron();
							this.ordEmissivityIndex = ssyValue;
							this.extraordEmissivityIndex = ssyValue;
						}
					} else {
						Double ssyValue = this.Synchrotron();
						this.ordEmissivityIndex = ssyValue;
						this.extraordEmissivityIndex = ssyValue;
					}
				}
			}
			
			
			protected Double GyroSincrotron(){
				
				Double beta = Math.sqrt(gamma*gamma-1)/gamma;
				Double ffc = getFrequencyHarmonic() * 2. / 3. / sin / gamma / gamma;
				
				if(ffc >= 20){
					return 0.;
				} else {
					
					Double ordS = (getFrequencyHarmonic() * gamma * (1 - this.refraction * beta * cos) +1);
					Double extraordS = (getFrequencyHarmonic() * gamma * (1 + this.refraction * beta * cos));
	
					Double sum = 0.;
	
					for(Double i=Math.floor(ordS); i<=Math.floor(extraordS); i++){
						Double cosPhi = (1 -i/getFrequencyHarmonic()/gamma) / beta / cos / this.refraction;
						Double sinPhi = Math.sqrt(1 -Math.pow(cosPhi, 2));
						Double xs = i * this.refraction * beta * sin * sinPhi / (1 -this.refraction * beta * cos * cosPhi);
						Double xstr;
	
						if(getFrequencyHarmonic() > 50){
							xstr = 0.8;
							if(gamma > 5) xstr = 0.9;
							if(gamma > 10) xstr = 0.95;
							if(gamma > 15) xstr = 0.96;
							if(xs < (xstr*i)) {
								continue;
							}
						}
						
						if(getFrequencyHarmonic() > 250){
							xstr = 0.9;
							if(gamma > 5) xstr = 0.92;
							if(gamma > 10) xstr = 0.97;
							if(gamma > 15) xstr = 0.98;
							if(xs < (xstr*i)){
								continue;
							}
						}
	
						Double bessel = MathUtils.Bessel(i.intValue(), xs);
						Double besselPr = MathUtils.BesselPr(i.intValue(), xs);
						
						Double f = Math.pow((-beta * sinPhi * besselPr + polarization * (cos / sin / refraction - beta * cosPhi / sin)*bessel),2);
						Double oldSum = sum;
						sum = sum + f;
					
						if(oldSum >0)
							if( ((sum-oldSum)/oldSum) < (1e-4)){
								break;
						}
				
					}
	
					return sum / beta / 2 / cos * getFrequencyHarmonic() / (1 + Math.pow(polarization,2));
				}
			}
			
			protected Double Synchrotron(){
				
				Double f =0.;
				Double ffc = getFrequencyHarmonic()*2 / 3 / sin / gamma / gamma * Math.pow((1 + 9 / 4 * (Math.pow(gamma, 2) -1) / Math.pow(getEnviroment().getRazinParameter(), 2) / Math.pow(getFrequencyHarmonic(), 2)), 1.5);
				if(ffc <= Constants.SSY_XD[0]){
					f = 2.15*ffc*0.333*(1-0.844*ffc*0.666);
				} else if(ffc > Constants.SSY_XD[33]){
					f = 1.253 * Math.exp(-ffc) * Math.sqrt(ffc) * (1 + 55/72/ffc);
				} else {
					f = this.Sear(ffc);
				}
				return (0.138 * sin * f) / 2;
			}
			
			protected Double Sear(Double ffc){
				int i=0;
				while(i < Constants.SSY_XD.length){
					if(Constants.SSY_XD[i] >= ffc) 	return (Constants.SSY_FD[i] * (ffc-Constants.SSY_XD[i-1])  + Constants.SSY_FD[i-1] * (Constants.SSY_XD[i] - ffc)) / (Constants.SSY_XD[i]-Constants.SSY_XD[i-1]);
					i++;
				}
				return 0.;
			}
			
			protected Double getOrdEmissivityIndex() {
				return this.ordEmissivityIndex;
			}
	
			protected Double getExtraordEmissivityIndex() {
				return this.extraordEmissivityIndex;
			}
			
			protected Double getRefraction(){
				return this.refraction;
			}
	
		}
	
		protected class Stokes {
			
			protected Double Q,V;
			
			protected Stokes(Double phiOrd, Double phiExtraord){
				this.Q = this.Q(phiOrd, phiExtraord);
				this.V = this.V(phiOrd, phiExtraord);
			}
			
			public Double Q(Double phiOrd, Double phiExtraord){
				return 	(phiOrd * (1 - Math.pow(getRefractionPolarization().getOrdPolarizationCoefficient(),2)) / (1 + Math.pow(getRefractionPolarization().getOrdPolarizationCoefficient(),2))) +
				  	    (phiExtraord * (1 - Math.pow(getRefractionPolarization().getExtraordPolarizationCoefficient(),2)) / (1 + Math.pow(getRefractionPolarization().getExtraordPolarizationCoefficient(),2)));
			}
			
			public Double V(Double phiOrd, Double phiExtraord){
				return 2 * (phiOrd * getRefractionPolarization().getOrdPolarizationCoefficient() / (1 + Math.pow(getRefractionPolarization().getOrdPolarizationCoefficient(), 2))) + (phiExtraord * getRefractionPolarization().getExtraordPolarizationCoefficient() / (1 + Math.pow(getRefractionPolarization().getExtraordPolarizationCoefficient(), 2)));
			}
			
			public Double getQ() {
				return Q;
			}
	
			public Double getV() {
				return V;
			}
			
		}
		
		protected class Fluxes {
			
			protected Double FluxPolarization(Double phiTotal, Double phiOrd, Double phiExtraord){
				return (phiTotal > 0) ? (phiExtraord-phiOrd) / phiTotal : 0.;
			}
			
			protected Double Phi(Double emissivity, Double absorption, Double sourceHeight, Double sourceVolume, Double sourceOmega){
				
				Double phi;
				Double arg = absorption * sourceHeight;
				
				if(arg < 1e-3){
					phi = emissivity * sourceVolume / Math.pow(Constants.UA, 2);
				} else {
					phi = sourceOmega * emissivity / absorption * (1 - Math.exp(-arg));
				}
				
				return phi;
			}
	
		}
		
	}

}
