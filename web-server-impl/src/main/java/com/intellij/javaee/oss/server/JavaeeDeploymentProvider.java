/*
 * Copyright (c) 2004 - 2009 by Fuhrer Engineering AG, CH-2504 Biel/Bienne, Switzerland
 */

package com.intellij.javaee.oss.server;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import com.intellij.javaee.artifact.JavaeeArtifactUtil;
import com.intellij.javaee.deployment.DeploymentMethod;
import com.intellij.javaee.deployment.DeploymentModel;
import com.intellij.javaee.deployment.DeploymentProviderEx;
import com.intellij.javaee.serverInstances.J2EEServerInstance;
import com.intellij.javaee.web.artifact.WebArtifactUtil;
import com.intellij.openapi.project.Project;
import com.intellij.packaging.artifacts.ArtifactType;

public class JavaeeDeploymentProvider extends DeploymentProviderEx
{

	private static final DeploymentMethod[] METHODS = {};

	private final boolean deployArchivesOnly;

	public JavaeeDeploymentProvider()
	{
		this(true);
	}

	public JavaeeDeploymentProvider(boolean deployArchivesOnly)
	{
		this.deployArchivesOnly = deployArchivesOnly;
	}

	public DeploymentMethod[] getAvailableMethods()
	{
		return METHODS;
	}

	public void doDeploy(Project project, J2EEServerInstance instance, DeploymentModel deployment)
	{
		((JavaeeServerInstance) instance).deploy(deployment);
	}

	public void startUndeploy(J2EEServerInstance instance, DeploymentModel deployment)
	{
		((JavaeeServerInstance) instance).undeploy(deployment);
	}

	public void updateDeploymentStatus(J2EEServerInstance instance, DeploymentModel deployment)
	{
		((JavaeeServerInstance) instance).updateDeploymentStatus(deployment);
	}

	@Override
	public Collection<? extends ArtifactType> getSupportedArtifactTypes()
	{
		if(deployArchivesOnly)
		{
			ArtifactType ear = JavaeeArtifactUtil.getInstance().getEarArtifactType();
			ArtifactType jar = JavaeeArtifactUtil.getInstance().getEjbJarArtifactType();
			ArtifactType war = WebArtifactUtil.getInstance().getWarArtifactType();
			return Arrays.asList(ear, jar, war);
		}
		else
		{
			return super.getSupportedArtifactTypes();
		}
	}

	@Nullable
	@NonNls
	public String getHelpId()
	{
		return null;
	}
}
