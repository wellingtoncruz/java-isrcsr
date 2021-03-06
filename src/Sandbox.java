import com.craam.wellington.interfaces.IInput;
import com.craam.wellington.interfaces.IOutput;
import com.craam.wellington.isrcsr.ISRCSR;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;

import com.craam.wellington.statics.Constants;
import com.craam.wellington.synclab.SyncLab;
import com.craam.wellington.synclab.io.CSVOutput;
import com.craam.wellington.synclab.io.InputData;
import com.craam.wellington.synclab.io.ManualInput;
import com.craam.wellington.utils.*;

public class Sandbox {

	public static void main(String[] args) {
		
			
			IInput<com.craam.wellington.isrcsr.io.InputData> isrcsrInput = new com.craam.wellington.isrcsr.io.ManualInput(new HashMap<String, Double>(){{
														put("ex", 2.5);
														put("ntotal", 1e35);
														put("bmagco", 1e3);
														put("bmagex", 1e3);
														put("angle", 45.);
														put("scsize", 0.5);
														put("scheight", 1e6);
														put("sesize", 20.);
														put("seheight", 1e9);
														put("j1", 15.);
														put("j2", 80.);
														put("etr", 2.5);
														put("npco", 9.72e-10);
														put("npex", 9.72e-10);
														put("ecsr", 5.);
														put("xnisrex", 0.999);
														put("xncsr", 5e-15);
														put("tb", 70.0e-12);
														put("kf",701.);														
														}});
			IOutput isrcsrOutput = new com.craam.wellington.isrcsr.io.CSVOutput("isrcsr.out");
			ISRCSR isrcsr = new ISRCSR(isrcsrInput, isrcsrOutput);
	}

}
