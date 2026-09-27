package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BiomarkerProfile
import com.example.service.LongevityCalculatorService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LIFEMAP HUMAN", appName)
  }

  @Test
  fun `test longevity calculator Levine PhenoAge computation`() {
    val profile = BiomarkerProfile(
      chronologicalAge = 45.0,
      glucoseMgDl = 88.0,
      hsCrpMgL = 0.45,
      restingPulseBpm = 54.0,
      dailySteps = 10500
    )
    val result = LongevityCalculatorService.computeDigitalTwin(profile)
    assertNotNull(result)
    assertTrue("PhenoAge should be positive", result.phenoAge > 0.0)
    assertEquals(7, result.reserves.size)
    assertTrue("Longevity Reserve Score should be between 0 and 100", result.longevityReserveScore in 0.0..100.0)
  }
}

