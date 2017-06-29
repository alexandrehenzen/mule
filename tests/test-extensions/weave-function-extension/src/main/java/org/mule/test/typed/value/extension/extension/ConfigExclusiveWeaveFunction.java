/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.typed.value.extension.extension;

import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Optional;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.mockito.Mockito;

public class ConfigExclusiveWeaveFunction {

  public Integer multiply(int x, int y) {
    return x * y;
  }

  public String echo(String echo) {
    return echo;
  }

  public SimplePojo spy(SimplePojo pojo) {
    return Mockito.spy(pojo);
  }

  public String wihtConfig(@Config FunctionsConfig config, String filepath) {
    return filepath + config.getStringTypedValue().getValue();
  }

  public Object xpath(@Config FunctionsConfig config, String expression,
                      @Optional(defaultValue = "#[payload]") Object item,
                      @Optional String returnType) {
    try {
      if (returnType == null) {
        return config.getxPath().evaluate(expression, item);
      } else {
        return config.getxPath().evaluate(expression, item, asQname(returnType));
      }
    } catch (XPathExpressionException e) {
      throw new MuleRuntimeException(e);
    }
  }

  private QName asQname(String name) {
    return new QName("http://www.w3.org/1999/XSL/Transform", name.toUpperCase());
  }

}
