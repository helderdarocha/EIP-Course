package br.com.argonavis.cursocamel.mqtt;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HttpTest {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	from("mqtt:test?subscribeTopicName=/esp8266/temperatura&host=tcp://127.0.0.1:1883")
            	.to("http://localhost:8080/iot-ws-rest/restapi/temperatura?httpClient.soTimeout=10&httpClient.connectionManagerTimeout=20")
            	.process(new Processor() {
                    @Override
                    public void process(Exchange ex) {
                    	System.out.println("The response code is: " + ex.getOut());
                    }
                });
            }
		});
		context.start();

        System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(30000);
        context.stop();
		
	}

}
