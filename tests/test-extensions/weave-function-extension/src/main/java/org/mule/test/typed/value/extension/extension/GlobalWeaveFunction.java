/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.typed.value.extension.extension;

public class GlobalWeaveFunction {

  public Integer globalMultiply(int x, int y) {
    return x * y;
  }

  public String globalEcho(String echo) {
    return echo;
  }

}
