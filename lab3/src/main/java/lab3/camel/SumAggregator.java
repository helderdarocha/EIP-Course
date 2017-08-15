package lab3.camel;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class SumAggregator implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange e1, Exchange e2) {
		if (e1 == null) {
            return e2;
        }
		Double a = Double.valueOf(e1.getIn().getBody(String.class));
		Double b = Double.valueOf(e2.getIn().getBody(String.class));
		e1.getIn().setBody(a+b);
		return e1;
	}

}
