package misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Factory implements Serializable{

	public JSONObject getJSONObject(String path) {
		System.out.println(path);
		InputStream in = getClass().getResourceAsStream(path); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuilder sb = new StringBuilder();
	    try {
			while ((line = br.readLine()) != null) {
			    sb.append(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public abstract String getName();
}
