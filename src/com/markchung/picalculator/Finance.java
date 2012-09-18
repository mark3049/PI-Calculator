package com.markchung.picalculator;
/**
 * pv * (1+rate)^nper + pmt *(1+rate*type)*{[(1+rate)^nper]-1}/rate + fv = 0
 */
/**
 * 
	FV		到期後未來值(終值) 	=FV(rate, nper, pmt, [pv], [type])
	PV 		單筆的現值 			=PV(rate, nper, pmt, [fv], [type])
	RATE	每期的利率或報酬率 	=RATE(nper, pmt, pv, [fv], [type], [guess])
	PMT 	每期投資金額 		=PMT(rate, nper, pv, [fv], [type])
	NPER 	期數 				=NPER(rate, pmt, pv, [fv], [type]) 
 */
public class Finance {
	public static double FV(double rate, int nper, double pmt) {
		return FV(rate, nper, pmt, 0);
	}

	public static double FV(double rate, int nper, double pmt, double pv) {
		return FV(rate, nper, pmt, pv, 0);
	}

	public static double FV(double rate, int nper, double pmt, double pv,
			int type) {
		double s = pv * Math.pow(1 + rate, nper);
		double a = pmt * (1 + rate * type) * (Math.pow(1 + rate, nper) - 1)
				/ rate;
		double fv = -s + -a;
		return fv;
	}

	public static double PMT(double rate, int nper, double pv) {
		return PMT(rate, nper, pv, 0);
	}

	public static double PMT(double rate, int nper, double pv, double fv) {
		return PMT(rate, nper, pv, fv, 0);
	}

	public static double PMT(double rate, int nper, double pv, double fv,
			int type) {
		double pmt = 0;
		double a = (1 + rate * type) * (Math.pow(1 + rate, nper) - 1) / rate;
		pmt = (fv + pv * (Math.pow(1 + rate, nper))) / a;
		return -pmt;
	}

	public static double PV(double rate, int nper, double pmt, double fv,
			int type) {
		double a = pmt * (1 + rate * type) * (Math.pow(1 + rate, nper) - 1)
				/ rate;
		double pv = (a + fv) / Math.pow(1 + rate, nper);
		return -pv;
	}

	public static double RATE(int nper, double pmt, double pv) {
		return RATE(nper, pmt, pv, 0);
	}

	public static double RATE(int nper, double pmt, double pv, double fv) {
		return RATE(nper, pmt, pv, fv, 0);
	}

	public static double RATE(int nper, double pmt, double pv, double fv,
			int type) {
		return RATE(nper, pmt, pv, fv, type, 200, 1e-5);
	}

	private static double rate_ev(double rate, int nper, double pmt, double pv,
			double fv, int type) {
		double x = pv * Math.pow(1 + rate, nper);
		x += pmt * (1 + rate * type) * (Math.pow(1 + rate, nper) - 1) / rate;
		x += fv;
		return x;
	}

	public static double RATE(int nper, double pmt, double pv, double fv,
			int type, int maxLoop, double MaxError) {
		double top_rate = 1;
		double top_x = rate_ev(top_rate, nper, pmt, pv, fv, type);
		double bottom_rate = 1e-12;
		double bottom_x = rate_ev(bottom_rate, nper, pmt, pv, fv, type);

		double rate = 0.5;
		double x;
		for (int i = 0; i < maxLoop; ++i) {
			rate = (top_rate + bottom_rate) / 2;
			x = rate_ev(rate, nper, pmt, pv, fv, type);
			if (Math.abs(x) < MaxError) {
				return rate;
			}

			if (top_x > 0 && bottom_x > 0 || top_x < 0 && bottom_x < 0) {
				return Double.NaN;
			} else if (top_x > 0 && bottom_x < 0) {
				if (x > 0) {
					top_rate = rate;
					top_x = x;
				} else {
					bottom_rate = rate;
					bottom_x = x;
				}
			} else { // top_x < 0 && bottom_x > 0
				if (x < 0) {
					top_rate = rate;
					top_x = x;
				} else {
					bottom_rate = rate;
					bottom_x = x;
				}
			}

		}
		return rate;
	}

	public static int NPER(double rate, double pmt, double pv) {
		return NPER(rate, pmt, pv, 0);
	}

	public static int NPER(double rate, double pmt, double pv, double fv) {
		return NPER(rate, pmt, pv, fv, 0);
	}

	public static int NPER(double rate, double pmt, double pv, double fv,
			int type) {
		int nper = 1;
		double x;
		double last_x = Double.MAX_VALUE;
		do {
			x = rate_ev(rate, nper, pmt, pv, fv, type);
			System.out.print(nper);
			System.out.print(" ");
			System.out.println(x);
			if (x < 0 && fv > 0 || fv < 0 && x > 0){
				if(Math.abs(x)>Math.abs(last_x)){
					return nper-1;
				}else{
					return nper;
				}
			}
			++nper;
			last_x = x;
			
		} while (nper < 1000);
		return 0;
	}
}
