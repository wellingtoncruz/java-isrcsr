package com.craam.wellington.isrcsr;

import java.util.ArrayList;
import java.util.List;

import com.craam.wellington.interfaces.IAlgorithm;
import com.craam.wellington.interfaces.IInput;
import com.craam.wellington.interfaces.IOutput;
import com.craam.wellington.isrcsr.io.*;

public class ISRCSR implements IAlgorithm {
	
	// Objetos de I/O
	private InputData inputData;
	private IOutput output;
	
	private Double normalizeFactor, lowEnergyElectron, highEnergyElectron;
	private Double CSRElectronNumber;
	private Source compactSource, extendedSource;
	
	private List<Frequency> frequencies = new ArrayList<Frequency>();
	
	public ISRCSR(IInput input, IOutput output){
		
		//Variável de controle de tempo
		long start = System.nanoTime();

		this.inputData = (InputData) input.getInputData();
		this.output = output;
		
		this.compactSource = this.inputData.getCompactSource();
		this.extendedSource = this.inputData.getExtendedSource();
		
		this.normalizeFactor = this.IntegrateEnergy();
		this.LowHighEnergyElectrons();
		
		this.extendedSource.setLowEnergyElectronISR(this.inputData.getElectronFractionISRExtendedSource() * this.lowEnergyElectron);
		this.extendedSource.setHighEnergyElectronISR(this.inputData.getElectronFractionISRExtendedSource() * this.highEnergyElectron);
		this.extendedSource.setTotalElectronISR(this.extendedSource.getLowEnergyElectronISR() + this.extendedSource.getHighEnergyElectronISR());

		this.compactSource.setLowEnergyElectronISR((1.0 - this.inputData.getElectronFractionISRExtendedSource()) * lowEnergyElectron);
		this.compactSource.setHighEnergyElectronISR((1.0 - this.inputData.getElectronFractionISRExtendedSource() - this.inputData.getElectronFractionCSR()) * highEnergyElectron);
		this.compactSource.setTotalElectronISR(this.compactSource.getLowEnergyElectronISR() + this.compactSource.getHighEnergyElectronISR());
		
		this.CSRElectronNumber = this.inputData.getElectronFractionCSR() * this.highEnergyElectron;
		this.compactSource.setHighEnergyElectronCSR(this.CSRElectronNumber);
		this.extendedSource.setHighEnergyElectronCSR(this.CSRElectronNumber);
		
		//Loop em frequências
		for(int k=1; k<=inputData.getHighestFrequency(); k++){
			this.addFrequency(k);
		}

		// Gera arquivos de relatório
		output.generateOutput(this);

		//Variável de controle de tempo
		long finish = System.nanoTime();
		System.out.println((finish-start)/1000000000.0);
	
	}
	
	private Double IntegrateEnergy(){
		Double xnorm = 0.;
		
		for(int i=this.inputData.getEnergyLower(); i<=this.inputData.getEnergyUpper(); i++){
			Double el = Math.pow(10, (0.05 * (i-1)  -2));
			Double eu = Math.pow(10, (0.05 * (i)    -2));
			Double em = Math.pow(10, (0.05 * (i-0.5)-2));
			Double de = eu-el;
			xnorm += Math.pow(em,-this.inputData.getEnergyEspectrum()) * de;
		}
		
		return xnorm;
	}

	private ISRCSR LowHighEnergyElectrons(){
		
		this.lowEnergyElectron = 0.;
		this.highEnergyElectron = 0.;
		
		for(int i=this.inputData.getEnergyLower(); i<=this.inputData.getEnergyUpper(); i++){
			Double el = Math.pow(10, (0.05 * (i-1)  -2));
			Double eu = Math.pow(10, (0.05 * (i)    -2));
			Double em = Math.pow(10, (0.05 * (i-0.5)-2));
			Double de = eu-el;
			if(em < this.inputData.getMicrobunchingTreshould()) this.lowEnergyElectron += (this.inputData.getElectronNumber() / this.normalizeFactor) * Math.pow(em,-this.inputData.getEnergyEspectrum()) * de;
			if(em > this.inputData.getMicrobunchingTreshould()) this.highEnergyElectron += (this.inputData.getElectronNumber() / this.normalizeFactor) * Math.pow(em,-this.inputData.getEnergyEspectrum()) * de;
		}
		return this;
	}
	
	public List<Frequency> getFrequencies(){
		return this.frequencies;
	}
	
