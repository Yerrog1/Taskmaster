package es.iesteis.proyectoud4bugstars.files;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;
import es.iesteis.proyectoud4bugstars.AppSettings;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;

@Component
public class AzureStorageFiler implements Filer {
	private final BlobServiceClient blobServiceClient;
	private final AppSettings appSettings;

	private static final OffsetDateTime EXPIRY_OFFSET = OffsetDateTime.now().plusMinutes(30);

	public AzureStorageFiler(AppSettings appSettings) {
		this.appSettings = appSettings;
		var token = new StorageSharedKeyCredential(
			appSettings.getAzureStorageAccount(),
			appSettings.getAzureStorageKey()
		);

		this.blobServiceClient = new BlobServiceClientBuilder()
			.credential(token)
			.endpoint("https://" + appSettings.getAzureStorageAccount() + ".blob.core.windows.net")
			.buildClient();
	}

	@Override
	public String getProfilePhotoUrl(String filename) {
		var cli = blobServiceClient
			.getBlobContainerClient(appSettings.getAzureStorageContainer())
			.getBlobClient(PROFILES_DIR + filename);

		if (!cli.exists()) return null;

		BlobServiceSasSignatureValues sas = new BlobServiceSasSignatureValues(
			EXPIRY_OFFSET,
			BlobSasPermission.parse("r")
		);

		return cli.getBlobUrl() + "?" + cli.generateSas(sas);
	}

	@Override
	public void saveProfilePhoto(String filename, byte[] file) {
		var cli = blobServiceClient
			.getBlobContainerClient("taskmaster-prod")
			.getBlobClient(PROFILES_DIR + filename);
		
		cli.deleteIfExists();
		cli.upload(new ByteArrayInputStream(file), file.length);
	}
}
