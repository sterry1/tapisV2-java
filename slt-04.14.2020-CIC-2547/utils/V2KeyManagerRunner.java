package edu.utexas.tacc.tapis.meta.utils;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class V2KeyManagerRunner {
  public static void main(String[] args) {
    try {
      String baseDir = System.getenv("KEYSTORE_BASEDIR");
      String keystore = System.getenv("KEYSTORE_FILE_NAME");
      String pass = System.getenv("KEYSTORE_PASS");
      V2KeyManager v2KeyManager = new V2KeyManager(baseDir,keystore);
      
      v2KeyManager.load(pass);
      System.out.println();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
