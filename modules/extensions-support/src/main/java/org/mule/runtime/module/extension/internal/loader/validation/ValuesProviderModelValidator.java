/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.validation;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.mule.runtime.extension.api.util.ExtensionMetadataTypeUtils.getType;
import static org.mule.runtime.module.extension.internal.util.IntrospectionUtils.isInstantiable;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.model.ComponentModel;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.meta.model.config.ConfigurationModel;
import org.mule.runtime.api.meta.model.connection.ConnectionProviderModel;
import org.mule.runtime.api.meta.model.operation.OperationModel;
import org.mule.runtime.api.meta.model.parameter.ParameterModel;
import org.mule.runtime.api.meta.model.parameter.ParameterizedModel;
import org.mule.runtime.api.meta.model.parameter.ValuesProviderModel;
import org.mule.runtime.api.meta.model.source.SourceModel;
import org.mule.runtime.api.meta.model.util.IdempotentExtensionWalker;
import org.mule.runtime.extension.api.loader.ExtensionModelValidator;
import org.mule.runtime.extension.api.loader.Problem;
import org.mule.runtime.extension.api.loader.ProblemsReporter;
import org.mule.runtime.extension.api.values.ValuesProvider;
import org.mule.runtime.extension.api.util.NameUtils;
import org.mule.runtime.module.extension.internal.loader.java.property.ValuesProviderFactoryModelProperty;
import org.mule.runtime.module.extension.internal.loader.java.property.ValuesProviderFactoryModelProperty.InjectableParameterInfo;
import org.mule.runtime.module.extension.internal.util.IntrospectionUtils;

import java.util.Map;

/**
 * {@link ExtensionModelValidator} for the correct usage of {@link ValuesProviderModel} and
 * {@link ValuesProviderFactoryModelProperty}
 *
 * @since 4.0
 */
public final class ValuesProviderModelValidator implements ExtensionModelValidator {

  @Override
  public void validate(ExtensionModel model, ProblemsReporter problemsReporter) {
    new IdempotentExtensionWalker() {

      @Override
      protected void onConfiguration(ConfigurationModel model) {
        validateModel(model, problemsReporter, false);
      }

      @Override
      protected void onConnectionProvider(ConnectionProviderModel model) {
        validateModel(model, problemsReporter, false);
      }

      @Override
      protected void onSource(SourceModel model) {
        validateModel(model, problemsReporter, true);
      }

      @Override
      protected void onOperation(OperationModel model) {
        validateModel(model, problemsReporter, true);
      }
    }.walk(model);
  }

  private void validateModel(ParameterizedModel model, ProblemsReporter problemsReporter, boolean supportsConnectionsAndConfigs) {
    model.getAllParameterModels()
        .forEach(param -> param
            .getModelProperty(ValuesProviderFactoryModelProperty.class)
            .ifPresent(modelProperty -> validateOptionsResolver(modelProperty, model, problemsReporter,
                                                                supportsConnectionsAndConfigs)));
  }

  private void validateOptionsResolver(ValuesProviderFactoryModelProperty modelProperty, ParameterizedModel model,
                                       ProblemsReporter problemsReporter, boolean supportsConnectionsAndConfigs) {
    Class<? extends ValuesProvider> valueProvider = modelProperty.getValueProvider();
    String providerName = valueProvider.getSimpleName();
    if (!isInstantiable(valueProvider)) {
      problemsReporter.addError(new Problem(model, format("The Value Provider [%s] is not instantiable but it should",
                                                          providerName)));
    }

    Map<String, MetadataType> allParameters =
        model.getAllParameterModels().stream().collect(toMap(IntrospectionUtils::getRealName, ParameterModel::getType));
    String modelName = NameUtils.getModelName(model);
    String modelTypeName = NameUtils.getComponentModelTypeName(model);

    for (InjectableParameterInfo parameterInfo : modelProperty.getInjectableParameters()) {

      if (!allParameters.containsKey(parameterInfo.getParameterName())) {
        problemsReporter.addError(new Problem(model,
                                              format("The Value Provider [%s] declares a parameter '%s' which doesn't exist in the %s '%s'",
                                                     providerName, parameterInfo.getParameterName(), modelTypeName, modelName)));
      } else {
        MetadataType metadataType = allParameters.get(parameterInfo.getParameterName());
        Class<?> expectedType = getType(metadataType)
            .orElseThrow(() -> new IllegalStateException(format("Unable to get Class for parameter: %s",
                                                                parameterInfo.getParameterName())));
        Class<?> gotType = getType(parameterInfo.getType())
            .orElseThrow(() -> new IllegalStateException(format("Unable to get Class for parameter: %s",
                                                                parameterInfo.getParameterName())));

        if (!expectedType.equals(gotType)) {
          problemsReporter.addError(new Problem(model,
                                                format("The Value Provider [%s] defines a parameter '%s' of type '%s' but in the %s '%s' is of type '%s'",
                                                       providerName, parameterInfo.getParameterName(), gotType, modelTypeName,
                                                       modelName, expectedType)));
        }
      }
    }

    if (supportsConnectionsAndConfigs && modelProperty.usesConnection() && model instanceof ComponentModel) {
      boolean requiresConnection = ((ComponentModel) model).requiresConnection();
      if (requiresConnection != modelProperty.usesConnection()) {
        problemsReporter.addError(new Problem(model,
                                              format("The Value Provider [%s] defines that requires a connection, but is used in the %s '%s' which is connection less",
                                                     providerName, modelTypeName, modelName)));
      }
    }

    if (!supportsConnectionsAndConfigs) {
      if (modelProperty.usesConnection()) {
        problemsReporter.addError(new Problem(model,
                                              format("The Value Provider [%s] defines that requires a connection which is not allowed for a Value Provider of a %s's parameter [%s]",
                                                     providerName, modelTypeName, modelName)));
      }

      if (modelProperty.usesConfig()) {
        problemsReporter.addError(new Problem(model,
                                              format("The Value Provider [%s] defines that requires a configuration which is not allowed for a Value Provider of a %s's parameter [%s]",
                                                     providerName, modelTypeName, modelName)));
      }
    }
  }
}
