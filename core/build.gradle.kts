dependencies {
    compileOnlyApi(libs.google.gson)
    compileOnlyApi(libs.tool.jb.annotations)
    compileOnlyApi(libs.tool.jspecify)

    testImplementation(platform(libs.test.junit.bom))
    testImplementation(libs.google.gson)
    testImplementation(libs.test.junit.jupiter)

    testRuntimeOnly(libs.test.junit.platform.launcher)
}
