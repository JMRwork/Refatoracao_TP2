package org.sammancoaching.dependencies;

public class ProjectBuilder {
	public boolean deploysSuccessfully;
	public TestStatus testStatus;
	public boolean deploysSuccessfullyToStaging;
	public TestStatus smokeTestStatus;

	public ProjectBuilder setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
        return this;
    }

    public ProjectBuilder setSmokeTestStatus(TestStatus smokeTestStatus) {
        this.smokeTestStatus = smokeTestStatus;
        return this;
    }

    public ProjectBuilder setDeploysSuccessfully(boolean deploysSuccessfully) {
        this.deploysSuccessfully = deploysSuccessfully;
        return this;
    }

    public ProjectBuilder setDeploysSuccessfullyToStaging(boolean deploysSuccessfully) {
        this.deploysSuccessfullyToStaging = deploysSuccessfully;
        return this;
    }

    public Project build() {
        return new Project(deploysSuccessfully, testStatus, deploysSuccessfullyToStaging, smokeTestStatus);
    }
}