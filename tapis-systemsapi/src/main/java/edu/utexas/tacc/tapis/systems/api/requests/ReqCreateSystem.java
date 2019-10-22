package edu.utexas.tacc.tapis.systems.api.requests;

public final class ReqCreateSystem
{
  public String name;
  public String description;
  public String owner;
  public String host;
  public boolean available;
  public String bucketName;
  public String rootDir;
  public String jobInputDir;
  public String jobOutputDir;
  public String scratchDir;
  public String workDir;
  public String effectiveUserId;
  public String accessCredential;
  public Protocol protocol;

  class Protocol
  {
    public String accessMechanism;
    public String[] transferMechanisms;
    public int port;
    public boolean useProxy;
    public String proxyHost;
    public int proxyPort;
  }
}
