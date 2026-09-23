import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    application
}

application {
    mainClass.set("dev.yorushi.dma.task2.Task2Runner")
}

dependencies {
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    testRuntimeOnly(libs.junit5.launcher)
}

tasks.withType<JavaCompile>().configureEach {
    // --release 17 pins both the bytecode level and the visible JDK API, so the
    // module stays consumable by Android's D8 when the app module is added.
    options.release.set(17)
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-serial"))
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        exceptionFormat = TestExceptionFormat.FULL
    }
    // Gradle lists tests one by one but never totals them; print the summary.
    afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ suite, result ->
        if (suite.parent == null) {
            println(
                "\nResults: ${result.resultType} (${result.testCount} tests, " +
                    "${result.successfulTestCount} passed, ${result.failedTestCount} failed, " +
                    "${result.skippedTestCount} skipped)"
            )
        }
    }))
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
