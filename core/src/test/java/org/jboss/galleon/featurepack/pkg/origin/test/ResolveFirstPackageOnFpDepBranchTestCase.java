/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.featurepack.pkg.origin.test;

import org.jboss.galleon.ArtifactCoords;
import org.jboss.galleon.ProvisioningDescriptionException;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.ArtifactCoords.Gav;
import org.jboss.galleon.config.FeaturePackConfig;
import org.jboss.galleon.config.ProvisioningConfig;
import org.jboss.galleon.repomanager.FeaturePackRepositoryManager;
import org.jboss.galleon.state.ProvisionedFeaturePack;
import org.jboss.galleon.state.ProvisionedState;
import org.jboss.galleon.test.PmProvisionConfigTestBase;
import org.jboss.galleon.test.util.fs.state.DirState;

/**
 *
 * @author Alexey Loubyansky
 */
public class ResolveFirstPackageOnFpDepBranchTestCase extends PmProvisionConfigTestBase {

    private static final Gav FP1_GAV = ArtifactCoords.newGav("org.pm.test", "fp1", "1.0.0.Final");
    private static final Gav FP2_GAV = ArtifactCoords.newGav("org.pm.test", "fp2", "1.0.0.Final");
    private static final Gav FP3_GAV = ArtifactCoords.newGav("org.pm.test", "fp3", "1.0.0.Final");
    private static final Gav FP4_GAV = ArtifactCoords.newGav("org.pm.test", "fp4", "1.0.0.Final");
    private static final Gav FP5_GAV = ArtifactCoords.newGav("org.pm.test", "fp5", "1.0.0.Final");

    @Override
    protected void setupRepo(FeaturePackRepositoryManager repoManager) throws ProvisioningDescriptionException {
        repoManager.installer()
        .newFeaturePack(FP1_GAV)
            .addDependency(FeaturePackConfig.builder(FP2_GAV).setInheritPackages(false).build())
            .addDependency(FeaturePackConfig.builder(FP3_GAV).setInheritPackages(false).build())
            .addDependency(FeaturePackConfig.builder(FP5_GAV).setInheritPackages(false).build())
            .newPackage("p1", true)
                .addDependency("p2")
                .writeContent("fp1/p1.txt", "p1")
                .getFeaturePack()
            .getInstaller()
        .newFeaturePack(FP2_GAV)
            .newPackage("p1", true)
                .writeContent("fp2/p1.txt", "p1")
                .getFeaturePack()
            .getInstaller()
        .newFeaturePack(FP3_GAV)
            .addDependency(FeaturePackConfig.builder(FP4_GAV).setInheritPackages(false).build())
             .newPackage("p1", true)
                 .writeContent("fp3/p1.txt", "p1")
                 .getFeaturePack()
            .getInstaller()
        .newFeaturePack(FP4_GAV)
            .newPackage("p1", true)
                .writeContent("fp4/p1.txt", "p1")
                .getFeaturePack()
            .newPackage("p2")
                .writeContent("fp4/p2.txt", "p2")
                .getFeaturePack()
           .getInstaller()
       .newFeaturePack(FP5_GAV)
           .newPackage("p1", true)
               .writeContent("fp5/p1.txt", "p1")
               .getFeaturePack()
           .newPackage("p2")
               .writeContent("fp5/p2.txt", "p2")
               .getFeaturePack()
          .getInstaller()
        .install();
    }

    @Override
    protected ProvisioningConfig provisioningConfig() throws ProvisioningException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FP1_GAV)
                .build();
    }

    @Override
    protected ProvisionedState provisionedState() throws ProvisioningException {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(FP4_GAV)
                        .addPackage("p2")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(FP1_GAV)
                        .addPackage("p1")
                        .build())
                .build();
    }

    @Override
    protected DirState provisionedHomeDir() {
        return newDirBuilder()
                .addFile("fp1/p1.txt", "p1")
                .addFile("fp4/p2.txt", "p2")
                .build();
    }
}
