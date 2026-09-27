// Flutter / Dart Implementation for LIFEMAP HUMAN™ 8-Axis Radar Chart
// Package: fl_chart (fl_chart: ^0.68.0+)
// State Management: Riverpod / Provider Real-Time Binding

import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';

/// Data Model representing the 8 Functional Reserve Scores (0–100 scale)
class LongevityReserveScores {
  final double vascular;
  final double metabolic;
  final double inflammatory;
  final double hormonal;
  final double muscle;
  final double cognitive;
  final double psychological;
  final double longevityReserveScore; // LRS Harmonic Weighted Mean

  const LongevityReserveScores({
    required this.vascular,
    required this.metabolic,
    required this.inflammatory,
    required this.hormonal,
    required this.muscle,
    required this.cognitive,
    required this.psychological,
    required this.longevityReserveScore,
  });

  /// Factory constructor for default clinical baseline
  factory LongevityReserveScores.baseline() {
    return const LongevityReserveScores(
      vascular: 82.0,
      metabolic: 80.0,
      inflammatory: 78.0,
      hormonal: 80.0,
      muscle: 84.0,
      cognitive: 85.0,
      psychological: 82.0,
      longevityReserveScore: 81.5,
    );
  }

  /// List of scores aligned to the 8 standard axes
  List<double> toList() => [
        vascular.clamp(0.0, 100.0),
        metabolic.clamp(0.0, 100.0),
        inflammatory.clamp(0.0, 100.0),
        hormonal.clamp(0.0, 100.0),
        muscle.clamp(0.0, 100.0),
        cognitive.clamp(0.0, 100.0),
        psychological.clamp(0.0, 100.0),
        longevityReserveScore.clamp(0.0, 100.0),
      ];
}

/// Custom Flutter Widget rendering an 8-Axis Longevity Radar Chart with fl_chart
class LongevityRadarChart extends StatefulWidget {
  final LongevityReserveScores reserves;
  final bool animate;
  final Function(int axisIndex, String axisName, double score)? onAxisTapped;

  const LongevityRadarChart({
    Key? key,
    required this.reserves,
    this.animate = true,
    this.onAxisTapped,
  }) : super(key: key);

  @override
  State<LongevityRadarChart> createState() => _LongevityRadarChartState();
}

