package com.intellij.javaee.deployment;

import java.util.List;

/**
 * @author nik
 */
public interface DeploymentView {

  List<DeploymentModel> getSelectedModels();
}