	public InputData getInputParams(){
		return inputData;
	}
	
	public Double getLowEnergyElectron() {
		return lowEnergyElectron;
	}

	public Double getHighEnergyElectron() {
		return highEnergyElectron;
	}

	public Double getCSRElectronNumber() {
		return CSRElectronNumber;
	}

	public class Frequency {
		
		private Integer frequencyIndex;
		private Double frequencyValue;
		private Double formFactor;
		
		// Emissividades e Absorções referentes a soma das duas fontes
		private Double ordISREmissivityLow, ordISREmissivityHigh;
		private Double ordISRAbsorptionLow, ordISRAbsorptionHigh;
		private Double extraordISREmissivityLow, extraordISREmissivityHigh;
		private Double extraordISRAbsorptionLow, extraordISRAbsorptionHigh;
		private Double ordCSREmissivity, extraordCSREmissivity;
		private Double ordCSRAbsorption, extraordCSRAbsorption;
		private Double ordISREmissivity, extraordISREmissivity;
		private Double ordISRAbsorption, extraordISRAbsorption;
		
		private Double phiISR, phiCSR;
		private Double phiLow, phiHigh;		
		private Double phiOrdISR, phiExtraordISR, phiOrdCSR, phiExtraordCSR;
		private Double phiOrd, phiExtraord;
		private Double phiTotal;
		
		private Double QISR,VISR,QCSR,VCSR, Q,V;
		private Double fluxPolarizationISR, fluxPolarizationCSR, fluxPolarization; 
		
		private Double emissivityISRPolarization, emissivityCSRPolarization, emissivityPolarization;

