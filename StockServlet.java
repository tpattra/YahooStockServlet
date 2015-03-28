import java.io.*;
import java.text.*;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.Attribute;

public class StockServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	response.setContentType("text/javascript; charset=UTF-8");
		PrintWriter out = response.getWriter();	
    	
		String stocksb = request.getParameter("param");
		String urlString = "http://default-environment-esf6ibbkbs.elasticbeanstalk.com/?company=" + stocksb; 
		
//		String urlString = "http://query.yahooapis.com/v1/public/yql?q=Select%20Name%2C%20Symbol%2C%20LastTradePriceOnly%2C%20"+
//		"Change%2C%20ChangeinPercent%2C%20PreviousClose%2C%20DaysLow%2C%20DaysHigh%2C%20Open%2C%20YearLow%2C%20YearHigh%2C%20Bid"+
//		"%2C%20Ask%2C%20AverageDailyVolume%2C%20OneyrTargetPrice%2C%20MarketCapitalization%2C%20Volume%2C%20Open%2C%20YearLow%20from%20yahoo.finance.quotes%20where%20symbol%3D%22GOOG%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
			
		try { 
			//Retrieve XML data
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(urlStream, "UTF-8"));
			
			//Parse XML data and convert to JSON
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(read);
			Element root = doc.getRootElement();
			
			
			if(root.getAttribute("error") != null) {
				out.println("{\"results\":{\"error\":\"Page Not Found!\"}}");
			}
			else {
						
				String json = "{\n\"result\":{\n";
				//Element company = result;
					String name = root.getChildText("Name");
					String symbol = root.getChildText("Symbol");
					json += "\"Name\":\"" + name + "\",";
					json += "\"Symbol\":\"" + symbol + "\",";
				
				Element quote = (Element) root.getChild("Quote");
				json += "\"Quote\":{\n";
					String changetype = quote.getChildText("ChangeType");
					String change = quote.getChildText("Change");
					String changeinpercent = quote.getChildText("ChangeInPercent");
					String lasttrade = quote.getChildText("LastTradePriceOnly");
					String open = quote.getChildText("Open");
					String yearlow = quote.getChildText("YearLow");
					String yearhigh = quote.getChildText("YearHigh");
					String volume = quote.getChildText("Volume");
					String oneyr = quote.getChildText("OneYearTargetPrice");
					String bid = quote.getChildText("Bid");
					String dayslow = quote.getChildText("DaysLow");
					String dayshigh = quote.getChildText("DaysHigh");
					String ask = quote.getChildText("Ask");		
					String average = quote.getChildText("AverageDailyVolume");
					String previous = quote.getChildText("PreviousClose");
					String market = quote.getChildText("MarketCapitalization");
					json += "\"ChangeType\":\"" + changetype + "\"";
					json += ",\"Change\":\"" + change + "\"";
					json += ",\"ChangeInPercent\":\"" + changeinpercent + "\"";
					json += ",\"LastTradePriceOnly\":\"" + lasttrade + "\"";
					json += ",\"Open\":\"" + open + "\"";
					json += ",\"YearLow\":\"" + yearlow + "\"";
					json += ",\"YearHigh\":\"" + yearhigh + "\"";
					json += ",\"Volume\":\"" + volume + "\"";
					json += ",\"OneYearTargetPrice\":\"" + oneyr + "\"";
					json += ",\"Bid\":\"" + bid + "\"";
					json += ",\"DaysLow\":\"" + dayslow + "\"";
					json += ",\"DaysHigh\":\"" + dayshigh + "\"";
					json += ",\"Ask\":\"" + ask + "\"";
					json += ",\"AverageDailyVolume\":\"" + average + "\"";
					json += ",\"PreviousClose\":\"" + previous + "\"";
					json += ",\"MarketCapitalization\":\"" + market + "\"";
					json += "},\n";
					//end Quote here
					
				Element news = (Element) root.getChild("News");
				json += "\"News\":{\n";
				List items = news.getChildren("Item");
				json += "\"Item\":[\n";
				for (int i=0; items != null && i < items.size(); i++){
					Element item = (Element) items.get(i);
					json += "{\n";
					String link = item.getChildText("Link");
					String title = item.getChildText("Title");
					json += "\"Link\":\"" + link + "\"";
					json += ",\"Title\":\"" + title + "\"";
					json += "}";
			
					if (i < items.size()-1) {
						json += ",";
					}
					json += "\n";
				}
				
				json += "]\n},\n";
				
				//get chart url
				String chartUrl = root.getChildText("StockChartImageURL");
				json += "\"StockChartImageURL\":\"" + chartUrl + "\"\n";
				json += "}\n";
				json += "}\n";
				//end result here 
				
				out.println(json);
			}
		} catch (MalformedURLException e) {
			out.println("{\"results\":{\"error\":\"MalformedURLException!\"}}");
			//pw.println("MalformedURLException " + e.getMessage());
		} catch (IOException e) {
			out.println("{\"results\":{\"error\":\"IOException!\"}}");
			//pw.println("IOException " + e.getMessage());
		} catch (JDOMException e) {
			out.println("{\"results\":{\"error\":\"JDOMException!\"}}");
			//pw.println("JDOMException " + e.getMessage());
		}
    }
}