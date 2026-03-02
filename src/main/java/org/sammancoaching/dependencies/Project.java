package org.sammancoaching.dependencies;

import static org.sammancoaching.dependencies.TestStatus.NO_TESTS;
import static org.sammancoaching.dependencies.TestStatus.PASSING_TESTS;

public class Project {
	private final boolean deploysSuccessfully;
	private final TestStatus testStatus;
	private final boolean deploysSuccessfullyToStaging;
	private final TestStatus smokeTestStatus;
	
	public static ProjectBuilder builder() {
        return new ProjectBuilder();
    }

    Project(boolean deploysSuccessfully, TestStatus unitTestStatus, boolean deploysSuccessfullyToStaging, TestStatus smokeTestStatus) {
        this.deploysSuccessfully = deploysSuccessfully;
        this.testStatus = unitTestStatus;
        this.deploysSuccessfullyToStaging = deploysSuccessfullyToStaging;
        this.smokeTestStatus = smokeTestStatus;
    }

    public boolean hasTests() {
        return testStatus != NO_TESTS;
    }

    public Status runTests() {
        return testStatus == PASSING_TESTS ? Status.SUCCESS : Status.FAILURE;
    }

    public Status deploy() {
        return deploy(DeploymentEnvironment.PRODUCTION);
    }
    public Status deploy(DeploymentEnvironment environment) {
       boolean success = switch (environment) {
            case STAGING -> deploysSuccessfullyToStaging;
            case PRODUCTION -> deploysSuccessfully;
            default -> false;
        };
       return success ? Status.SUCCESS : Status.FAILURE;
    }

    public TestStatus runSmokeTests() {
        return smokeTestStatus;
    }
}
