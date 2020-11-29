import java.util.logging.Logger;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.catalyst.advanced.CatalystAdvancedIOHandler;
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import com.zc.component.zcql.ZCQL;


public class CovidTrackerAIO implements CatalystAdvancedIOHandler {
	private static final Logger LOGGER = Logger.getLogger(CovidTrackerAIO.class.getName());
	
	private static String TABLENAME = "CovidTracker";
	private static String CITYCOLUMNNAME = "CityName";
	private static String COUNTCOLUMNNAME = "CovidCount";
	JSONObject responseData = new JSONObject();
	static String GET = "GET";
	static String POST = "POST";
	
	@Override
    public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			//Fetches the endpoint and method to which the call was made
			String url = request.getRequestURI();
			String method = request.getMethod();

			//Get the input count for the city and store in dataset
			if ((url.equals("/covid")) && method.equals(POST)) {
				//Gets the request body and parses it
				ServletInputStream requestBody = request.getInputStream();
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(requestBody, "UTF-8"));

				String cityName = (String) jsonObject.get("city_name");
				
				updateCityCovidCount(cityName);
				responseData.put("message", "Thanks for your input");
			} else if((url.equals("/covid")) && method.equals(GET)) {
				String cityName = request.getParameter("city_name");
				ZCRowObject row = getCovidCountForCity(cityName);
				BigInteger covidCount = new BigInteger(row.get(TABLENAME,COUNTCOLUMNNAME).toString());
				responseData.put("message", "Total Covid Affected people in your city : " + covidCount);
				responseData.put("signal", "positive");
				
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE,"Exception in CovidTrackerAIO",e);
			response.setStatus(500);
		}
		response.setContentType("application/json");
		response.getWriter().write(responseData.toString());
		response.setStatus(200);
	}
	
	public void updateCityCovidCount(String cityName) throws Exception {
		
		ZCRowObject selectRow = getCovidCountForCity(cityName);
		if(null == selectRow) {

			ZCRowObject row = ZCRowObject.getInstance();
			row.set(CITYCOLUMNNAME, cityName);
			row.set(COUNTCOLUMNNAME, 1);
			ZCObject.getInstance().getTableInstance(TABLENAME).insertRow(row);
		} else {
			BigInteger covidCount = new BigInteger(selectRow.get(TABLENAME,COUNTCOLUMNNAME).toString());
//			selectRow.set(COUNTCOLUMNNAME, covidCount.add(BigInteger.valueOf(1)));
			ZCRowObject row = ZCRowObject.getInstance();
			row.set(CITYCOLUMNNAME, cityName);
			row.set(COUNTCOLUMNNAME, covidCount.add(BigInteger.valueOf(1)));
			row.set("ROWID", selectRow.get("ROWID"));
			List rows = new ArrayList();
			rows.add(row);
			ZCObject.getInstance().getTableInstance(TABLENAME).updateRows(rows);
			
		}
			
		
		//String updateQuery = "update "+ TABLENAME + " set "+ COUNTCOLUMNNAME + "=" +  covidCount + " where " + CITYCOLUMNNAME + "=" + cityName;
		
		//ZCQL.getInstance().executeQuery(updateQuery);
	}
	
	public ZCRowObject getCovidCountForCity(String cityName) throws Exception {
		String selectQuery = "select * from " + TABLENAME + " where " + CITYCOLUMNNAME + "=" + cityName;
		ZCRowObject row = null;
		ArrayList<ZCRowObject> resultSet  = ZCQL.getInstance().executeQuery(selectQuery);
		
		if(!resultSet.isEmpty())
			row = resultSet.get(0);
		
		return row;
	}
	
}