/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2013, 6WIND S.A. All rights reserved.                 *
 *                                                                     *
 * This file is part of the Jenkins Lockable Resources Plugin and is   *
 * published under the MIT license.                                    *
 *                                                                     *
 * See the "LICENSE.txt" file for more information.                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package org.jenkins.plugins.lockableresources.queue;

import hudson.EnvVars;
import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Queue;
import java.util.HashMap;
import java.util.List;

import org.jenkins.plugins.lockableresources.RequiredResourcesProperty;

public class Utils {

	public static AbstractProject<?, ?> getProject(Queue.Item item) {
		if (item.task instanceof AbstractProject)
			return (AbstractProject<?, ?>) item.task;
		return null;
	}

	public static AbstractProject<?, ?> getProject(AbstractBuild<?, ?> build) {
		Object p = build.getParent();
		if (p instanceof AbstractProject)
			return (AbstractProject<?, ?>) p;
		return null;
	}

        /**
         * Create environment based on item parameters
         * 
         * @param item
         * @return 
         */
	public static EnvVars getEnvVars(Queue.Item item) {
            HashMap<String, String> params = new HashMap<>();
            List<ParametersAction> paramsActions = item.getActions(ParametersAction.class);
            for(ParametersAction pa: paramsActions) {
                if(pa != null) {
                    List<ParameterValue> paramsValues = pa.getParameters();
                    for(ParameterValue pv: paramsValues) {
                        if(pv != null) {
                            params.put(pv.getName(), pv.getValue().toString());
                        }
                    }
                }
            }
            return new EnvVars(params);
        }
        
	public static LockableResourcesStruct requiredResources(
			AbstractProject<?, ?> project, EnvVars env) {
		if (project instanceof MatrixConfiguration) {
			env.putAll(((MatrixConfiguration) project).getCombination());
			project = (AbstractProject<?, ?>) project.getParent();
		}

		RequiredResourcesProperty property = project.getProperty(RequiredResourcesProperty.class);
		if (property != null)
			return new LockableResourcesStruct(property, env);

		return null;
	}
}
