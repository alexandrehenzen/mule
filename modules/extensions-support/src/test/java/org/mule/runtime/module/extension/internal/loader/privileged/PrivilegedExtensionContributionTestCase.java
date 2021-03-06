/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.privileged;

import org.junit.Test;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.module.extension.internal.loader.privileged.extension.PrivilegedExtension;
import org.mule.runtime.module.extension.internal.util.MuleExtensionUtils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.module.extension.internal.loader.privileged.extension.ChangeNameDeclarationEnricher.NEW_NAME;

public class PrivilegedExtensionContributionTestCase {

  @Test
  public void loadPrivilegedExtension() {
    ExtensionModel extension = MuleExtensionUtils.loadExtension(PrivilegedExtension.class);
    assertThat(extension.getName(), is(NEW_NAME));
  }
}
