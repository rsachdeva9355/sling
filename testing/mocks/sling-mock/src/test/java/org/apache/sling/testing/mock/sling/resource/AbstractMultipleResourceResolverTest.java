/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.testing.mock.sling.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.apache.sling.testing.mock.sling.MockSling;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import com.google.common.collect.ImmutableMap;

/**
 * Tests content access accross multiple resource resolvers.
 */
public abstract class AbstractMultipleResourceResolverTest {

    private final BundleContext bundleContext = MockOsgi.newBundleContext();
    protected abstract ResourceResolverType getResourceResolverType();

    protected ResourceResolverFactory newResourceResolerFactory() {
        return MockSling.newResourceResolverFactory(getResourceResolverType(), bundleContext);
    }
    
    @Test
    public void testMultipleResourceResolver() throws Exception {
        ResourceResolverFactory factory = newResourceResolerFactory();
        ResourceResolver resolver1 = factory.getResourceResolver(ImmutableMap.<String, Object>of(
                ResourceResolverFactory.USER, "user1"));
        ResourceResolver resolver2 = factory.getResourceResolver(ImmutableMap.<String, Object>of(
                ResourceResolverFactory.USER, "user2"));
        
        // validate user names
        assertEquals("user1", resolver1.getAttribute(ResourceResolverFactory.USER));
        assertEquals("user2", resolver2.getAttribute(ResourceResolverFactory.USER));
        
        // add a resource in resolver 1
        Resource root = resolver1.getResource("/");
        resolver1.create(root, "test", ImmutableMap.<String, Object>of());
        resolver1.commit();
        
        // try to get resource in resolver 2
        Resource testResource2 = resolver2.getResource("/test");
        assertNotNull(testResource2);
        
        // delete resource and make sure it is removed in resolver 1 as well
        resolver2.delete(testResource2);
        resolver2.commit();
        
        assertNull(resolver1.getResource("/test"));
    }

}
