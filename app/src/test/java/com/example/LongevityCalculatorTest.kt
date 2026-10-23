package com.example

import com.example.data.model.BiomarkerProfile
import com.example.service.LongevityCalculatorService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LongevityCalculatorTest {

  // Reference values computed independently from Levine et al. 2018 (SI units).
  @Test
  fun phenoAge_matchesLevineReference_forHealthyDefaults() {
    assertEquals(31.3, LongevityCalculatorService.calculatePhenoAge(BiomarkerProfile()), 0.15)
  }

  @Test
  fun phenoAge_matchesLevineReference_forInflamedMetabolicProfile() {
    val profile = BiomarkerProfile(
      chronologicalAge = 55.0,
      glucoseMgDl = 130.0,
      albuminGDl = 3.8,
      hsCrpMgL = 6.0,
      rdwPercent = 15.0,
      wbc103Ul = 9.0,
      lymphocytePercent = 22.0
    )
    assertEquals(65.0, LongevityCalculatorService.calculatePhenoAge(profile), 0.15)
  }

  @Test
  fun ageDifference_isPhenoAgeMinusChronologicalAge() {
    val result = LongevityCalculatorService.computeDigitalTwin(BiomarkerProfile())
    assertEquals(result.phenoAge - result.chronologicalAge, result.ageDifference, 0.051)
  }

  @Test
  fun worseBiomarkers_produceOlderPhenoAge() {
    val healthy = LongevityCalculatorService.calculatePhenoAge(BiomarkerProfile())
    val inflamed = LongevityCalculatorService.calculatePhenoAge(BiomarkerProfile(hsCrpMgL = 8.0, rdwPercent = 15.5))
    assertTrue(inflamed > healthy)
  }

  @Test
  fun mortalityRisk_isAPercentage() {
    val risk = LongevityCalculatorService.computeDigitalTwin(BiomarkerProfile()).gompertzMortalityRisk
    assertTrue("risk $risk should be within 0..100", risk in 0.0..100.0)
  }
}
