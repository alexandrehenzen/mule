/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.property;

import static java.util.stream.Collectors.toList;
import static org.mule.runtime.api.util.Preconditions.checkNotNull;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.values.Value;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.extension.api.values.ValuesProvider;
import org.mule.runtime.module.extension.internal.runtime.resolver.ParameterValueResolver;
import org.mule.runtime.module.extension.internal.values.ValuesProviderFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Private {@link ModelProperty} which commu
 *
 * @since 4.0
 */
public final class ValuesProviderFactoryModelProperty implements ModelProperty {

  private final Field connectionField;
  private final Field configField;
  private final Class<? extends ValuesProvider> valuesProvider;
  private final List<InjectableParameterInfo> injectableParameters;

  /**
   * @param valuesProvider
   * @param injectableParameters
   * @param connectionField
   * @param configField
   */
  private ValuesProviderFactoryModelProperty(Class<? extends ValuesProvider> valuesProvider,
                                             List<InjectableParameterInfo> injectableParameters,
                                             Field connectionField,
                                             Field configField) {
    checkNotNull(valuesProvider, "Values Provider Class parameter can't be null");
    checkNotNull(injectableParameters, "injectableParameters parameter can't be null");

    this.valuesProvider = valuesProvider;
    this.injectableParameters = injectableParameters;
    this.connectionField = connectionField;
    this.configField = configField;
  }

  public static ValuesProviderFactoryModelPropertyBuilder builder(Class<? extends ValuesProvider> dynamicOptionsResolver) {
    return new ValuesProviderFactoryModelPropertyBuilder(dynamicOptionsResolver);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "MetadataKeyId";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPublic() {
    return false;
  }

  public Class<? extends ValuesProvider> getValueProvider() {
    return valuesProvider;
  }

  /**
   * @return The list of parameter names that are considered as required.
   */
  public List<String> getRequiredParameters() {
    return injectableParameters
        .stream()
        .map(InjectableParameterInfo::getParameterName)
        .collect(toList());
  }

  /**
   * @return The {@link List} of parameters that requires to be inyected into the {@link ValuesProvider}
   */
  public List<InjectableParameterInfo> getInjectableParameters() {
    return injectableParameters;
  }

  /**
   * @return Indicates if the {@link ValuesProvider} requires a connection to resolve the {@link Value values}
   */
  public boolean usesConnection() {
    return connectionField != null;
  }

  /**
   * @return Indicates if the {@link ValuesProvider} requires a configuration to resolve the {@link Value values}
   */
  public boolean usesConfig() {
    return configField != null;
  }

  /**
   * @return The field inside the {@link ValuesProvider} that represents the connection
   */
  public Field getConnectionField() {
    return connectionField;
  }

  /**
   * @return The field inside the {@link ValuesProvider} that represents the configuration
   */
  public Field getConfigField() {
    return configField;
  }

  public ValuesProviderFactory createFactory(ParameterValueResolver parameterValueResolver, Supplier<Object> connectionSupplier,
                                             Supplier<Object> configurationSupplier, MuleContext muleContext) {
    return new ValuesProviderFactory(this, parameterValueResolver, connectionSupplier, configurationSupplier, muleContext);
  }

  /**
   *
   */
  public static class ValuesProviderFactoryModelPropertyBuilder {

    private final Class<? extends ValuesProvider> dynamicOptionsResolver;
    private final List<InjectableParameterInfo> injectableParameters;
    private Field connectionField;
    private Field configField;

    ValuesProviderFactoryModelPropertyBuilder(Class<? extends ValuesProvider> dynamicOptionsResolver) {
      this.dynamicOptionsResolver = dynamicOptionsResolver;
      this.injectableParameters = new ArrayList<>();
    }

    public ValuesProviderFactoryModelPropertyBuilder withInjectableParameter(String name, MetadataType metadataType,
                                                                             boolean isRequired) {
      injectableParameters.add(new InjectableParameterInfo(name, metadataType, isRequired));
      return this;
    }

    public ValuesProviderFactoryModelProperty build() {
      return new ValuesProviderFactoryModelProperty(dynamicOptionsResolver, injectableParameters,
                                                    connectionField, configField);
    }

    public void withConnection(Field connectionField) {
      this.connectionField = connectionField;
    }

    public void withConfig(Field configField) {
      this.configField = configField;
    }
  }

  public static class InjectableParameterInfo {

    String parameterName;
    MetadataType type;
    boolean required;

    InjectableParameterInfo(String parameterName, MetadataType type, boolean required) {
      this.parameterName = parameterName;
      this.type = type;
      this.required = required;
    }

    public String getParameterName() {
      return parameterName;
    }

    public void setParameterName(String parameterName) {
      this.parameterName = parameterName;
    }

    public MetadataType getType() {
      return type;
    }

    public void setType(MetadataType type) {
      this.type = type;
    }

    public boolean isRequired() {
      return required;
    }

    public void setRequired(boolean required) {
      this.required = required;
    }
  }
}
