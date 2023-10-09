package com.frostedmc.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class VPNAPI {

    private String apiKey;

	public VPNAPI(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getAPIKey() {
		return this.apiKey;
	}
	public void setAPIKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public CheckResult checkIPAddress(String ipAddress) throws Exception {
		JsonObject json = readUrl("https://api.frostedmc.com/v1/vpn/" + ipAddress);
        String specificRegion = "";
		String company = "";
		String region = "";
		if((json.get("success") != null && json.get("success").getAsBoolean())) {
            JsonObject response = json.get("response")
                    .getAsJsonObject();
            if(response.get("details") != null) {
                if(response.get("vpn").getAsBoolean()) {
                    JsonObject details = response.get("details")
                            .getAsJsonObject();
                    region = details.get("region").getAsString();
                    company = details.get("company").getAsString();
                    if(details.get("specific-region") != null) {
                        specificRegion = details.get("specific-region").getAsString();
                    }
                }
                return new CheckResult(ipAddress,
                        "Success.",
                        response.get("vpn").getAsBoolean(),
                        company,
                        region,
                        specificRegion);
            } else {
                return new CheckResult(ipAddress,
                        "Details were undefined.",
                        false,
                        company,
                        region,
                        specificRegion);
            }
        } else {
            return new CheckResult(ipAddress,
                    json.get("error").getAsString(),
                    false,
                    company,
                    region,
                    specificRegion);
        }
	}

    private JsonObject readUrl(String urlString) throws Exception {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection)
                    url.openConnection();
            conn.addRequestProperty("X-Internal-Key", this.apiKey);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            return new JsonParser().parse(in).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public class CheckResult {
		
		private String ipAddress;
		private String message;
		private boolean vpn;
		private String company;
		private String region;
		private String specificRegion;

		private CheckResult(String ipAddress, String message, boolean vpn, String company, String region, String specificRegion) {
			this.ipAddress = ipAddress;
			this.message = message;
			this.vpn = vpn;
			this.company = company;
            this.region = region;
			this.specificRegion = specificRegion;
		}

		public String getIPAddress() {
			return this.ipAddress;
		}

		public String getMessage() {
			return this.message;
		}

		public boolean isVPN() {
			return this.vpn;
		}

		public String getCompany() {
			return this.company;
		}

		public String getRegion() {
			return this.region;
		}

		public String getSpecificRegion() {
			return this.specificRegion;
		}
	}
}