package lab3.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Application {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {
			public void configure() {
				// 1) File Channel Inbound Adapter + Content-based router
				from("file:/Users/helderdarocha/Desktop/NewEIP/CODE/lab3/src/main/resources/dados?noop=true")
				.choice()
				   .when(header("CamelFileNameOnly").endsWith(".txt")).to("direct:in-txt-file")
				   .when(header("CamelFileNameOnly").endsWith(".xml")).to("direct:in-xml-file")
				   .when(header("CamelFileNameOnly").endsWith(".csv")).to("direct:in-csv-file")
				   .otherwise().to("direct:invalid").stop()
				.end();

				// 2) TXT processing
				// 2.1) Transformer/Enricher/Normalizer: add headers + convert body to string
				from("direct:in-txt-file").process(e -> {
					e.getIn().setHeader("Placa", e.getIn().getHeader("CamelFileNameOnly", String.class).split("_")[0]);
					e.getIn().setHeader("Data", e.getIn().getHeader("CamelFileNameOnly", String.class).split("_")[1].split("\\.")[0]);
					e.getIn().setHeader("Group", "all");
					e.getIn().setBody(e.getIn().getBody(String.class));
				}).to("direct:in-msg"); // TXT normalizado
				
				

				// 3) CSV processing
				// 3.1) Splitter
				from("direct:in-csv-file")
				.split(body(String.class).tokenize("\n"))
				.to("direct:in-csv-split");

				// 3.2) Message Filter (discard message with contents)
				from("direct:in-csv-split")
				.filter(body(String.class).isNotEqualTo("vehicle,date,miles"))
				.to("direct:in-csv-line");

				// 3.3) Transformer/Header Content Enricher/Body Content Filter
				from("direct:in-csv-line").process(e -> {
					e.getIn().setHeader("Placa", e.getIn().getBody(String.class).split(",")[0]);
					e.getIn().setHeader("Data", e.getIn().getBody(String.class).split(",")[1]);
					e.getIn().setHeader("Group", "all");
					e.getIn().setBody(e.getIn().getBody(String.class).split(",")[2]);
				}).to("direct:in-csv-miles");

				// 3.4) Transformer (convert miles to km)
				from("direct:in-csv-miles")
				.process(e -> e.getIn().setBody(e.getIn().getBody(Double.class) * 1.609))
				.to("direct:in-msg"); // CSV normalizado
				
				

				// 4) XML Processing
				// 4.1) Transformer - convert File to String
				from("direct:in-xml-file")
				.process(e -> e.getIn().setBody(e.getIn().getBody(String.class)))
				.to("direct:in-xml");

				// 4.2) XPath Content-Based Router
				from("direct:in-xml")
				.choice()
				   .when().xpath("/deslocamentos").to("direct:deslocamento-xml")
				   .when().xpath("/vehicle").to("direct:reading-xml")
				   .otherwise().to("direct:invalid").stop()
				.end();

				// 4.3) XML deslocamentos - processint
				// 4.3.1) Transformer / Header Enricher (deslocamentos)
				from("direct:deslocamento-xml")
				.process(e -> {
					e.getIn().setHeader("Placa", XPathBuilder.xpath("/deslocamentos/@id").evaluate(e, String.class));
					e.getIn().setHeader("Group", "all");
				}).to("direct:deslocamento-split");

				// 4.3.2) XPath Splitter
				from("direct:deslocamento-split")
				.split(xpath("//deslocamento"))
				.to("direct:deslocamento-line");

				// 4.3.3) Transformer / Header Content Enricher / Body Content Filter
				from("direct:deslocamento-line")
				.process(e -> {
					e.getIn().setHeader("Data", XPathBuilder.xpath("/deslocamento/@data").evaluate(e, String.class));
					e.getIn().setBody(XPathBuilder.xpath("/deslocamento/text()").evaluate(e, String.class));
				}).to("direct:in-msg"); // XML deslocamento - normalizado
				
				
				// 4.4) XML vehicle - processing
				// 4.4.1) Transformer / Header Enricher (vehicle)
				from("direct:reading-xml")
				.process(e -> {
					e.getIn().setHeader("Placa", XPathBuilder.xpath("/vehicle/@id").evaluate(e, String.class));
					e.getIn().setHeader("Group", "all");
				}).to("direct:reading-split");

				// 4.4.2) XPath Splitter
				from("direct:reading-split")
				.split(xpath("/vehicle/reading"))
				.to("direct:reading-line");

				// 4.4.3) Transformer (converts odometer readings into distance)
				from("direct:reading-line")
				.process(e -> {
					e.getIn().setHeader("Data", XPathBuilder.xpath("/reading/@date").evaluate(e, String.class));
					String after = XPathBuilder.xpath("/reading/@after").evaluate(e, String.class);
					String before = XPathBuilder.xpath("/reading/@before").evaluate(e, String.class);
					e.getIn().setBody(Double.valueOf(after) - Double.valueOf(before));
				}).to("direct:in-msg"); // XML - vehicle - normalizado

				// 5) Invalid Channel log
				from("direct:invalid").process(e -> System.out.println("Invalid: " + e.getIn().getHeaders()));

				// 6) Normalized messages outputs
				from("direct:in-msg")
						.process(e -> System.out.println("Data: " 
				                + e.getIn().getHeader("Data") + ", Placa: "
								+ e.getIn().getHeader("Placa") + ", Payload:" 
				                + e.getIn().getBody()))
						.to("direct:total");

				// 7) Aggregators
				// 7.1) Aggregator - total sum
				from("direct:total").aggregate(header("Group"), new SumAggregator()).completionTimeout(5000)
				.to("direct:out-total");

				// 7.2 Output
				from("direct:out-total").process(e -> System.out.println("Resultado: " + e.getIn().getBody()));

			}
		});

		context.start();
		System.out.println("O servidor está no ar por 20 segundos.");
		Thread.sleep(20000);
		context.stop();

	}

}