		public Frequency(Integer index){

			this.frequencyIndex = index;
			this.setFrequencyValue(Math.pow(10, (0.01*(index-1))) * 1e9);
			this.formFactor = 1.0 / Math.cosh(Math.PI * inputData.getMicrobunchingWidth() * frequencyValue / 2.0);

			compactSource.getMicrobunch()
		    .setNormalizedCSRhigh(((1.0 - this.formFactor) + CSRElectronNumber * this.formFactor) * inputData.getElectronFractionCSR() * inputData.getElectronNumber() / normalizeFactor)
		    .setNormalizedCSRhigh_(inputData.getElectronFractionCSR() * inputData.getElectronNumber() / normalizeFactor);

		    extendedSource.getMicrobunch()
		    .setNormalizedCSRhigh(((1.0 - this.formFactor) + CSRElectronNumber * this.formFactor) * inputData.getElectronFractionCSR() * inputData.getElectronNumber() / normalizeFactor)
		    .setNormalizedCSRhigh_(inputData.getElectronFractionCSR() * inputData.getElectronNumber() / normalizeFactor);

			extendedSource.addFrequency(index, frequencyValue, inputData.getEnergyLower(), inputData.getEnergyUpper(), inputData.getEnergyEspectrum(), inputData.getEnergyTreshould(), inputData.getMicrobunchingTreshould(),
			(inputData.getElectronFractionISRExtendedSource() * inputData.getElectronNumber() / normalizeFactor),
		    (inputData.getElectronFractionISRExtendedSource() * inputData.getElectronNumber() / normalizeFactor));

		    compactSource.addFrequency(index, frequencyValue, inputData.getEnergyLower(), inputData.getEnergyUpper(), inputData.getEnergyEspectrum(), inputData.getEnergyTreshould(), inputData.getMicrobunchingTreshould(),
			((1.0 - inputData.getElectronFractionISRExtendedSource()) * inputData.getElectronNumber() / normalizeFactor),
			(1.0 - inputData.getElectronFractionISRExtendedSource() - inputData.getElectronFractionCSR()) * inputData.getElectronNumber() / normalizeFactor);
		    
		    this.ordISREmissivityLow = compactSource.getFrequency(index).getOrdISREmissivityLow() + extendedSource.getFrequency(index).getOrdISREmissivityLow();
		    this.ordISREmissivityHigh = compactSource.getFrequency(index).getOrdISREmissivityHigh() + extendedSource.getFrequency(index).getOrdISREmissivityHigh();
		    this.ordISRAbsorptionLow = compactSource.getFrequency(index).getOrdISRAbsorptionLow() + extendedSource.getFrequency(index).getOrdISRAbsorptionLow();
		    this.ordISREmissivityHigh = compactSource.getFrequency(index).getOrdISRAbsorptionHigh() + extendedSource.getFrequency(index).getOrdISRAbsorptionHigh();

		    this.extraordISREmissivityLow = compactSource.getFrequency(index).getExtraordISREmissivityLow() + extendedSource.getFrequency(index).getExtraordISREmissivityLow();
		    this.extraordISREmissivityHigh = compactSource.getFrequency(index).getExtraordISREmissivityHigh() + extendedSource.getFrequency(index).getExtraordISREmissivityHigh();
		    this.extraordISRAbsorptionLow = compactSource.getFrequency(index).getExtraordISRAbsorptionLow() + extendedSource.getFrequency(index).getExtraordISRAbsorptionLow();
		    this.extraordISREmissivityHigh = compactSource.getFrequency(index).getExtraordISRAbsorptionHigh() + extendedSource.getFrequency(index).getExtraordISRAbsorptionHigh();		    
		    
		    this.ordCSREmissivity = compactSource.getFrequency(index).getOrdCSREmissivity() + extendedSource.getFrequency(index).getOrdCSREmissivity();
		    this.extraordCSREmissivity = compactSource.getFrequency(index).getExtraordCSREmissivity() + extendedSource.getFrequency(index).getExtraordCSREmissivity();
		    this.ordCSRAbsorption = compactSource.getFrequency(index).getOrdCSRAbsorption() + extendedSource.getFrequency(index).getOrdCSRAbsorption();
		    this.extraordCSRAbsorption = compactSource.getFrequency(index).getExtraordCSRAbsorption() + extendedSource.getFrequency(index).getExtraordCSRAbsorption();

		    this.ordISREmissivity = compactSource.getFrequency(index).getOrdISREmissivity() + extendedSource.getFrequency(index).getOrdISREmissivity();
		    this.extraordISREmissivity = compactSource.getFrequency(index).getExtraordISREmissivity() + extendedSource.getFrequency(index).getExtraordISREmissivity();
		    this.ordISRAbsorption = compactSource.getFrequency(index).getOrdISRAbsorption() + extendedSource.getFrequency(index).getOrdISRAbsorption();
		    this.extraordISRAbsorption = compactSource.getFrequency(index).getExtraordISRAbsorption() + extendedSource.getFrequency(index).getExtraordISRAbsorption();
		    
		    if(this.getExtraordISREmissivity() > 0){
				this.emissivityISRPolarization = (getExtraordISREmissivity() - getOrdISREmissivity()) / (getExtraordISREmissivity() + getOrdISREmissivity()); 
			} else {
				this.emissivityISRPolarization = 0.;
			}

			if(this.getExtraordCSREmissivity() > 0){
				this.emissivityCSRPolarization = (getExtraordCSREmissivity() - getOrdCSREmissivity()) / (getExtraordCSREmissivity() + getOrdCSREmissivity()); 
			} else {
				this.emissivityCSRPolarization = 0.;
			}
			
			if((this.getExtraordISREmissivity() + this.getExtraordCSREmissivity()) > 0){
				this.emissivityPolarization = ((getExtraordISREmissivity() + getExtraordCSREmissivity()) - (getExtraordISREmissivity() + getOrdCSREmissivity())) / ((getExtraordISREmissivity() + getExtraordCSREmissivity()) + (getExtraordISREmissivity() + getOrdCSREmissivity()));
			} else {
				this.emissivityPolarization = 0.;
			}
			
			this.phiISR = compactSource.getFrequency(index).getStokesAndPolarization().getPhiISRTotal() + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiISRTotal();
			this.phiCSR = extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSRTotal();
			
			this.phiLow = compactSource.getFrequency(index).getStokesAndPolarization().getPhiISRTotalLow() + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiISRTotalLow();
			this.phiHigh = compactSource.getFrequency(index).getStokesAndPolarization().getPhiISRTotalHigh() + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiISRTotalHigh() + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSROrd()  + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSRExtraord();
			
			this.phiOrdISR = compactSource.getFrequency(index).getStokesAndPolarization().getPhiISROrd() + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiISROrd();
			this.phiExtraordISR = compactSource.getFrequency(index).getStokesAndPolarization().getPhiISRExtraord() + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiISRExtraord();

			this.phiOrdCSR = extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSROrd();
			this.phiExtraordCSR = extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSRExtraord();

			this.phiOrd = this.phiOrdISR + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSROrd();
			this.phiExtraord = this.phiExtraordISR + extendedSource.getFrequency(index).getStokesAndPolarization().getPhiCSRExtraord();
			
			this.phiTotal = this.phiOrd + this.phiExtraord;
			
			this.QISR = compactSource.getFrequency(index).getStokesAndPolarization().getQISR() + extendedSource.getFrequency(index).getStokesAndPolarization().getQISR();
			this.VISR = compactSource.getFrequency(index).getStokesAndPolarization().getVISR() + extendedSource.getFrequency(index).getStokesAndPolarization().getVISR();
			
			this.QCSR = extendedSource.getFrequency(index).getStokesAndPolarization().getQCSR();
			this.VCSR = extendedSource.getFrequency(index).getStokesAndPolarization().getVCSR();
			
			this.Q = this.QISR + this.QCSR;
			this.V = this.VISR + this.VCSR;
			
			if(this.phiISR > 0){
		    	  this.fluxPolarizationISR = (this.phiExtraordISR -this.phiOrdISR) / phiISR; 
		    } else {
		    	  this.fluxPolarizationISR = 0.;
		    }

			if(this.phiCSR > 0){
		    	  this.fluxPolarizationCSR = (this.phiExtraordCSR -this.phiOrdCSR) / phiCSR; 
		    } else {
		    	  this.fluxPolarizationCSR = 0.;
		    }

			if(this.phiTotal > 0){
		    	  this.fluxPolarization = (this.phiExtraord -this.phiOrd) / phiTotal; 
		    } else {
		    	  this.fluxPolarization = 0.;
		    }			
			
		}

