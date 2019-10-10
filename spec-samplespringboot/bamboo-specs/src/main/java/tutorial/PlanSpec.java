package tutorial;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;

/**
 * Plan configuration for Bamboo.
 *
 * @see <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">Bamboo Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run 'main' to publish your plan.
     */
    public Plan plan() {
        final Plan plan = new Plan(new Project()
                .oid(new BambooOid("bfq2ax08dp1e"))
                .key(new BambooKey("SAM"))
                .name("Sample-SpringBoot-Spec")
                .description("Creating a Sample Spring Boot build plan for spec"),
                "SpringBoot Build",
                new BambooKey("SB"))
                .oid(new BambooOid("bfgd3bn0k26h"))
                .description("This is the Plan where testing can be done")
                .pluginConfigurations(new ConcurrentBuilds()
                        .useSystemWideDefault(false))
                .stages(new Stage("Default Stage")
                        .jobs(new Job("CI JOB",
                                new BambooKey("JOB1"))
                                .description("This Job will be doing the CI part")
                                .tasks(new VcsCheckoutTask()
                                                .description("Checkout Default Repository")
                                                .checkoutItems(new CheckoutItem().defaultRepository()),
                                        new MavenTask()
                                                .description("Executing Maven Task")
                                                .goal("clean test")
                                                .jdk("JDK 1.8")
                                                .executableLabel("Maven 3")
                                                .hasTests(true),
                                        new MavenTask()
                                                .description("This task will be doing the deployment to Jfrog artifactory")
                                                .goal("clean deploy")
                                                .jdk("JDK 1.8")
                                                .executableLabel("Artifactory Maven")
                                                .hasTests(true))))
                .linkedRepositories("Sample-SpringBoot")

                .triggers(new BitbucketServerTrigger())
                .planBranchManagement(new PlanBranchManagement()
                        .delete(new BranchCleanup())
                        .notificationForCommitters());
        return plan;
    }

    public PlanPermissions planPermission() {
        final PlanPermissions planPermission = new PlanPermissions(new PlanIdentifier("SAM", "SB"))
                .permissions(new Permissions()
                        .userPermissions("user3", PermissionType.EDIT, PermissionType.VIEW, PermissionType.ADMIN, PermissionType.CLONE, PermissionType.BUILD));
        return planPermission;
    }

    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://localhost:8085");
        final PlanSpec planSpec = new PlanSpec();

        final Plan plan = planSpec.plan();
        bambooServer.publish(plan);

        final PlanPermissions planPermission = planSpec.planPermission();
        bambooServer.publish(planPermission);
    }
}
