package com.craam.wellington.isrcsr;

import com.craam.wellington.statics.Constants;

public class Microbunch {
	
	//Setado pelo usuário
	private Double size; // Tamanho da fonte
	private Double height; // Altura da fonte	
	private Double radius;
	private Double area;
	private Double omega;
	private Double volume;

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

}