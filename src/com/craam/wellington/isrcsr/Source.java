package com.craam.wellington.isrcsr;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.craam.wellington.isrcsr.ISRCSR.Frequency;
import com.craam.wellington.statics.Constants;
import com.craam.wellington.utils.MathUtils;

public class Source {
	
	//Setado pelo usuário
	private Double size; // Tamanho da fonte
	private Double height; // Altura da fonte	
	private Double magneticField; // Intensidade do campo magnético
	private Double plasmaDensity; // Densidade do Plasma
	
	private Microbunch microbunch;

	//Calculado internamente
	private Double gyroFrequency;
	private Double plasmaFrequency;
	private Double frequencyRatio;
	private Double razinParameter;
	private Double razinCutoff;
	private Double radius;
	private Double area;
	private Double omega;
	private Double volume;
	private Double density;
	
	private Double lowEnergyElectronISR;
	private Double highEnergyElectronISR;
	private Double totalElectronISR;
	
	private Integer viewAngle; 
	
	private Boolean useRazin = true;
	
	private List<FrequencyData> frequencies = new ArrayList<FrequencyData>();

	public Double getSize(){
		return size;
	}

	public Double getHeight(){
		return height;
	}

	public Double getMagneticField() {
		return magneticField;
	}

	public Double getPlasmaDensity() {
		return plasmaDensity;
	}
	
	public Double getGyroFrequency() {
		return gyroFrequency;
	}

	public Double getPlasmaFrequency() {
		return plasmaFrequency;
	}

	public Double getFrequencyRatio() {
		return frequencyRatio;
	}

	public Double getRazinParameter() {
		return razinParameter;
	}

