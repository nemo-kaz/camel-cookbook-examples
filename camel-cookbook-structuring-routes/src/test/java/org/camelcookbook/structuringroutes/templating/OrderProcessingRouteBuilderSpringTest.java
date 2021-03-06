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

package org.camelcookbook.structuringroutes.templating;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OrderProcessingRouteBuilderSpringTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/templating-context.xml");
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testRoutingLogic() throws Exception {
        context.getRouteDefinition("ukOrders")
            .adviceWith(context, new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    replaceFromWith("direct:uk");

                    interceptSendToEndpoint("file:///orders/out/*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:out");
                }
            });

        context.getRouteDefinition("usOrders")
            .adviceWith(context, new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    replaceFromWith("direct:us");

                    interceptSendToEndpoint("file:///orders/out/*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:out");
                }
            });

        context.getRouteDefinition("jamaicaOrders")
            .adviceWith(context, new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    replaceFromWith("direct:jamaica");

                    interceptSendToEndpoint("file:///orders/out/*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:out");
                }
            });

        context.start();

        MockEndpoint out = getMockEndpoint("mock:out");
        out.setExpectedMessageCount(1);
        out.message(0).body().startsWith("2013-11-23");
        out.message(0).header("CamelFileName").isEqualTo("2013-11-23.csv");

        template.sendBody("direct:uk", "23-11-2013,1,Geology rocks t-shirt");

        out.assertIsSatisfied();

        out.reset();
        out.setExpectedMessageCount(1);
        out.message(0).body().startsWith("2013-11-23");
        out.message(0).header("CamelFileName").isEqualTo("2013-11-23.csv");

        template.sendBody("direct:us", "11-23-2013,1,Geology rocks t-shirt");

        out.assertIsSatisfied();

        out.reset();
        out.setExpectedMessageCount(1);
        out.message(0).body().startsWith("2013-11-23");
        out.message(0).header("CamelFileName").isEqualTo("2013-11-23.csv");

        template.sendBody("direct:jamaica", "23-11-2013,1,Geology rocks t-shirt");

        out.assertIsSatisfied();

        out.reset();
        out.setExpectedMessageCount(1);
        out.message(0).body().startsWith("2013-11-23");
        out.message(0).header("CamelFileName").isEqualTo("2013-11-23.csv");

        template.sendBody("direct:uk", "11-23-2013,1,Geology rocks t-shirt");

        out.assertIsNotSatisfied();
    }
}
