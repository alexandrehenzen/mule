/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.module.extension.values;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import org.mule.runtime.api.values.Value;
import org.junit.Test;

import java.util.Set;

public class OperationValuesTestCase extends AbstractValuesTestCase {

  @Override
  protected String getConfigFile() {
    return "values/operation-options.xml";
  }

  @Test
  public void singleOptions() throws Exception {
    Set<Value> channels = getValues("single-values-enabled-parameter", "channels");
    assertThat(channels, hasSize(3));
    assertThat(channels, hasValues("channel1", "channel2", "channel3"));
  }

  @Test
  public void singleOptionsEnabledParameterWithConnection() throws Exception {
    Set<Value> channels = getValues("singleValuesEnabledParameterWithConnection", "channels");
    assertThat(channels, hasSize(3));
    assertThat(channels, hasValues("connection1", "connection2", "connection3"));
  }

  @Test
  public void singleOptionsEnabledParameterWithConfiguration() throws Exception {
    Set<Value> channels = getValues("singleValuesEnabledParameterWithConfiguration", "channels");
    assertThat(channels, hasSize(3));
    assertThat(channels, hasValues("config1", "config2", "config3"));
  }

  @Test
  public void singleOptionsEnabledParameterWithRequiredParameters() throws Exception {
    Set<Value> channels = getValues("singleValuesEnabledParameterWithRequiredParameters", "channels");
    assertThat(channels, hasSize(4));
    assertThat(channels, hasValues("requiredInteger:2", "requiredBoolean:false", "strings:[1, 2]", "requiredString:aString"));
  }

  @Test
  public void singleOptionsEnabledParameterInsideParameterGroup() throws Exception {
    Set<Value> channels = getValues("singleValuesEnabledParameterInsideParameterGroup", "channels");
    assertThat(channels, hasSize(3));
    assertThat(channels, hasValues("channel1", "channel2", "channel3"));
  }

  @Test
  public void singleOptionsEnabledParameterRequiresValuesOfParameterGroup() throws Exception {
    Set<Value> channels = getValues("singleValuesEnabledParameterRequiresValuesOfParameterGroup", "values");
    assertThat(channels, hasSize(1));
    assertThat(channels, hasValues("anyParameter:aParam"));
  }

  @Test
  public void multiLevelOption() throws Exception {
    Set<Value> values = getValues("multiLevelValue", "values");
    Value value = values.iterator().next();
    assertThat(value.getDisplayName(), is("America"));
    assertThat(value.getId(), is("America"));
    assertThat(value.getPartName(), is("continent"));

    Value countryChild = value.getChilds().iterator().next();
    assertThat(countryChild.getDisplayName(), is("Argentina"));
    assertThat(countryChild.getId(), is("Argentina"));
    assertThat(countryChild.getPartName(), is("country"));
  }

  @Test
  public void singleOptionsWithRequiredParameterWithAlias() throws Exception {
    Set<Value> channels = getValues("singleValuesWithRequiredParameterWithAlias", "channels");
    assertThat(channels, hasSize(1));
    assertThat(channels, hasValues("requiredString:dummyValue"));
  }

  @Test
  public void resolverGetsMuleContextInjection() throws Exception {
    Set<Value> values = getValues("resolverGetsMuleContextInjection", "channel");
    assertThat(values, hasSize(1));
    assertThat(values, hasValues("INJECTED!!!"));
  }

  @Test
  public void optionsInsideShowInDslGroup() throws Exception {
    Set<Value> values = getValues("valuesInsideShowInDslGroup", "values");
    assertThat(values, hasSize(1));
    assertThat(values, hasValues("anyParameter:someValue"));
  }
}
