package es.iesteis.proyectoud4bugstars;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppSettings {
	private List<String> corsAllowedOrigins;
	private String jwtSecret;

	private String frontendBaseUrl;
	private String azureCsConnString;
	private String azureCsSenderEmail;

	private String azureStorageAccount;
	private String azureStorageKey;
	private String azureStorageContainer;

	public List<String> getCorsAllowedOrigins() {
		return corsAllowedOrigins;
	}

	public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
		this.corsAllowedOrigins = corsAllowedOrigins;
	}

	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}

	public String getAzureCsConnString() {
		return azureCsConnString;
	}

	public void setAzureCsConnString(String azureCsConnString) {
		this.azureCsConnString = azureCsConnString;
	}

	public String getFrontendBaseUrl() {
		return frontendBaseUrl;
	}

	public void setFrontendBaseUrl(String frontendBaseUrl) {
		this.frontendBaseUrl = frontendBaseUrl;
	}

	public String getAzureCsSenderEmail() {
		return azureCsSenderEmail;
	}

	public void setAzureCsSenderEmail(String azureCsSenderEmail) {
		this.azureCsSenderEmail = azureCsSenderEmail;
	}

	public String getAzureStorageAccount() {
		return azureStorageAccount;
	}

	public void setAzureStorageAccount(String azureStorageAccount) {
		this.azureStorageAccount = azureStorageAccount;
	}

	public String getAzureStorageKey() {
		return azureStorageKey;
	}

	public void setAzureStorageKey(String azureStorageKey) {
		this.azureStorageKey = azureStorageKey;
	}

	public String getAzureStorageContainer() {
		return azureStorageContainer;
	}

	public void setAzureStorageContainer(String azureStorageContainer) {
		this.azureStorageContainer = azureStorageContainer;
	}
}
