package com.craam.wellington.isrcsr;

import com.craam.wellington.statics.Constants;

public class Enviroment {

	// Setado pelo usuário
	private Double magneticField; // Intensidade do campo magnético
	private Double plasmaDensity; // Densidade do Plasma

	//Calculado internamente
	private Double gyroFrequency;
	private Double plasmaFrequency;
	private Double frequencyRatio;
	private Double razinParameter;
	private Double razinCutoff;
	
	private Integer viewAngle;
	
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

	public Enviroment setMagneticField(Double magneticField) {
		this.magneticField = magneticField;
		this.gyroFrequency = 0.5 / Math.PI * (Constants.ELECTRON_CHARGE / (Constants.ELECTRON_MASS * Constants.KG_TO_G)  / (Constants.LIGHT_SPEED * Constants.MS_TO_CMS)) * this.magneticField;
		if(this.getPlasmaFrequency() != null){
			this.setFrequencyRatio();
			if(this.viewAngle != null) this.setRazinCutoff();
		}
		return this;
	}

	public Enviroment setPlasmaDensity(Double plasmaDensity) {
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
	
	private Enviroment setRazinCutoff(){
		this.razinCutoff = (2./3.) * Math.pow(this.getPlasmaFrequency(), 2) / this.getGyroFrequency() / Math.sin(Math.toRadians(this.viewAngle));
		return this;
	}
	
	public Enviroment setViewAngle(int viewAngle){
		this.viewAngle = viewAngle;
		if(this.getPlasmaFrequency() != null && this.getGyroFrequency() != null) this.setRazinCutoff();
		return this;
	}

	
}
