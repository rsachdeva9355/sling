/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.sample.slingshot.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.sample.slingshot.SlingshotConstants;
import org.apache.sling.sample.slingshot.SlingshotUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(methods="POST", resourceTypes=SlingshotConstants.RESOURCETYPE_RATINGS)
public class RatingPostServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ResourceResolverFactory factory;

    @Override
    protected void doPost(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response)
    throws ServletException, IOException {
        final String rating = request.getParameter(SlingshotConstants.PROPERTY_RATING);

        final String userId = request.getRemoteUser();

        logger.debug("New rating from {} : {}", userId, rating);

        // save rating
        ResourceResolver resolver = null;
        try {
            resolver = factory.getAdministrativeResourceResolver(null);

            final Resource reqResource = resolver.getResource(request.getResource().getPath());

            SlingshotUtil.setOwnRating(reqResource.getParent(), userId, Integer.valueOf(rating));
        } catch ( final LoginException le ) {
            throw new ServletException("Unable to login", le);
        } finally {
            if ( resolver != null ) {
                resolver.close();
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(200);

        final PrintWriter pw = response.getWriter();
        pw.print("{ ");
        pw.print(" \"rating\" : ");
        pw.print(String.valueOf(SlingshotUtil.getRating(request.getResource().getParent())));
        pw.println("}");
    }

}
