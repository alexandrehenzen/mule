/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.http.internal.multipart;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mule.module.http.api.HttpHeaders.Names.CONTENT_TYPE;
import static org.mule.module.http.internal.multipart.HttpMultipartEncoder.MANDATORY_TYPE_ERROR_MESSAGE;
import static org.mule.module.http.internal.multipart.HttpMultipartEncoder.createContent;

import org.mule.module.http.internal.domain.MultipartHttpEntity;
import org.mule.tck.junit4.AbstractMuleTestCase;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMultipart;

import org.junit.Before;
import org.junit.Test;

public class HttpMultipartEncoderTestCase extends AbstractMuleTestCase
{

    private static final String FIRST_PART_CONTENT = "first-part-test";

    private static final String SECOND_PART_CONTENT = "second-part-test";

    private static final String THIRD_PART_CONTENT = "second-part-test";

    private final List<HttpPart> httpParts = new ArrayList<>();

    private final MultipartHttpEntity multipartHttpEntity = mock(MultipartHttpEntity.class);

    @Before
    public void setUp() throws Exception
    {
        HttpPart partOne = new HttpPart("firstPart", FIRST_PART_CONTENT.getBytes(), "text/plain", FIRST_PART_CONTENT.getBytes().length);
        partOne.addHeader(CONTENT_TYPE, "text/plain");
        httpParts.add(partOne);

        HttpPart partTwo = new HttpPart("secondPart", SECOND_PART_CONTENT.getBytes(), "text/plain", SECOND_PART_CONTENT.getBytes().length);
        partTwo.addHeader(CONTENT_TYPE, "text/plain");
        httpParts.add(partTwo);

        HttpPart partThree = new HttpPart("thirdPart", THIRD_PART_CONTENT.getBytes(), "text/plain", THIRD_PART_CONTENT.getBytes().length);
        partThree.addHeader(CONTENT_TYPE, "text/plain");
        httpParts.add(partThree);

        when(multipartHttpEntity.getParts()).thenReturn(httpParts);

    }

    @Test
    public void createMultipartRelatedContentWithStartParameter() throws Exception
    {
        MimeMultipart mimeMultipart = createContent(multipartHttpEntity, "multipart/related; type=\"text/plain\"; start=thirdPart");
        assertThat((String) mimeMultipart.getBodyPart(0).getContent(), is(THIRD_PART_CONTENT));
        assertThat((String) mimeMultipart.getBodyPart(1).getContent(), is(FIRST_PART_CONTENT));
        assertThat((String) mimeMultipart.getBodyPart(2).getContent(), is(SECOND_PART_CONTENT));
        assertThat(mimeMultipart.getContentType(), containsString("type=\"text/plain\""));
    }

    @Test
    public void createMultipartRelatedContentWithoutStartParameter() throws Exception
    {
        MimeMultipart mimeMultipart = createContent(multipartHttpEntity, "multipart/related; type=\"text/plain\"; boundary= \"MIMEBoundary\"");
        assertThat((String) mimeMultipart.getBodyPart(0).getContent(), is(FIRST_PART_CONTENT));
        assertThat((String) mimeMultipart.getBodyPart(1).getContent(), is(SECOND_PART_CONTENT));
        assertThat((String) mimeMultipart.getBodyPart(2).getContent(), is(THIRD_PART_CONTENT));
        assertThat(mimeMultipart.getContentType(), containsString("type=\"text/plain\""));
    }

    @Test
    public void createMultipartRelatedContentWithoutMandatoryTypeParameter() throws Exception
    {
        try
        {
            createContent(multipartHttpEntity, "multipart/related");
        }
        catch(Exception e)
        {
            assertThat(e.getMessage(), is(MANDATORY_TYPE_ERROR_MESSAGE));
        }
    }

}