		public Integer getFrequencyIndex() {
			return frequencyIndex;
		}

		public Double getFrequencyValue() {
			return frequencyValue;
		}

		public void setFrequencyValue(Double frequencyValue) {
			this.frequencyValue = frequencyValue;
		}

		public Double getOrdISREmissivityLow() {
			return ordISREmissivityLow;
		}

		public Double getOrdISREmissivityHigh() {
			return ordISREmissivityHigh;
		}

		public Double getOrdISRAbsorptionLow() {
			return ordISRAbsorptionLow;
		}

		public Double getOrdISRAbsorptionHigh() {
			return ordISRAbsorptionHigh;
		}

		public Double getExtraordISREmissivityLow() {
			return extraordISREmissivityLow;
		}

		public Double getExtraordISREmissivityHigh() {
			return extraordISREmissivityHigh;
		}

		public Double getExtraordISRAbsorptionLow() {
			return extraordISRAbsorptionLow;
		}

		public Double getExtraordISRAbsorptionHigh() {
			return extraordISRAbsorptionHigh;
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

		public Double getFormFactor() {
			return formFactor;
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

		public Double getEmissivityISRPolarization() {
			return emissivityISRPolarization;
		}

		public Double getEmissivityCSRPolarization() {
			return emissivityCSRPolarization;
		}

		public Double getEmissivityPolarization() {
			return emissivityPolarization;
		}

		public Double getPhiISR() {
			return phiISR;
		}

		public Double getPhiCSR() {
			return phiCSR;
		}

		public Double getPhiLow() {
			return phiLow;
		}

		public Double getPhiHigh() {
			return phiHigh;
		}

		public Double getPhiOrdISR() {
			return phiOrdISR;
		}

		public Double getPhiExtraordISR() {
			return phiExtraordISR;
		}

		public Double getPhiOrdCSR() {
			return phiOrdCSR;
		}

		public Double getPhiExtraordCSR() {
			return phiExtraordCSR;
		}

		public Double getPhiOrd() {
			return phiOrd;
		}

		public Double getPhiExtraord() {
			return phiExtraord;
		}

		public Double getPhiTotal() {
			return phiTotal;
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

		public Double getQ() {
			return Q;
		}

		public Double getV() {
			return V;
		}

		public Double getFluxPolarizationISR() {
			return fluxPolarizationISR;
		}

		public Double getFluxPolarizationCSR() {
			return fluxPolarizationCSR;
		}

		public Double getFluxPolarization() {
			return fluxPolarization;
		}

	}
	
	public Frequency addFrequency(Integer index){
		Frequency frequency = new Frequency(index);
		this.frequencies.add(frequency);
		return frequency;
	}
	
	public Frequency getFrequency(Integer index){
		return this.frequencies.get(index-1);
	}
	
	
}
