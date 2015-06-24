package com.craam.wellington.isrcsr;

import java.util.ArrayList;
import java.util.List;

import com.craam.wellington.isrcsr.GenericSource.Frequency;
import com.craam.wellington.statics.Constants;

public class Source extends GenericSource {
	
	protected Double lowEnergyElectronISR;
	protected Double highEnergyElectronISR;
	protected Double totalElectronISR;
	
	protected List<Frequency> frequencies = new ArrayList<Frequency>();
	
	public Source(Enviroment enviroment){
		super(enviroment);
	}
	
	public Source setSize(Double sourceSize) {
		this.size = sourceSize;
		this.radius = this.size * Constants.ARC_TO_CM / 2.;
		this.area = Math.PI * Math.pow(this.radius, 2);
		this.omega = this.area / Math.pow(Constants.UA, 2);
		if(this.height != null) this.setVolume();
		return this;
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
	
	public Enviroment getEnviroment(){
		return enviroment;
	}
	
	public Source setLowEnergyElectronISR(Double lowEnergyElectronISR) {
		this.lowEnergyElectronISR = lowEnergyElectronISR;
		return this;
	}

	public Source setHighEnergyElectronISR(Double highEnergyElectronISR) {
		this.highEnergyElectronISR = highEnergyElectronISR;
		return this;
	}
	
	public Source setTotalElectronISR(Double totalElectronISR) {
		this.totalElectronISR = totalElectronISR;
		if(this.volume != null) this.density = this.totalElectronISR / this.volume;
		return this;
	}


	public void setUseRazin(Boolean useRazin) {
		this.useRazin = useRazin;
	}

	public class Frequency extends GenericSource.Frequency {

		private Stokes stokes;
		private Fluxes fluxes;
		
		private Double electronSliceLow;
		private Double electronSliceHigh;
		
		private Double ordISREmissivityHigh=0., extraordISREmissivityHigh=0.;
		private Double ordISRAbsorptionHigh=0., extraordISRAbsorptionHigh=0.;

		private Double ordISREmissivityLow=0., extraordISREmissivityLow=0.;
		private Double ordISRAbsorptionLow=0., extraordISRAbsorptionLow=0.;
		
		private Double ordISREmissivity=0., extraordISREmissivity=0.;
		private Double ordISRAbsorption=0., extraordISRAbsorption=0.;
		
		public Frequency(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double energyTreshould, Double microbunchTreshould, Double electronSliceLow, Double electronSliceHigh){
			
			super(index, frequencyValue, energyLower, energuUpper, energyEspectrum, energyTreshould, microbunchTreshould);
			
			this.electronSliceLow = electronSliceLow;
			this.electronSliceHigh = electronSliceHigh;

			ordISREmissivityLow = (ordEmissivityLow * electronSliceLow) / volume;
			ordISRAbsorptionLow = (ordAbsorptionLow * electronSliceLow)/ volume;
			
			ordISREmissivityHigh = (ordEmissivityHigh * electronSliceHigh) / volume;
			ordISRAbsorptionHigh = (ordAbsorptionHigh * electronSliceHigh)/ volume;
			
			extraordISREmissivityLow = (extraordEmissivityLow * electronSliceLow) / volume;
			extraordISRAbsorptionLow = (extraordAbsorptionLow * electronSliceLow)/ volume;
			
			extraordISREmissivityHigh = (extraordEmissivityHigh * electronSliceHigh) / volume;
			extraordISRAbsorptionHigh = (extraordAbsorptionHigh * electronSliceHigh) / volume;
			
			ordISREmissivity = ordISREmissivityHigh + ordISREmissivityLow;
			extraordISREmissivity = extraordISREmissivityHigh + extraordISREmissivityLow;
			
			ordISRAbsorption = ordISRAbsorptionHigh + ordISRAbsorptionLow;
			extraordISRAbsorption = extraordISRAbsorptionHigh + extraordISRAbsorptionLow;
			
			this.fluxes = new Fluxes();
			this.stokes = new Stokes(fluxes.getPhiOrd(), fluxes.getPhiExtraord());
		}

		public Double getelectronSliceLow() {
			return electronSliceLow;
		}

		public Double getElectronSliceHigh() {
			return electronSliceHigh;
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

		public Stokes getStokes(){
			return stokes;
		}
		
		public Fluxes getFluxes() {
			return fluxes;
		}

		public class Stokes extends GenericSource.Frequency.Stokes {
			
			protected Double emissivityISRPolarization;
			
			protected Stokes(Double phiOrd, Double phiExtraord){
				super(phiOrd, phiExtraord);
			
				if(getExtraordISREmissivity() > 0){
					this.emissivityISRPolarization = (getExtraordISREmissivity() - getOrdISREmissivity()) / (getExtraordISREmissivity() + getOrdISREmissivity()); 
				} else {
					this.emissivityISRPolarization = 0.;
				}

			}

			public Double getISREmissivityPolarization(){
				return emissivityISRPolarization;
			}
		}
		
		public class Fluxes extends GenericSource.Frequency.Fluxes {
		
			protected Double phiISROrd, phiISRExtraord, phiISROrdLow, phiISRExtraordLow, phiISROrdHigh, phiISRExtraordHigh;
			protected Double phiISRTotal, phiCSRTotal, phiISRTotalLow, phiISRTotalHigh;
			protected Double fluxPolarizationISR, fluxPolarizationCSR;
		
			Fluxes(){
			
				this.phiISROrd = this.Phi(getOrdISREmissivity(), getOrdISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISROrdLow = this.Phi(getOrdISREmissivityLow(), getOrdISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISROrdHigh = this.Phi(getOrdISREmissivityHigh(), getOrdISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISRExtraord = this.Phi(getExtraordISREmissivity(), getExtraordISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISRExtraordLow = this.Phi(getExtraordISREmissivityLow(), getExtraordISRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiISRExtraordHigh = this.Phi(getExtraordISREmissivityHigh(), getExtraordISRAbsorption(), getHeight(), getVolume(), getOmega());
			
				this.phiISRTotal = this.phiISROrd + this.phiISRExtraord;
				this.phiISRTotalLow = this.phiISROrdLow + this.phiISRExtraordLow;
				this.phiISRTotalHigh = this.phiISROrdHigh + this.phiISRExtraordHigh;  
	
				this.fluxPolarizationISR = this.FluxPolarization(this.phiISRTotal, this.phiISROrd, this.phiISRExtraord);
			}
	
			public Double getPhiOrd() {
				return phiISROrd;
			}
	
			public Double getPhiExtraord() {
				return phiISRExtraord;
			}
	
			public Double getPhirdLow() {
				return phiISROrdLow;
			}
	
			public Double getPhiExtraordLow() {
				return phiISRExtraordLow;
			}
	
			public Double getPhiOrdHigh() {
				return phiISROrdHigh;
			}
	
			public Double getPhiExtraordHigh() {
				return phiISRExtraordHigh;
			}
	
			public Double getPhiTotal() {
				return phiISRTotal;
			}
	
			public Double getPhiTotalLow() {
				return phiISRTotalLow;
			}
	
			public Double getPhiTotalHigh() {
				return phiISRTotalHigh;
			}
			
			public Double getFluxPolarization() {
				return fluxPolarizationISR;
			}

		}

	}
	
	public Frequency addFrequency(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double gammaTreshould, Double microbunchTreshould,  Double electronSliceLow, Double electronSliceHigh){
		Frequency frequency = new Frequency(index, frequencyValue, energyLower, energuUpper, energyEspectrum, gammaTreshould, microbunchTreshould, electronSliceLow, electronSliceHigh);
		frequencies.add(frequency);
		return frequency;
	}
	
	public Frequency getFrequency(Integer index){
		return frequencies.get(index-1);
	}

	
}
