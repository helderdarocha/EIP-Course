package lab3;

import java.util.List;

public class SumAggregator {
	public Double add(List<Double> results) {
		double total = 0.0;
		for (double partialResult : results) {
			total += partialResult;
		}
		return total;
	}
}
