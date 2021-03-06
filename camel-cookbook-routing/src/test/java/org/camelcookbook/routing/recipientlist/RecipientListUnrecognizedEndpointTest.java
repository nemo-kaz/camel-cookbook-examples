/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.routing.recipientlist;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RecipientListUnrecognizedEndpointTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:first")
    private MockEndpoint first;

    @EndpointInject(uri = "mock:second")
    private MockEndpoint second;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RecipientListUnrecognizedEndpointRouteBuilder();
    }

    @Test
    public void testMessageRoutedToMulticastEndpoints() throws InterruptedException {
        final String messageBody = "Message to be distributed via recipientList";

        first.setExpectedMessageCount(1);
        first.message(0).body().isEqualTo(messageBody);
        second.setExpectedMessageCount(1);
        second.message(0).body().isEqualTo(messageBody);

        template.sendBody(messageBody);

        assertMockEndpointsSatisfied();
    }
}
