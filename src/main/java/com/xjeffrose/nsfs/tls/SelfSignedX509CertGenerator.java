package com.xjeffrose.nsfs.tls;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

//import java.util.logging.Logger;

@Slf4j
final class SelfSignedX509CertGenerator {
//  private static final Logger log = Log.getLogger(SelfSignedX509CertGenerator.class.getName());

  static final Date NOT_BEFORE = new Date(System.currentTimeMillis() - 86400000L * 365);
  static final Date NOT_AFTER = new Date(253402300799000L);

  SelfSignedX509CertGenerator() {
  }

  public static X509Certificate generate(String fqdn)
      throws IOException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

    //Generate an RSA key pair.
    final KeyPair keypair;
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(2048, null);
    keypair = keyGen.generateKeyPair();

    PrivateKey key = keypair.getPrivate();

    // Prepare the information required for generating an X.509 certificate.
    X509CertInfo info = new X509CertInfo();
    X500Name owner = null;

    owner = new X500Name("CN=" + fqdn);

    info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
    info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new BigInteger(64, new SecureRandom())));
    try {
      info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
    } catch (CertificateException ignore) {
      info.set(X509CertInfo.SUBJECT, owner);
    }
    try {
      info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
    } catch (CertificateException ignore) {
      info.set(X509CertInfo.ISSUER, owner);
    }
    info.set(X509CertInfo.VALIDITY, new CertificateValidity(NOT_BEFORE, NOT_AFTER));
    info.set(X509CertInfo.KEY, new CertificateX509Key(keypair.getPublic()));
    info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha256WithRSAEncryption_oid)));

    // Sign the cert to identify the algorithm that's used.
    X509CertImpl cert = new X509CertImpl(info);
    cert.sign(key, "SHA256withRSA");

    // Update the algorithm and sign again.
    info.set(CertificateAlgorithmId.NAME + '.' + CertificateAlgorithmId.ALGORITHM, cert.get(X509CertImpl.SIG_ALG));
    cert = new X509CertImpl(info);
    cert.sign(key, "SHA256withRSA");
    cert.verify(keypair.getPublic());

    return new X509Certificate(fqdn, key, cert);
  }
}
