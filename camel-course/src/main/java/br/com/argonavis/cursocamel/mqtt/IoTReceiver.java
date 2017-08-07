package br.com.argonavis.cursocamel.mqtt;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import br.com.argonavis.cursocamel.Configuration;

public class IoTReceiver {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	from("mqtt:test?subscribeTopicName=/esp8266/temperatura&host=tcp://127.0.0.1:1883")
            	.transform(body().convertToString())
            	.process(new Processor() {
                    @Override
                    public void process(Exchange ex) {
                    	String data = ex.getIn().getBody(String.class);
                    	double number = Double.parseDouble(data) * (3.3/1024)*100;
                        
                        Date now = new Date();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        String date = df.format(now);
                        
                        ex.getIn().setBody("{\"dataHora\":"+date+", \"value\": "+number+"}");
                    }
                }) 
            	.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(new Processor() {
                    @Override
                    public void process(Exchange ex) {
                    	String body = ex.getIn().getBody(String.class);
                        System.out.println("Sending body in: " + body);
                    }
                })
                .end()
                .to("restlet:http://localhost:8080/iot-ws-rest?restletMethod=post&connectionTimeout=1000&socketTimeout=2000")
                .end()
                .process(new Processor() {
                    @Override
                    public void process(Exchange ex) {
                    	System.out.println("The response code is: " + ex.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
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
