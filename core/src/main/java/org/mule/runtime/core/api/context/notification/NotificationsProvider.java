/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.api.context.notification;

import org.mule.runtime.core.api.util.Pair;

import java.util.Map;

/**
 * Plugins may implement this interface and register that implementation in the bootstrap to declare the notification types it
 * supports.
 *
 * @since 4.0
 */
public interface NotificationsProvider {

  /**
   * The key of the returned map is the string representation of the notification. It must be in the format
   * {@code [plugin]:[NOTIFICATION]}
   * 
   * @return the mapping of the notification id to the concrete types of {@link ServerNotification} and
   *         {@link ServerNotificationListener} it represents.
   */
  Map<String, Pair<Class<? extends ServerNotification>, Class<? extends ServerNotificationListener>>> getEventListenerMapping();
}
