package es.iesteis.proyectoud4bugstars.files;

import java.io.IOException;

public interface Filer {
	String PROFILES_DIR = "profiles/";

	String getProfilePhotoUrl(String filename);
	void saveProfilePhoto(String filename, byte[] file) throws IOException;
}
