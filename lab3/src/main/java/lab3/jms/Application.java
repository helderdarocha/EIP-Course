package lab3.jms;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.Queue;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.camel.builder.xml.XPathBuilder;

public class Application {
	
	public static final String DATA_FOLDER = "/Users/helderdarocha/Desktop/NewEIP/CODE/lab3/src/main/resources/dados";

	public static void main(String[] args) throws Exception {
		
		Executor thread = Executors.newFixedThreadPool(1);
		
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		
		Destination inFile = (Destination) ctx.lookup("in-file");
		Destination inXmlFile = (Destination) ctx.lookup("in-xml-file");
		Destination inCsvFile = (Destination) ctx.lookup("in-csv-file");
		Destination inTxtFile = (Destination) ctx.lookup("in-txt-file");
		
		Destination inCsvSplit    = (Destination) ctx.lookup("in-csv-split");
		Destination inCsvLine     = (Destination) ctx.lookup("in-csv-line");
		Destination inCsvMiles    = (Destination) ctx.lookup("in-csv-miles");
		
		Destination deslocamentoXml   = (Destination) ctx.lookup("deslocamento-xml");
		Destination deslocamentoSplit = (Destination) ctx.lookup("deslocamento-split");
		Destination deslocamentoLine  = (Destination) ctx.lookup("deslocamento-line");
		Destination deslocamentoDataLine  = (Destination) ctx.lookup("deslocamento-data-line");
		Destination readingXml        = (Destination) ctx.lookup("reading-xml");
		Destination readingSplit      = (Destination) ctx.lookup("reading-split");
		Destination readingLine       = (Destination) ctx.lookup("reading-line");
		Destination readingDataLine   = (Destination) ctx.lookup("reading-data-line");
		
		Destination inMsg     = (Destination) ctx.lookup("in-msg");
		Destination outTotal  = (Destination) ctx.lookup("out-total");
		Destination invalid   = (Destination) ctx.lookup("invalid");

		// 1) File Inbound Adapter
		System.out.println("Waiting for files... ");
	    FileInboundAdapter fileInboundAdapter = new FileInboundAdapter(new File(DATA_FOLDER), inFile, con);
		
	    // 2) Content-based Router
		Map<String, Destination> outMap = new HashMap<>();
		outMap.put("xml", inXmlFile);
		outMap.put("csv", inCsvFile);
		outMap.put("txt", inTxtFile);
		HeaderBasedRouter fileTypeRouter = new HeaderBasedRouter(inFile, invalid, "FileType", outMap, con);
		
		// 3) TXT processing
		// 3.1) Transformer/Enricher/Normalizer: add headers + convert body to string
		TextNormalizer textMessageNormalizer = new TextNormalizer(inTxtFile, inMsg, con);
		
		// 4) CSV processing
		// 4.1) Splitter
		LineSplitter csvLineSplitter = new LineSplitter(inCsvFile, inCsvSplit, con);

		// 4.2) Message Filter (discard message with contents)
		CSVDiscardLineFilter csvDiscardLineFilter = new CSVDiscardLineFilter(inCsvSplit, inCsvLine, "vehicle,date,miles", con);

		// 4.3) Transformer/Header Content Enricher/Body Content Filter
		CSVHeaderEnricher csvHeaderEnricher = new CSVHeaderEnricher(inCsvLine, inCsvMiles, con);

		// 4.4) Transformer (convert miles to km)
		KmToMilesTransformer kmToMilesTransformer = new KmToMilesTransformer(inCsvMiles, inMsg, con);
		
		// 5) XML Processing
		// 5.1) XPath Content-Based Router
		Map<String, Destination> xmlOutMap = new HashMap<>();
		xmlOutMap.put("deslocamentos", deslocamentoXml);
		xmlOutMap.put("vehicle", readingXml);
		XPathRouter xpathRouter = new XPathRouter(inXmlFile, invalid, "local-name(/*)", xmlOutMap, con);

		// 5.2) XML deslocamentos - processing
		// 5.2.1) Transformer / Header Enricher (deslocamentos)
		Map<String, String> headers1 = new HashMap<>();
		headers1.put("Placa", "/deslocamentos/@id");
		headers1.put("Group", "'all'");
		XMLHeaderEnricher xmlHeaderEnricher1 = new XMLHeaderEnricher(deslocamentoXml, deslocamentoSplit, headers1, con);

		// 5.2.2) XPath Splitter
		XPathSplitter xmlSplitter1 = new XPathSplitter(deslocamentoSplit, deslocamentoLine, "//deslocamento", con);

		// 5.2.3) Transformer / Header Content Enricher
		Map<String, String> headers2 = new HashMap<>();
		headers2.put("Data", "/deslocamento/@data");
		XMLHeaderEnricher xmlHeaderEnricher2 = new XMLHeaderEnricher(deslocamentoLine, deslocamentoDataLine, headers2, con);

		// 5.2.4) Transformer / Body Content Filter
		XPathContentFilter xpathContentFilter1 = new XPathContentFilter(deslocamentoDataLine, inMsg, "/deslocamento/text()", con);
		

		// 5.3) XML vehicle - processing
		// 5.3.1) Transformer / Header Enricher (deslocamentos)
		Map<String, String> headers3 = new HashMap<>();
		headers3.put("Placa", "/vehicle/@id");
		headers3.put("Group", "'all'");
		XMLHeaderEnricher xmlHeaderEnricher3 = new XMLHeaderEnricher(readingXml, readingSplit, headers3, con);

		// 5.3.2) XPath Splitter
		XPathSplitter xmlSplitter2 = new XPathSplitter(readingSplit, readingLine, "//reading", con);

		// 5.3.3) Transformer / Header Content Enricher
		Map<String, String> headers4 = new HashMap<>();
		headers4.put("Data", "/reading/@date");
		XMLHeaderEnricher xmlHeaderEnricher4 = new XMLHeaderEnricher(readingLine, readingDataLine, headers4, con);

		// 5.3.4) Transformer / Body Content Filter
		XPathContentFilter xpathContentFilter2 = new XPathContentFilter(readingDataLine, inMsg, "/reading/@after - /reading/@before", con);
		
		// 6) Aggregator
		SumAggregator sumAggregator = new SumAggregator(inMsg, outTotal, "Group = 'all'", con);
		
		// 7) Result consumer
		ResultConsumer result = new ResultConsumer(outTotal, con);
		
		// 7) Route setup
		result.init();
		sumAggregator.init();
		xpathContentFilter2.init();
		xpathContentFilter1.init();
		xmlHeaderEnricher4.init();
		xmlHeaderEnricher2.init();
		xmlSplitter2.init();
		xmlSplitter1.init();
		xmlHeaderEnricher3.init();
		xmlHeaderEnricher1.init();
		xpathRouter.init();
		kmToMilesTransformer.init();
		csvHeaderEnricher.init();
		csvDiscardLineFilter.init();
		csvLineSplitter.init();
		textMessageNormalizer.init();
		fileTypeRouter.init();
		fileInboundAdapter.run(thread); // start last
		con.start();
		
	}

}
