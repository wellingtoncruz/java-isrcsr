package com.craam.wellington.isrcsr;

import com.craam.wellington.statics.Constants;

public class Source {
	
	//Setado pelo usuário
	private Double size; // Tamanho da fonte
	private Double height; // Altura da fonte	
	private Double magneticField; // Intensidade do campo magnético
	private Double plasmaDensity; // Densidade do Plasma

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
	
	private Integer viewAngle; 

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
	
	private void setRazinCutoff(){
		this.razinCutoff = (2./3.) * Math.pow(this.getPlasmaFrequency(), 2) / this.getGyroFrequency() / Math.sin(Math.toRadians(this.viewAngle));
	}
	
}