class _LongevityRadarChartState extends State<LongevityRadarChart>
    with SingleTickerProviderStateMixin {
  int? _selectedEntryIndex;
  late AnimationController _animController;
  late Animation<double> _scaleAnimation;

  static const List<String> _axisTitles = [
    'Vascular',
    'Metabolic',
    'Immune',
    'Hormonal',
    'Muscle',
    'Cognitive',
    'Psych',
    'LRS Mean',
  ];

  // Sleek Dark Theme Color Palette
  static const Color colorPrimary = Color(0xFFCDC0E9);     // Sleek Lilac Accent
  static const Color colorBackground = Color(0xFF141519);  // Dark Surface
  static const Color colorGridBorder = Color(0xFF2B2C33);  // Subtle Ring Border
  static const Color colorTextPrimary = Color(0xFFE2E2E9);
  static const Color colorTextMuted = Color(0xFF8E9099);
  static const Color colorGreen = Color(0xFF81C784);

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 750),
    );
    _scaleAnimation = CurvedAnimation(
      parent: _animController,
      curve: Curves.easeOutCubic,
    );
    if (widget.animate) {
      _animController.forward();
    } else {
      _animController.value = 1.0;
    }
  }

  @override
  void didUpdateWidget(covariant LongevityRadarChart oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.reserves != widget.reserves && widget.animate) {
      _animController.forward(from: 0.0);
    }
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _scaleAnimation,
      builder: (context, child) {
        final scores = widget.reserves.toList();
        final animatedEntries = scores.map((val) {
          // Animate entry from baseline towards target score
          final animatedVal = val * _scaleAnimation.value;
          return RadarEntry(value: math.max(10.0, animatedVal));
        }).toList();

        return AspectRatio(
          aspectRatio: 1.15,
          child: Stack(
            alignment: Alignment.center,
            children: [
              // Main fl_chart Radar Chart
              RadarChart(
                RadarChartData(
                  radarTouchData: RadarTouchData(
                    touchCallback: (FlTouchEvent event, response) {
                      if (!event.isInterestedForInteractions ||
                          response?.touchedSpot == null) {
                        setState(() {
                          _selectedEntryIndex = null;
                        });
                        return;
                      }
                      final touchedIndex =
                          response!.touchedSpot!.touchedRadarEntryIndex;
                      setState(() {
                        _selectedEntryIndex = touchedIndex;
                      });

                      if (widget.onAxisTapped != null &&
                          touchedIndex >= 0 &&
                          touchedIndex < _axisTitles.length) {
                        widget.onAxisTapped!(
                          touchedIndex,
                          _axisTitles[touchedIndex],
                          scores[touchedIndex],
                        );
                      }
                    },
                  ),
                  dataSets: [
                    // Optimal 100% Benchmark Layer (Subtle Reference Ring)
                    RadarDataSet(
                      fillColor: Colors.transparent,
                      borderColor: colorPrimary.withOpacity(0.18),
                      borderWidth: 1.0,
                      entryRadius: 0,
                      dataEntries: List.generate(
                        8,
                        (_) => const RadarEntry(value: 100.0),
                      ),
                    ),
                    // Live Biological Digital Twin User Polygon
                    RadarDataSet(
                      fillColor: colorPrimary.withOpacity(0.24),
                      borderColor: colorPrimary,
                      borderWidth: 2.2,
                      entryRadius: 3.5,
                      dataEntries: animatedEntries,
                    ),
                  ],
                  radarBackgroundColor: Colors.transparent,
                  borderData: FlBorderData(show: false),
                  radarBorderData: const BorderSide(
                    color: colorGridBorder,
                    width: 1.0,
                  ),
                  titlePositionPercentageOffset: 0.18,
                  titleTextStyle: const TextStyle(
                    color: colorTextMuted,
                    fontSize: 11.0,
                    fontWeight: FontWeight.w600,
                    letterSpacing: 0.4,
                  ),
                  getTitle: (index, angle) {
                    final isSelected = _selectedEntryIndex == index;
                    return RadarChartTitle(
                      text: _axisTitles[index],
                      positionPercentageOffset: 0.20,
                    );
                  },
                  tickCount: 3,
                  ticksTextStyle: const TextStyle(
                    color: colorTextMuted,
                    fontSize: 9.0,
                  ),
                  tickBorderData: const BorderSide(
                    color: colorGridBorder,
                    width: 0.8,
                  ),
                  gridBorderData: const BorderSide(
                    color: colorGridBorder,
                    width: 1.0,
                  ),
                ),
                swapAnimationDuration: const Duration(milliseconds: 350),
                swapAnimationCurve: Curves.easeInOut,
              ),

              // Center Harmonic Mean (LRS) Indicator Badge
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                decoration: BoxDecoration(
                  color: colorBackground.withOpacity(0.88),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color: colorPrimary.withOpacity(0.4),
                    width: 1.2,
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: colorPrimary.withOpacity(0.15),
                      blurRadius: 10,
                      spreadRadius: 2,
                    ),
                  ],
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text(
                      'LRS',
                      style: TextStyle(
                        color: colorTextMuted,
                        fontSize: 9.0,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 0.8,
                      ),
                    ),
                    Text(
                      '${(widget.reserves.longevityReserveScore * _scaleAnimation.value).toInt()}',
                      style: const TextStyle(
                        color: colorPrimary,
                        fontSize: 15.0,
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                  ],
                ),
              ),

              // Active Tooltip Overlay when user taps an axis
              if (_selectedEntryIndex != null &&
                  _selectedEntryIndex! >= 0 &&
                  _selectedEntryIndex! < _axisTitles.length)
                Positioned(
                  top: 10,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: colorBackground,
                      borderRadius: BorderRadius.circular(10),
                      border: Border.all(color: colorPrimary, width: 1),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(
                          '${_axisTitles[_selectedEntryIndex!]}: ',
                          style: const TextStyle(
                            color: colorTextPrimary,
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Text(
                          '${scores[_selectedEntryIndex!].toStringAsFixed(1)} / 100',
                          style: const TextStyle(
                            color: colorPrimary,
                            fontSize: 12,
                            fontWeight: FontWeight.w800,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
            ],
          ),
        );
      },
    );
  }
}
