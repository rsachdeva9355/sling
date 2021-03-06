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
package org.apache.sling.crankstart.core.commands;

import org.apache.sling.crankstart.api.CrankstartCommand;
import org.apache.sling.crankstart.api.CrankstartCommandLine;
import org.apache.sling.crankstart.api.CrankstartContext;

/** CrankstartCommand that does nothing */
public class NullCommand implements CrankstartCommand {
    private final String name;
    private final String description;
    
    public NullCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getDescription() {
        return name + ": " + description;
    }
    
    @Override
    public boolean appliesTo(CrankstartCommandLine commandLine) {
        return name.equals(commandLine.getVerb());
    }

    @Override
    public void execute(CrankstartContext crankstartContext, CrankstartCommandLine commandLine) throws Exception {
    }
}
