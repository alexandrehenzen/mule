/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.capability.xml;

import static org.mule.runtime.api.connection.ConnectionValidationResult.success;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;

/**
 * Provider Documentation
 */
public class TestDocumentedProvider implements ConnectionProvider<String> {

  @Override
  public String connect() throws ConnectionException {
    return "Magic Connection";
  }

  @Override
  public void disconnect(String connection) {

  }

  @Override
  public ConnectionValidationResult validate(String connection) {
    return success();
  }
}