	public Double getRazinCutoff() {
		return razinCutoff;
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

	public Source setSize(Double sourceSize) {
		this.size = sourceSize;
		this.radius = this.size * Constants.ARC_TO_CM / 2.;
		this.area = Math.PI * Math.pow(this.radius, 2);
		this.omega = this.area / Math.pow(Constants.UA, 2);
		if(this.height != null) this.setVolume();
		return this;
	}

	public Source setHeight(Double height){
		this.height = height;
		if(this.area != null) this.setVolume();
		return this;
	}
	
	private void setVolume(){
		this.volume = this.area * this.height;
	}

	public Source setViewAngle(int viewAngle){
		this.viewAngle = viewAngle;
		if(this.getPlasmaFrequency() != null && this.getGyroFrequency() != null) this.setRazinCutoff();
		return this;
	}

	public Source setMagneticField(Double magneticField) {
		this.magneticField = magneticField;
		this.gyroFrequency = 0.5 / Math.PI * (Constants.ELECTRON_CHARGE / (Constants.ELECTRON_MASS * Constants.KG_TO_G)  / (Constants.LIGHT_SPEED * Constants.MS_TO_CMS)) * this.magneticField;
		if(this.getPlasmaFrequency() != null){
			this.setFrequencyRatio();
			if(this.viewAngle != null) this.setRazinCutoff();
		}
		return this;
	}

	public Source setPlasmaDensity(Double plasmaDensity) {
		this.plasmaDensity = plasmaDensity;
		this.plasmaFrequency = Constants.ELECTRON_CHARGE * Math.sqrt(this.plasmaDensity / Math.PI / (Constants.ELECTRON_MASS * Constants.KG_TO_G));
		if(this.getGyroFrequency() != null){
			this.setFrequencyRatio();
			if(this.viewAngle != null) this.setRazinCutoff();
		}
		return this;
	}
	
	private void setFrequencyRatio(){
		this.frequencyRatio = this.getPlasmaFrequency() / this.getGyroFrequency();
		this.razinParameter = (3./2.) / this.frequencyRatio;
	}
	
	public Double getLowEnergyElectronISR() {
		return lowEnergyElectronISR;
	}

	public Double getHighEnergyElectronISR() {
		return highEnergyElectronISR;
	}

	public Double getTotalElectronISR() {
		return totalElectronISR;
	}
	
	public Microbunch getMicrobunch(){
		return microbunch;
	}
	
	public Source setMicrobunchSize(Double tb){
		this.microbunch = new Microbunch(tb);
		return this;
	}

	public Source setLowEnergyElectronISR(Double lowEnergyElectronISR) {
		this.lowEnergyElectronISR = lowEnergyElectronISR;
		return this;
	}

	public Source setHighEnergyElectronISR(Double highEnergyElectronISR) {
		this.highEnergyElectronISR = highEnergyElectronISR;
		return this;
	}
	
	public Source setHighEnergyElectronCSR(Double highEnergyElectronCSR) {
		this.microbunch.setHighEnergyElectronCSR(highEnergyElectronCSR);
		return this;
	}
	

	public Source setTotalElectronISR(Double totalElectronISR) {
		this.totalElectronISR = totalElectronISR;
		if(this.volume != null) this.density = this.totalElectronISR / this.volume;
		return this;
	}

	private Source setRazinCutoff(){
		this.razinCutoff = (2./3.) * Math.pow(this.getPlasmaFrequency(), 2) / this.getGyroFrequency() / Math.sin(Math.toRadians(this.viewAngle));
		return this;
	}
	
	public Boolean getUseRazin() {
		return useRazin;
	}

	public void setUseRazin(Boolean useRazin) {
		this.useRazin = useRazin;
	}

	public class FrequencyData {
		
		private Integer frequencyIndex;
		private Double frequencyValue;
		private Double frequencyHarmonic;
		
		private Double normalizedISRlow;
		private Double normalizedISRhigh;
		
		private RefractionPolarization refractionPolarization;
		private StokesAndPolarization stokesAndPolarization; 
		private Radiation radiation;
		
		private Double ordISREmissivityHigh=0., extraordISREmissivityHigh=0.;
		private Double ordISRAbsorptionHigh=0., extraordISRAbsorptionHigh=0.;

		private Double ordISREmissivityLow=0., extraordISREmissivityLow=0.;
		private Double ordISRAbsorptionLow=0., extraordISRAbsorptionLow=0.;
		
		private Double ordCSREmissivity=0., extraordCSREmissivity=0.;
		private Double ordCSRAbsorption=0., extraordCSRAbsorption=0.;
		private Double ordISREmissivity=0., extraordISREmissivity=0.;
		private Double ordISRAbsorption=0., extraordISRAbsorption=0.;
		
		private Double energyTreshould, microbunchTreshould;
		
		private Double cos, sin;

		public FrequencyData(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double energyTreshould, Double microbunchTreshould, Double normalizedISRlow, Double normalizedISRhigh){
			
			this.cos = Math.cos(viewAngle * Constants.DEGREE_TO_RAD);
			this.sin = Math.sin(viewAngle * Constants.DEGREE_TO_RAD);

			this.frequencyIndex = index;
			this.frequencyValue = frequencyValue;
			this.frequencyHarmonic = frequencyValue / gyroFrequency;
			this.refractionPolarization = new RefractionPolarization();
			this.energyTreshould = energyTreshould;
			this.microbunchTreshould = microbunchTreshould;
			
			this.normalizedISRlow = normalizedISRlow;
			this.normalizedISRhigh = normalizedISRhigh;
			

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

					ordISREmissivityLow +=  (radiation.getOrdEmissivityIndex() * Math.pow(em, -energyEspectrum) * de * magneticField * Constants.EMISSIVITY_FACTOR) * normalizedISRlow;
					ordISRAbsorptionLow += (radiation.getOrdEmissivityIndex() * Math.pow(em, -energyEspectrum) * de / frequencyHarmonic / frequencyHarmonic / magneticField * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1)) * normalizedISRlow;
				
					extraordISREmissivityLow += (radiation.getExtraordEmissivityIndex() * Math.pow(em, -energyEspectrum) * de * magneticField * Constants.EMISSIVITY_FACTOR) * normalizedISRlow;
					extraordISRAbsorptionLow += (radiation.getExtraordEmissivityIndex() * Math.pow(em, -energyEspectrum) * de / Math.pow(frequencyHarmonic, 2) / magneticField * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1)) * normalizedISRlow;
				
				} else {
					
					// High Energy Electrons
					
					ordISREmissivityHigh +=  (radiation.getOrdEmissivityIndex() * Math.pow(em, -energyEspectrum) * de * magneticField * Constants.EMISSIVITY_FACTOR) * normalizedISRhigh;
					ordISRAbsorptionHigh += (radiation.getOrdEmissivityIndex() * Math.pow(em, -energyEspectrum) * de / (Math.pow(frequencyHarmonic, 2)) / magneticField * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1)) * normalizedISRhigh;
					extraordISREmissivityHigh += (radiation.getExtraordEmissivityIndex() * Math.pow(em, -energyEspectrum) * de * magneticField * Constants.EMISSIVITY_FACTOR) * normalizedISRhigh;
					extraordISRAbsorptionHigh += (radiation.getExtraordEmissivityIndex() * Math.pow(em, -energyEspectrum) * de / Math.pow(frequencyHarmonic, 2) / magneticField * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1)) * normalizedISRhigh;

					ordCSREmissivity +=  (radiation.getOrdEmissivityIndex() * Math.pow(em, -energyEspectrum) * de * magneticField * Constants.EMISSIVITY_FACTOR) * microbunch.getNormalizedCSRhigh(); 
					ordCSRAbsorption += (radiation.getOrdEmissivityIndex() * Math.pow(em, -energyEspectrum) * de / (Math.pow(frequencyHarmonic, 2)) / magneticField * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1)) *  microbunch.getNormalizedCSRhigh_();
					extraordCSREmissivity += (radiation.getExtraordEmissivityIndex() * Math.pow(em, -energyEspectrum) * de * magneticField * Constants.EMISSIVITY_FACTOR) *  microbunch.getNormalizedCSRhigh();
					extraordCSRAbsorption += (radiation.getExtraordEmissivityIndex() * Math.pow(em, -energyEspectrum) * de / Math.pow(frequencyHarmonic, 2) / magneticField * Constants.ABSORPTION_FACTOR * (energyEspectrum * gamma *(gamma + 1 ) + 2 *Math.pow(gamma, 2) -1) / gamma / (Math.pow(gamma, 2)-1)) * microbunch.getNormalizedCSRhigh_();
					
				}
			}
			
			ordISREmissivityLow = ordISREmissivityLow / volume;
			ordISRAbsorptionLow = ordISRAbsorptionLow / volume;
			ordISREmissivityHigh = ordISREmissivityHigh / volume;
			ordISRAbsorptionHigh = ordISRAbsorptionHigh / volume;
			ordCSREmissivity = ordCSREmissivity / microbunch.getVolume();
			ordCSRAbsorption = ordCSRAbsorption / microbunch.getVolume(); 

			extraordISREmissivityLow = extraordISREmissivityLow / volume;
			extraordISRAbsorptionLow = extraordISRAbsorptionLow / volume;
			extraordISREmissivityHigh = extraordISREmissivityHigh / volume;
			extraordISRAbsorptionHigh = extraordISRAbsorptionHigh / volume;
			extraordCSREmissivity = extraordCSREmissivity / microbunch.getVolume();
			extraordCSRAbsorption = extraordCSRAbsorption / microbunch.getVolume();
			
			ordISREmissivity = ordISREmissivityHigh + ordISREmissivityLow;
			extraordISREmissivity = extraordISREmissivityHigh + extraordISREmissivityLow;
			ordISRAbsorption = ordISRAbsorptionHigh + ordISRAbsorptionLow;
			extraordISRAbsorption = extraordISRAbsorptionHigh + extraordISRAbsorptionLow;
			
			this.stokesAndPolarization = new StokesAndPolarization();

		}

		public Double getNormalizedISRlow() {
			return normalizedISRlow;
		}

		public Double getNormalizedISRhigh() {
			return normalizedISRhigh;
		}
		
		public Double getFrequencyHarmonic(){
			return frequencyHarmonic;
		}

		public Double getFrequencyValue(){
			return frequencyValue;
		}

		public RefractionPolarization getRefractionPolarization(){
			return refractionPolarization;
		}

		protected class RefractionPolarization {

			private Double ordRefractionIndex, extraordRefractionIndex;
			private Double ordPolarizationCoefficient, extraordPolarizationCoefficient;
			
			private RefractionPolarization(){
				
				// Refraction Index
				Double refractionNum = 2 * Math.pow(frequencyRatio,2) * (Math.pow(frequencyRatio,2) - Math.pow(frequencyHarmonic,2));
				Double ordRefractionDnum = +Math.sqrt(Math.pow(frequencyHarmonic,4) * Math.pow(sin,4) + 4 * Math.pow(frequencyHarmonic,2)*Math.pow((Math.pow(frequencyRatio,2)-Math.pow(frequencyHarmonic,2)), 2) * Math.pow(cos,2)) - 2 * Math.pow(frequencyHarmonic,2) * (Math.pow(frequencyRatio,2) - Math.pow(frequencyHarmonic,2)) - Math.pow(frequencyHarmonic, 2) * Math.pow(sin, 2);
				Double extraordRefractionDnum = -Math.sqrt(Math.pow(frequencyHarmonic,4) * Math.pow(sin,4) + 4 * Math.pow(frequencyHarmonic,2)*Math.pow((Math.pow(frequencyRatio,2)-Math.pow(frequencyHarmonic,2)), 2) * Math.pow(cos,2)) - 2 * Math.pow(frequencyHarmonic,2) * (Math.pow(frequencyRatio,2) - Math.pow(frequencyHarmonic,2)) - Math.pow(frequencyHarmonic, 2) * Math.pow(sin, 2);

				this.ordRefractionIndex = 1 + refractionNum / ordRefractionDnum;
				this.extraordRefractionIndex = 1 + refractionNum / extraordRefractionDnum;

				// Polarization Index
				Double polarizationNum = 2 * frequencyHarmonic * (Math.pow(frequencyRatio,2) - Math.pow(frequencyHarmonic,2)) * cos;
				Double ordPolarizationDnum = +Math.sqrt(Math.pow(frequencyHarmonic, 4) * Math.pow(sin,4) + 4 * Math.pow(frequencyHarmonic, 2) * Math.pow((Math.pow(frequencyRatio, 2) - Math.pow(frequencyHarmonic, 2)), 2)* Math.pow(cos, 2)) - Math.pow(frequencyHarmonic,2) *  Math.pow(sin,2);
				Double extraordPolarizationDnum = -Math.sqrt(Math.pow(frequencyHarmonic, 4) * Math.pow(sin,4) + 4 * Math.pow(frequencyHarmonic, 2) * Math.pow((Math.pow(frequencyRatio, 2) - Math.pow(frequencyHarmonic, 2)), 2)* Math.pow(cos, 2)) - Math.pow(frequencyHarmonic,2) *  Math.pow(sin,2);

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
					
					if(frequencyHarmonic > frequencyRatio){
						this.refraction = Math.sqrt(refractionPolarization.getOrdRefractionIndex());
						this.polarization = refractionPolarization.getOrdPolarizationCoefficient();
						this.ordEmissivityIndex = this.GyroSincrotron();
					}
					
					if(getUseRazin()){
						if(frequencyHarmonic > (Math.sqrt(Math.pow(frequencyRatio,2) + 0.25) + 0.5)){
							this.refraction = Math.sqrt(refractionPolarization.getExtraordRefractionIndex());
							this.polarization = refractionPolarization.getExtraordPolarizationCoefficient();
							this.extraordEmissivityIndex = this.GyroSincrotron();
						}
					}
					
				
				} else if(this.gamma >= this.gammaTreshould){
						
					if(getUseRazin()){
						if(frequencyHarmonic > (Math.sqrt(Math.pow(frequencyRatio,2) + 0.25) + 0.5)){
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
				Double ffc = frequencyHarmonic * 2. / 3. / sin / gamma / gamma;
				
				if(ffc >= 20){
					return 0.;
				} else {
					
					Double ordS = (frequencyHarmonic * gamma * (1 - this.refraction * beta * cos) +1);
					Double extraordS = (frequencyHarmonic * gamma * (1 + this.refraction * beta * cos));

					Double sum = 0.;

					for(Double i=Math.floor(ordS); i<=Math.floor(extraordS); i++){
						Double cosPhi = (1 -i/frequencyHarmonic/gamma) / beta / cos / this.refraction;
						Double sinPhi = Math.sqrt(1 -Math.pow(cosPhi, 2));
						Double xs = i * this.refraction * beta * sin * sinPhi / (1 -this.refraction * beta * cos * cosPhi);
						Double xstr;

						if(frequencyHarmonic > 50){
							xstr = 0.8;
							if(gamma > 5) xstr = 0.9;
							if(gamma > 10) xstr = 0.95;
							if(gamma > 15) xstr = 0.96;
							if(xs < (xstr*i)) {
								continue;
							}
						}
						
						if(frequencyHarmonic > 250){
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

					return sum / beta / 2 / cos * frequencyHarmonic / (1 + Math.pow(polarization,2));
				}
			}
			
			protected Double Synchrotron(){
				
				Double f =0.;
				Double ffc = frequencyHarmonic*2 / 3 / sin / gamma / gamma * Math.pow((1 + 9 / 4 * (Math.pow(gamma, 2) -1) / Math.pow(razinParameter, 2) / Math.pow(frequencyHarmonic, 2)), 1.5);
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

		public Double getOrdISREmissivityHigh() {
			return ordISREmissivityHigh;
		}

		public Double getExtraordISREmissivityHigh() {
			return extraordISREmissivityHigh;
		}

		public Double getOrdISRAbsorptionHigh() {
			return ordISRAbsorptionHigh;
		}

		public Double getExtraordISRAbsorptionHigh() {
			return extraordISRAbsorptionHigh;
		}

		public Double getOrdISREmissivityLow() {
			return ordISREmissivityLow;
		}

		public Double getExtraordISREmissivityLow() {
			return extraordISREmissivityLow;
		}

		public Double getOrdISRAbsorptionLow() {
			return ordISRAbsorptionLow;
		}

		public Double getExtraordISRAbsorptionLow() {
			return extraordISRAbsorptionLow;
		}

		public Double getOrdCSREmissivity() {
			return ordCSREmissivity;
		}

		public Double getExtraordCSREmissivity() {
			return extraordCSREmissivity;
		}

		public Double getOrdCSRAbsorption() {
			return ordCSRAbsorption;
		}

		public Double getExtraordCSRAbsorption() {
			return extraordCSRAbsorption;
		}

		public Double getOrdISREmissivity() {
			return ordISREmissivity;
		}

		public Double getExtraordISREmissivity() {
			return extraordISREmissivity;
		}

		public Double getOrdISRAbsorption() {
			return ordISRAbsorption;
		}

		public Double getExtraordISRAbsorption() {
			return extraordISRAbsorption;
		}

		public StokesAndPolarization getStokesAndPolarization(){
			return stokesAndPolarization;
		}
		public class StokesAndPolarization {
			
			protected Double emissivityISRPolarization, emissivityCSRPolarization;
			protected Double phiISROrd, phiISRExtraord, phiISROrdLow, phiISRExtraordLow, phiISROrdHigh, phiISRExtraordHigh;
			protected Double phiCSROrd, phiCSRExtraord;
			protected Double phiISRTotal, phiCSRTotal, phiISRTotalLow, phiISRTotalHigh;
			protected Double QISR,VISR,QCSR,VCSR;
			protected Double fluxPolarizationISR, fluxPolarizationCSR;
			
			protected StokesAndPolarization(){
				
				if(getExtraordISREmissivity() > 0){
					this.emissivityISRPolarization = (getExtraordISREmissivity() - getOrdISREmissivity()) / (getExtraordISREmissivity() + getOrdISREmissivity()); 
				} else {
					this.emissivityISRPolarization = 0.;
				}

				if(getExtraordCSREmissivity() > 0){
					this.emissivityCSRPolarization = (getExtraordCSREmissivity() - getOrdCSREmissivity()) / (getExtraordCSREmissivity() + getOrdCSREmissivity()); 
				} else {
					this.emissivityCSRPolarization = 0.;
				}

				this.phiISROrd = this.Phi(getOrdISREmissivity(), getOrdISRAbsorption(), getHeight(), getVolume(), getOmega());
				//this.phiISROrdLow = this.Phi(getOrdISREmissivityLow(), getOrdISRAbsorptionLow(), getHeight(), getVolume(), getOmega());
				//this.phiISROrdHigh = this.Phi(getOrdISREmissivityHigh(), getOrdISRAbsorptionHigh(), getHeight(), getVolume(), getOmega());
				this.phiISROrdLow = this.Phi(getOrdISREmissivityLow(), getOrdISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISROrdHigh = this.Phi(getOrdISREmissivityHigh(), getOrdISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISRExtraord = this.Phi(getExtraordISREmissivity(), getExtraordISRAbsorption(), getHeight(), getVolume(), getOmega());
				//this.phiISRExtraordLow = this.Phi(getExtraordISREmissivityLow(), getExtraordISRAbsorptionLow(), getHeight(), getVolume(), getOmega());
				//this.phiISRExtraordHigh = this.Phi(getExtraordISREmissivityHigh(), getExtraordISRAbsorptionHigh(), getHeight(), getVolume(), getOmega());
				this.phiISRExtraordLow = this.Phi(getExtraordISREmissivityLow(), getExtraordISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISRExtraordHigh = this.Phi(getExtraordISREmissivityHigh(), getExtraordISRAbsorption(), getHeight(), getVolume(), getOmega());
				
				this.phiCSROrd = this.Phi(getOrdCSREmissivity(), getOrdCSRAbsorption(), microbunch.getHeight(), microbunch.getVolume(), microbunch.getOmega());
				this.phiCSRExtraord = this.Phi(getExtraordCSREmissivity(), getExtraordCSRAbsorption(), microbunch.getHeight(), microbunch.getVolume(), microbunch.getOmega());
				
				this.phiISRTotal = this.phiISROrd + this.phiISRExtraord;
				this.phiISRTotalLow = this.phiISROrdLow + this.phiISRExtraordLow;
				this.phiISRTotalHigh = this.phiISROrdHigh + this.phiISRExtraordHigh;  
				this.phiCSRTotal = this.phiCSROrd + this.phiCSRExtraord;

				this.QISR = this.Q(this.phiISROrd, this.phiISRExtraord);
				this.VISR = this.Q(this.phiISROrd, this.phiISRExtraord);
				this.fluxPolarizationISR = this.FluxPolarization(this.phiISRTotal, this.phiISROrd, this.phiISRExtraord);

				this.QCSR = this.Q(this.phiCSROrd, this.phiCSRExtraord);
				this.VCSR = this.Q(this.phiCSROrd, this.phiCSRExtraord);
				this.fluxPolarizationCSR = this.FluxPolarization(this.phiCSRTotal, this.phiCSROrd, this.phiCSRExtraord);

			}
			
			protected Double Q(Double phiOrd, Double phiExtraord){
				return 	(phiOrd * (1 - Math.pow(getRefractionPolarization().getOrdPolarizationCoefficient(),2)) / (1 + Math.pow(getRefractionPolarization().getOrdPolarizationCoefficient(),2))) +
				  	    (phiExtraord * (1 - Math.pow(getRefractionPolarization().getExtraordPolarizationCoefficient(),2)) / (1 + Math.pow(getRefractionPolarization().getExtraordPolarizationCoefficient(),2)));
				
			}
			
			protected Double V(Double phiOrd, Double phiExtraord){
				return 2 * (phiOrd * getRefractionPolarization().getOrdPolarizationCoefficient() / (1 + Math.pow(getRefractionPolarization().getOrdPolarizationCoefficient(), 2))) + (phiExtraord * getRefractionPolarization().getExtraordPolarizationCoefficient() / (1 + Math.pow(getRefractionPolarization().getExtraordPolarizationCoefficient(), 2)));
			}
			
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
			
			public Double getQISR() {
				return QISR;
			}

			public Double getVISR() {
				return VISR;
			}

			public Double getQCSR() {
				return QCSR;
			}

			public Double getVCSR() {
				return VCSR;
			}

			public Double getFluxPolarizationISR() {
				return fluxPolarizationISR;
			}

			public Double getFluxPolarizationCSR() {
				return fluxPolarizationISR;
			}

			public Double getEmissivityISRPolarization() {
				return emissivityISRPolarization;
			}

			public Double getEmissivityCSRPolarization() {
				return emissivityCSRPolarization;
			}

			public Double getPhiISROrd() {
				return phiISROrd;
			}

			public Double getPhiISRExtraord() {
				return phiISRExtraord;
			}

			public Double getPhiISROrdLow() {
				return phiISROrdLow;
			}

			public Double getPhiISRExtraordLow() {
				return phiISRExtraordLow;
			}

			public Double getPhiISROrdHigh() {
				return phiISROrdHigh;
			}

			public Double getPhiISRExtraordHigh() {
				return phiISRExtraordHigh;
			}

			public Double getPhiCSROrd() {
				return phiCSROrd;
			}

			public Double getPhiCSRExtraord() {
				return phiCSRExtraord;
			}

			public Double getPhiISRTotal() {
				return phiISRTotal;
			}

			public Double getPhiCSRTotal() {
				return phiCSRTotal;
			}

			public Double getPhiISRTotalLow() {
				return phiISRTotalLow;
			}

			public Double getPhiISRTotalHigh() {
				return phiISRTotalHigh;
			}
			
		}

		
	}
	
	public FrequencyData addFrequency(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double gammaTreshould, Double microbunchTreshould,  Double normalizedISRlow, Double normalizedISRhigh){
		FrequencyData frequency = new FrequencyData(index, frequencyValue, energyLower, energuUpper, energyEspectrum, gammaTreshould, microbunchTreshould, normalizedISRlow, normalizedISRhigh);
		this.frequencies.add(frequency);
		return frequency;
	}
	
	public FrequencyData getFrequency(Integer index){
		return this.frequencies.get(index-1);
	}
	

	
	public class Microbunch {
		
		//Setado pelo usuário
		private Double size; // Tamanho da fonte
		private Double height; // Altura da fonte	
		private Double radius;
		private Double area;
		private Double omega;
		private Double volume;
		private Double density;
		
		private Double highEnergyElectronCSR;
		private Double normalizedCSRhigh, normalizedCSRhigh_; 
		
		public Microbunch(Double tb){
			this.size = (Constants.LIGHT_SPEED * Constants.MS_TO_CMS) * tb;
			this.height = (Constants.LIGHT_SPEED * Constants.MS_TO_CMS) * tb;
			this.radius = this.size / 2.;
			this.area = Math.PI * Math.pow(radius, 2);
			this.omega = this.area / Math.pow(Constants.UA,2);
			this.volume = this.area * this.height;
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

		public Double getHighEnergyElectronCSR() {
			return highEnergyElectronCSR;
		}
		
		public Microbunch setHighEnergyElectronCSR(Double highEnergyElectronCSR) {
			this.highEnergyElectronCSR = highEnergyElectronCSR;
			if(this.volume != null) this.density = this.highEnergyElectronCSR / this.volume;
			return this;
		}
		
		public Microbunch setNormalizedCSRhigh(Double normalizedCSRhigh){
			this.normalizedCSRhigh = normalizedCSRhigh;
			return this;
		}
		
		public Microbunch setNormalizedCSRhigh_(Double normalizedCSRhigh_){
			this.normalizedCSRhigh_ = normalizedCSRhigh_;
			return this;
		}

		public Double getNormalizedCSRhigh() {
			return normalizedCSRhigh;
		}

		public Double getNormalizedCSRhigh_() {
			return normalizedCSRhigh_;
		}
		
	}
	
}
