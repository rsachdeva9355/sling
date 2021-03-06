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
package org.apache.sling.maven.slingstart;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.sling.provisioning.model.Model;
import org.apache.sling.provisioning.model.ModelUtility;
import org.apache.sling.provisioning.model.io.ModelReader;
import org.apache.sling.provisioning.model.io.ModelWriter;

public abstract class ProjectHelper {

    /** The raw local model. */
    private static final String RAW_MODEL_TXT = Model.class.getName() + "/raw.txt";
    private static final String RAW_MODEL_CACHE = Model.class.getName() + "/raw.cache";

    private static final String EFFECTIVE_MODEL_TXT = Model.class.getName() + "/effective.txt";
    private static final String EFFECTIVE_MODEL_CACHE = Model.class.getName() + "/effective.cache";

    /**
     *
     * @param info The project info
     * @throws IOException If writing fails
     */
    public static void storeProjectInfo(final DependencyLifecycleParticipant.ProjectInfo info)
    throws IOException {
        // we have to serialize as the dependency lifecycle participant uses a different class loader (!)
        final StringWriter w1 = new StringWriter();
        ModelWriter.write(w1, info.localModel);
        info.project.setContextValue(RAW_MODEL_TXT, w1.toString());

        final StringWriter w2 = new StringWriter();
        ModelWriter.write(w2, info.model);
        info.project.setContextValue(EFFECTIVE_MODEL_TXT, w2.toString());
    }

    /**
     * Get the effective model from the project
     * @param project The maven projet
     * @return The effective model
     * @throws MojoExecutionException If reading fails
     */
    public static Model getEffectiveModel(final MavenProject project)
    throws MojoExecutionException {
        Model result = (Model) project.getContextValue(EFFECTIVE_MODEL_CACHE);
        if ( result == null ) {
            try {
                final StringReader r = new StringReader((String)project.getContextValue(EFFECTIVE_MODEL_TXT));
                result = ModelReader.read(r, project.getId());
                result = ModelUtility.getEffectiveModel(result, null);
                project.setContextValue(EFFECTIVE_MODEL_CACHE, result);
            } catch ( final IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
        }
        return result;
    }

    /**
     * Get the raw model from the project
     * @param project The maven projet
     * @return The raw local model
     * @throws MojoExecutionException If reading fails
     */
    public static Model getRawModel(final MavenProject project)
    throws MojoExecutionException {
        Model result = (Model) project.getContextValue(RAW_MODEL_CACHE);
        if ( result == null ) {
            try {
                final StringReader r = new StringReader((String)project.getContextValue(RAW_MODEL_TXT));
                result = ModelReader.read(r, project.getId());
                project.setContextValue(RAW_MODEL_CACHE, result);
            } catch ( final IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
        }
        return result;
    }
}
