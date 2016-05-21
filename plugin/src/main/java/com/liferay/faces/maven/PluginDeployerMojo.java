/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;


/**
 * Deploys (copies) a Plugin to the $LIFERAY_HOME/deploy folder.
 *
 * @author  Mika Koivisto
 * @author  Thiago Moreira
 * @goal    deploy
 */

public class PluginDeployerMojo extends AbstractLiferayMojo {

	/**
	 * @parameter  expression="${liferayVersion}"
	 */
	protected String liferayVersion;

	/**
	 * @parameter  expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter  expression="${autoDeployDir}"
	 * @required
	 */
	private File autoDeployDir;

	/**
	 * @parameter  default-value="${project.build.directory}/${project.build.finalName}.war" expression="${warFile}"
	 * @required
	 */
	private File warFile;

	/**
	 * @parameter  default-value="${project.build.directory}/${project.build.finalName}.jar" expression="${jarFile}"
	 * @required
	 */
	private File jarFile;

	/**
	 * @parameter  default-value="${project.build.finalName}.war" expression="${warFileName}"
	 * @required
	 */
	private String warFileName;

	/**
	 * @parameter  default-value="${project.build.finalName}.jar" expression="${jarFileName}"
	 * @required
	 */
	private String jarFileName;

	public void execute() throws MojoExecutionException {

		if (!isLiferayProject()) {
			return;
		}

		if (jarFile != null && jarFile.exists()) {
			System.err.println("HELLO: " + jarFileName);
		} else {
			System.err.println("NOPE: ");
		}

		File sourceFile;
		String sourceFileName;
		String destinationFileName = warFileName;

		if (warFile.exists()) {
			sourceFile = warFile;
			sourceFileName = warFileName;
			destinationFileName = warFileName;
		} else if(jarFile.exists()) {
			sourceFile = jarFile;
			sourceFileName = jarFileName;
			destinationFileName = jarFileName;
		} else {
			getLog().warn("Neither " + warFileName + " nor " + jarFileName + " exist.");
			return;
		}

		destinationFileName = destinationFileName.replaceFirst("-SNAPSHOT", "");
		destinationFileName = destinationFileName.replaceFirst("-(\\d+\\.)*(\\d+).war$", ".war");
		destinationFileName = destinationFileName.replaceFirst("-(\\d+\\.)*(\\d+).jar$", ".jar");
		getLog().info("FAST Deploying " + sourceFileName + " to " + autoDeployDir.getAbsolutePath() + "/" +
				destinationFileName);
		CopyTask.copyFile(sourceFile, autoDeployDir, destinationFileName, null, true, true);
	}

	@Override
	protected boolean isLiferayProject() {
		String packaging = project.getPackaging();

		if (packaging.equals("pom")) {
			getLog().info("Skipping " + project.getArtifactId());

			return false;
		}

		return true;
	}

}
