package com.revenuecat.placeholder

object Configuration {
  const val compileSdk = 36
  const val targetSdk = 36
  const val minSdk = 23
  const val majorVersion = 1
  const val minorVersion = 0
  const val patchVersion = 2
  const val versionName = "$majorVersion.$minorVersion.$patchVersion"
  const val versionCode = 3
  const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
  const val artifactGroup = "com.revenuecat.purchases"
}
