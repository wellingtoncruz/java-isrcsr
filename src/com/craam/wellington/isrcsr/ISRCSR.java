package com.craam.wellington.isrcsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craam.wellington.interfaces.IInput;
import com.craam.wellington.interfaces.IOutput;
import com.craam.wellington.isrcsr.io.*;
import com.craam.wellington.statics.Constants;
import com.craam.wellington.utils.*;

public class ISRCSR {
	
	// Objetos de I/O
	InputData inputData;
	IOutput output;
	
	Double minElectronEnergy, maxElectronEnergy;
	Double emissivityFactor, absorptionFactor;
	Source compactSource, extendedSource;
	Microbunch microbunch;
	
	
	public ISRCSR(IInput input, IOutput output){
		//Variável de controle de tempo
		long start = System.nanoTime();

		this.inputData = (InputData) input.getInputData();
		this.output = output;
		
		this.compactSource = this.inputData.getCompactSource();
		this.extendedSource = this.inputData.getExtendedSource();
		this.microbunch = this.inputData.getMicrobunch();
		
		this.minElectronEnergy = Math.pow(10, (0.05*(inputData.getEnergyLower()-1))-2);
		this.maxElectronEnergy = Math.pow(10, (0.05*inputData.getEnergyUpper()-2));
		
		//Variável de controle de tempo
		long finish = System.nanoTime();
		System.out.println((finish-start)/1000000000.0);
		System.out.println(Constants.EMISSIVITY_FACTOR);
		System.out.println(Constants.ABSORPTION_FACTOR);
		System.out.println((finish-start)/1000000000.0);
	}
	
}
