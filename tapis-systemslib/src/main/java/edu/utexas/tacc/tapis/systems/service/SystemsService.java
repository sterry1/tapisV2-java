package edu.utexas.tacc.tapis.systems.service;

import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.systems.model.Capability;
import edu.utexas.tacc.tapis.systems.model.Credential;
import edu.utexas.tacc.tapis.systems.model.Protocol.AccessMethod;
import edu.utexas.tacc.tapis.systems.model.TSystem;

import java.util.List;

/*
 * Interface for Systems Service
 */
public interface SystemsService
{
  int createSystem(TSystem system, String apiUserId, String scrubbedJson) throws TapisException, IllegalStateException, IllegalArgumentException;

  int deleteSystemByName(String tenantName, String systemName) throws TapisException;

  boolean checkForSystemByName(String tenantName, String systemName) throws TapisException;

  TSystem getSystemByName(String tenantName, String systemName, String apiUserId, boolean getCreds, AccessMethod accessMethod) throws TapisException;

  List<TSystem> getSystems(String tenantName, String apiUserId) throws TapisException;

  List<String> getSystemNames(String tenantName) throws TapisException;

  String getSystemOwner(String tenantName, String systemName) throws TapisException;

  void grantUserPermissions(String tenantName, String systemName, String userName, List<String> permissions) throws TapisException;

  void revokeUserPermissions(String tenantName, String systemName, String userName, List<String> permissions) throws TapisException;

  List<String> getUserPermissions(String tenantName, String systemName, String userName) throws TapisException;

  void createUserCredential(String tenantName, String systemName, String userName, Credential credential) throws TapisException;

  void deleteUserCredential(String tenantName, String systemName, String userName) throws TapisException;

  Credential getUserCredential(String tenantName, String systemName, String userName, AccessMethod accessMethod) throws TapisException;
}
