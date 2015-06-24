package com.craam.wellington.isrcsr;

import java.util.ArrayList;
import java.util.List;

import com.craam.wellington.isrcsr.Source.Frequency;
import com.craam.wellington.statics.Constants;

public class Microbunch extends GenericSource {
	
	private Double highEnergyElectronCSR;
	protected List<Frequency> frequencies = new ArrayList<Frequency>();


	public Microbunch(Enviroment enviroment){
		super(enviroment);
	}
	
	public Microbunch setSize(Double tb){
		this.size = (Constants.LIGHT_SPEED * Constants.MS_TO_CMS) * tb;
		this.height = (Constants.LIGHT_SPEED * Constants.MS_TO_CMS) * tb;
		this.radius = this.size / 2.;
		this.area = Math.PI * Math.pow(radius, 2);
		this.omega = this.area / Math.pow(Constants.UA,2);
		this.volume = this.area * this.height;
		return this;
	}

	public Enviroment getEnviroment(){
		return enviroment;
	}


	public Double getHighEnergyElectronCSR() {
		return highEnergyElectronCSR;
	}

	public Microbunch setHighEnergyElectronCSR(Double highEnergyElectronCSR) {
		this.highEnergyElectronCSR = highEnergyElectronCSR;
		if(this.volume != null) this.density = this.highEnergyElectronCSR / this.volume;
		return this;
	}
	
	public Boolean getUseRazin() {
		return useRazin;
	}
	
	public class Frequency extends GenericSource.Frequency {

		private Stokes stokes;
		private Fluxes fluxes;
		
		private Double electronSliceEmisivity, electronSliceAbsorption;
		
		private Double ordCSREmissivity=0., extraordCSREmissivity=0.;
		private Double ordCSRAbsorption=0., extraordCSRAbsorption=0.;
		
		public Frequency(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double energyTreshould, Double microbunchTreshould, Double electronSliceHighEmissivity, Double electronSliceHighAbsorption){
			
			super(index, frequencyValue, energyLower, energuUpper, energyEspectrum, energyTreshould, microbunchTreshould);
			
			this.electronSliceEmisivity = electronSliceHighEmissivity;
			this.electronSliceAbsorption = electronSliceHighAbsorption;

			ordCSREmissivity = (ordEmissivityHigh * electronSliceEmisivity) / volume;
			ordCSRAbsorption = (ordAbsorptionHigh * electronSliceAbsorption)/ volume;
			
			extraordCSREmissivity = (extraordEmissivityHigh * electronSliceEmisivity) / volume;
			extraordCSRAbsorption = (extraordAbsorptionHigh * electronSliceAbsorption)/ volume;
			
			this.fluxes = new Fluxes();
			this.stokes = new Stokes(fluxes.getPhiOrd(), fluxes.getPhiExtraord());
		}

		public Double getelectronSliceEmissivity() {
			return electronSliceEmisivity;
		}

		public Double getElectronSliceAbsorption() {
			return electronSliceAbsorption;
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

		public Stokes getStokes(){
			return stokes;
		}
		
		public Fluxes getFluxes() {
			return fluxes;
		}

		public class Stokes extends GenericSource.Frequency.Stokes {
			
			protected Double emissivityCSRPolarization;
			
			protected Stokes(Double phiOrd, Double phiExtraord){
				super(phiOrd, phiExtraord);
			
				if(getExtraordCSREmissivity() > 0){
					this.emissivityCSRPolarization = (getExtraordCSREmissivity() - getOrdCSREmissivity()) / (getExtraordCSREmissivity() + getOrdCSREmissivity()); 
				} else {
					this.emissivityCSRPolarization = 0.;
				}

			}

			public Double getCSREmissivityPolarization(){
				return emissivityCSRPolarization;
			}
		}
		
		public class Fluxes extends GenericSource.Frequency.Fluxes {
		
			protected Double phiCSROrd, phiCSRExtraord, phiCSRTotal;
			protected Double fluxPolarizationCSR;
		
			Fluxes(){
			
				this.phiCSROrd = this.Phi(getOrdCSREmissivity(), getOrdCSRAbsorption(), getHeight(), getVolume(), getOmega());
				this.phiCSRExtraord = this.Phi(getExtraordCSREmissivity(), getExtraordCSRAbsorption(), getHeight(), getVolume(), getOmega());
			
				this.phiCSRTotal = this.phiCSROrd + this.phiCSRExtraord;
				this.fluxPolarizationCSR = this.FluxPolarization(this.phiCSRTotal, this.phiCSROrd, this.phiCSRExtraord);
			}
	
			public Double getPhiOrd() {
				return phiCSROrd;
			}
	
			public Double getPhiExtraord() {
				return phiCSRExtraord;
			}
	
			public Double getPhiCSRTotal() {
				return phiCSRTotal;
			}
	
			public Double getFluxPolarization() {
				return fluxPolarizationCSR;
			}

		}
	
	}

	public Frequency addFrequency(Integer index, Double frequencyValue, int energyLower, int energuUpper, Double energyEspectrum, Double gammaTreshould, Double microbunchTreshould,  Double electronSliceEmissivity, Double electronSliceAbsorption){
		Frequency frequency = new Frequency(index, frequencyValue, energyLower, energuUpper, energyEspectrum, gammaTreshould, microbunchTreshould, electronSliceEmissivity, electronSliceAbsorption);
		frequencies.add(frequency);
		return frequency;
	}
	
	public Frequency getFrequency(Integer index){
		return (Frequency)frequencies.get(index-1);
	}
	
}
