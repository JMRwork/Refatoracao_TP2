package org.sammancoaching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sammancoaching.dependencies.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PipelineTest {

    @Mock
    private Config configMock;
    @Mock
    private Emailer emailerMock;

    private CapturingLogger logger;
    private Pipeline pipeline;

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
        logger = new CapturingLogger();
        pipeline = new Pipeline(configMock, emailerMock, logger);
    }

    // ========== Cenários com email habilitado ==========

    @Test
    void noTests_deploySuccess_emailEnabled() {
        when(configMock.sendEmailSummary()).thenReturn(true);
        Project project = Project.builder()
                .setTestStatus(TestStatus.NO_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: No tests"));
        assertTrue(logger.getLoggedLines().get(1).contains("INFO: Deployment successful"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Sending email"));
        verify(emailerMock).send("Deployment completed successfully");
    }

    @Test
    void noTests_deployFails_emailEnabled() {
        when(configMock.sendEmailSummary()).thenReturn(true);
        Project project = Project.builder()
                .setTestStatus(TestStatus.NO_TESTS)
                .setDeploysSuccessfully(false)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: No tests"));
        assertTrue(logger.getLoggedLines().get(1).contains("ERROR: Deployment failed"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Sending email"));
        verify(emailerMock).send("Deployment failed");
    }

    @Test
    void testsPassing_deploySuccess_emailEnabled() {
        when(configMock.sendEmailSummary()).thenReturn(true);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: Tests passed"));
        assertTrue(logger.getLoggedLines().get(1).contains("INFO: Deployment successful"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Sending email"));
        verify(emailerMock).send("Deployment completed successfully");
    }

    @Test
    void testsPassing_deployFails_emailEnabled() {
        when(configMock.sendEmailSummary()).thenReturn(true);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(false)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: Tests passed"));
        assertTrue(logger.getLoggedLines().get(1).contains("ERROR: Deployment failed"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Sending email"));
        verify(emailerMock).send("Deployment failed");
    }

    @Test
    void testsFailing_emailEnabled() {
        when(configMock.sendEmailSummary()).thenReturn(true);
        Project project = Project.builder()
                .setTestStatus(TestStatus.FAILING_TESTS)
                .setDeploysSuccessfully(true) // não deve ser usado
                .build();

        pipeline.run(project);

        assertEquals(2, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("ERROR: Tests failed"));
        assertTrue(logger.getLoggedLines().get(1).contains("INFO: Sending email"));
        verify(emailerMock).send("Tests failed");
    }

    // ========== Cenários com email desabilitado ==========

    @Test
    void testsPassing_deploySuccess_emailDisabled() {
        when(configMock.sendEmailSummary()).thenReturn(false);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: Tests passed"));
        assertTrue(logger.getLoggedLines().get(1).contains("INFO: Deployment successful"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Email disabled"));
        verifyNoInteractions(emailerMock);
    }

    @Test
    void testsPassing_deployFails_emailDisabled() {
        when(configMock.sendEmailSummary()).thenReturn(false);
        Project project = Project.builder()
                .setTestStatus(TestStatus.PASSING_TESTS)
                .setDeploysSuccessfully(false)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: Tests passed"));
        assertTrue(logger.getLoggedLines().get(1).contains("ERROR: Deployment failed"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Email disabled"));
        verifyNoInteractions(emailerMock);
    }

    @Test
    void testsFailing_emailDisabled() {
        when(configMock.sendEmailSummary()).thenReturn(false);
        Project project = Project.builder()
                .setTestStatus(TestStatus.FAILING_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        pipeline.run(project);

        assertEquals(2, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("ERROR: Tests failed"));
        assertTrue(logger.getLoggedLines().get(1).contains("INFO: Email disabled"));
        verifyNoInteractions(emailerMock);
    }

    @Test
    void noTests_deploySuccess_emailDisabled() {
        when(configMock.sendEmailSummary()).thenReturn(false);
        Project project = Project.builder()
                .setTestStatus(TestStatus.NO_TESTS)
                .setDeploysSuccessfully(true)
                .build();

        pipeline.run(project);

        assertEquals(3, logger.getLoggedLines().size());
        assertTrue(logger.getLoggedLines().get(0).contains("INFO: No tests"));
        assertTrue(logger.getLoggedLines().get(1).contains("INFO: Deployment successful"));
        assertTrue(logger.getLoggedLines().get(2).contains("INFO: Email disabled"));
        verifyNoInteractions(emailerMock);
    }

    // Teste extra para garantir que deploy não é tentado quando testes falham
    @Test
    void testsFailing_shouldNotAttemptDeploy() {
        when(configMock.sendEmailSummary()).thenReturn(false);
        Project project = Project.builder()
                .setTestStatus(TestStatus.FAILING_TESTS)
                .setDeploysSuccessfully(true) // mesmo que seja true, não deve ser chamado
                .build();

        pipeline.run(project);

        // Verifica que não há log de deploy
        assertTrue(logger.getLoggedLines().stream().noneMatch(line -> line.contains("Deployment")));
    }
}