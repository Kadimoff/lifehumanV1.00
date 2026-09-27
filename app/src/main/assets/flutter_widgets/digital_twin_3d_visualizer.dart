// Flutter / Dart 3D Lightweight Digital Twin Visualizer for LIFEMAP HUMAN™
// Compatible with: Flutter 3.x+ / Web, Android, iOS, macOS, Windows, Linux
// Technology: Pure lightweight Dart 3D Projection Engine & CustomPainter (Zero heavy native C++ binary overhead)

import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/// 3D Coordinate in anatomical model space
class Vector3D {
  final double x;
  final double y;
  final double z;

  const Vector3D(this.x, this.y, this.z);

  Vector3D copyWith({double? x, double? y, double? z}) =>
      Vector3D(x ?? this.x, y ?? this.y, z ?? this.z);

  /// Apply Yaw (around Y axis) and Pitch (around X axis) rotations
  Vector3D rotate(double yawRad, double pitchRad) {
    // 1. Rotate around X axis (Pitch)
    final cosPitch = math.cos(pitchRad);
    final sinPitch = math.sin(pitchRad);
    final y1 = y * cosPitch - z * sinPitch;
    final z1 = y * sinPitch + z * cosPitch;

    // 2. Rotate around Y axis (Yaw)
    final cosYaw = math.cos(yawRad);
    final sinYaw = math.sin(yawRad);
    final x2 = x * cosYaw + z1 * sinYaw;
    final z2 = -x * sinYaw + z1 * cosYaw;

    return Vector3D(x2, y1, z2);
  }

  /// 3D to 2D Perspective Projection
  Offset project(Size size, {double fov = 450.0, double scale = 1.0}) {
    final cameraDistance = 350.0;
    final depth = z + cameraDistance;
    final factor = (depth > 10.0) ? (fov / depth) * scale : 1.0;

    final screenX = (size.width / 2.0) + (x * factor);
    final screenY = (size.height / 2.0) + (y * factor);

    return Offset(screenX, screenY);
  }
}

/// Anatomical Bone / Wireframe segment
class MeshEdge {
  final int startIndex;
  final int endIndex;
  final Color color;
  final double strokeWidth;

  const MeshEdge(this.startIndex, this.endIndex, {
    this.color = const Color(0x33CDC0E9),
    this.strokeWidth = 1.2,
  });
}

/// Interactive Organ & System Reserve Node
class SystemReserveNode {
  final String id;
  final String title;
  final String systemName;
  final Vector3D localPosition;
  final Color color;
  final double score; // 0–100 scale
  final int biologicalAge;
  final String clinicalSummary;
  final List<String> biomarkers;
  final IconData icon;

  const SystemReserveNode({
    required this.id,
    required this.title,
    required this.systemName,
    required this.localPosition,
    required this.color,
    required this.score,
    required this.biologicalAge,
    required this.clinicalSummary,
    required this.biomarkers,
    required this.icon,
  });
}

/// 3D Digital Twin Visualizer Widget
class DigitalTwin3DVisualizer extends StatefulWidget {
  final List<SystemReserveNode> reserveNodes;
  final double overallLrsScore;
  final double phenoAge;
  final double chronologicalAge;
  final Function(SystemReserveNode node)? onNodeSelected;
  final bool enableAutoRotation;

  const DigitalTwin3DVisualizer({
    Key? key,
    required this.reserveNodes,
    this.overallLrsScore = 82.0,
    this.phenoAge = 35.8,
    this.chronologicalAge = 40.0,
    this.onNodeSelected,
    this.enableAutoRotation = true,
  }) : super(key: key);

  @override
  State<DigitalTwin3DVisualizer> createState() => _DigitalTwin3DVisualizerState();
}

class _DigitalTwin3DVisualizerState extends State<DigitalTwin3DVisualizer>
    with SingleTickerProviderStateMixin {
  double _yaw = 0.35; // Radians (~20 deg)
  double _pitch = 0.05; // Radians
  bool _isAutoRotating = true;
  SystemReserveNode? _selectedNode;
  late AnimationController _pulseController;

  // Preset 3D Mesh vertices for holographic anatomical avatar
  static const List<Vector3D> _bodyVertices = [
    // Head & Neck (0..4)
    Vector3D(0, -145, 0),    // 0: Crown
    Vector3D(0, -125, 0),    // 1: Forehead / Brain
    Vector3D(0, -105, 0),    // 2: Chin
    Vector3D(-18, -125, 0),  // 3: Left Ear
    Vector3D(18, -125, 0),   // 4: Right Ear

    // Torso & Shoulders (5..12)
    Vector3D(0, -90, 0),     // 5: Neck base
    Vector3D(-42, -80, 0),   // 6: Left Shoulder
    Vector3D(42, -80, 0),    // 7: Right Shoulder
    Vector3D(-12, -65, 12),  // 8: Cardiac Center
    Vector3D(0, -40, 0),     // 9: Mid Spine
    Vector3D(12, -20, 10),   // 10: Hepatic / Metabolic
    Vector3D(-28, -25, 0),   // 11: Left Waist
    Vector3D(28, -25, 0),    // 12: Right Waist

    // Pelvis & Hips (13..15)
    Vector3D(0, 5, 0),       // 13: Pelvis Core
    Vector3D(-25, 15, 0),    // 14: Left Hip
    Vector3D(25, 15, 0),     // 15: Right Hip

    // Left Arm (16..18)
    Vector3D(-55, -40, 0),   // 16: Left Elbow
    Vector3D(-65, 0, 0),     // 17: Left Wrist
    Vector3D(-68, 15, 0),    // 18: Left Hand

    // Right Arm (19..21)
    Vector3D(55, -40, 0),    // 19: Right Elbow
    Vector3D(65, 0, 0),      // 20: Right Wrist
    Vector3D(68, 15, 0),     // 21: Right Hand

    // Left Leg (22..24)
    Vector3D(-22, 65, 5),    // 22: Left Knee
    Vector3D(-20, 120, 0),   // 23: Left Ankle
    Vector3D(-20, 130, 12),  // 24: Left Foot

    // Right Leg (25..27)
    Vector3D(22, 65, 5),     // 25: Right Knee
    Vector3D(20, 120, 0),    // 26: Right Ankle
    Vector3D(20, 130, 12),   // 27: Right Foot
  ];

  static const List<MeshEdge> _bodyEdges = [
    // Head Contour
    MeshEdge(0, 3), MeshEdge(0, 4), MeshEdge(3, 2), MeshEdge(4, 2),
    MeshEdge(0, 1), MeshEdge(1, 2), MeshEdge(2, 5),

    // Shoulders & Spine
    MeshEdge(5, 6), MeshEdge(5, 7), MeshEdge(6, 7),
    MeshEdge(5, 9), MeshEdge(9, 13),

    // Ribcage / Chest Cage
    MeshEdge(6, 11), MeshEdge(7, 12), MeshEdge(11, 12),
    MeshEdge(8, 9), MeshEdge(10, 9),

    // Pelvis
    MeshEdge(13, 14), MeshEdge(13, 15), MeshEdge(14, 15),

    // Left Arm
    MeshEdge(6, 16), MeshEdge(16, 17), MeshEdge(17, 18),

    // Right Arm
    MeshEdge(7, 19), MeshEdge(19, 20), MeshEdge(20, 21),

    // Left Leg
    MeshEdge(14, 22), MeshEdge(22, 23), MeshEdge(23, 24),

    // Right Leg
    MeshEdge(15, 25), MeshEdge(25, 26), MeshEdge(26, 27),
  ];

  @override
  void initState() {
    super.initState();
    _isAutoRotating = widget.enableAutoRotation;
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1100),
    )..repeat(reverse: true);

    // Auto-orbit ticker loop
    Timer.periodic(const Duration(milliseconds: 16), (timer) {
      if (!mounted) {
        timer.cancel();
        return;
      }
      if (_isAutoRotating) {
        setState(() {
          _yaw = (_yaw + 0.009) % (2 * math.pi);
        });
      }
    });
  }

  @override
  void dispose() {
    _pulseController.dispose();
    super.dispose();
  }

  void _handleTap(Offset tapPosition, Size size) {
    // Find closest projected organ node within tap radius
    double minDistance = 28.0;
    SystemReserveNode? closest;

    for (final node in widget.reserveNodes) {
      final rotated = node.localPosition.rotate(_yaw, _pitch);
      final projected = rotated.project(size);
      final distance = (projected - tapPosition).distance;

      if (distance < minDistance) {
        minDistance = distance;
        closest = node;
      }
    }

    if (closest != null) {
      HapticFeedback.mediumImpact();
      setState(() {
        _selectedNode = closest;
        _isAutoRotating = false;
      });
      if (widget.onNodeSelected != null) {
        widget.onNodeSelected!(closest);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final size = Size(constraints.maxWidth, constraints.maxHeight);

        return Container(
          decoration: BoxDecoration(
            color: const Color(0xFF0F1014),
            borderRadius: BorderRadius.circular(24.0),
            border: Border.all(color: const Color(0xFF23252E), width: 1.2),
          ),
          child: Stack(
            children: [
              // 3D Canvas with Gesture Drag Interaction
              GestureDetector(
                onPanStart: (_) {
                  setState(() {
                    _isAutoRotating = false;
                  });
                },
                onPanUpdate: (details) {
                  setState(() {
                    _yaw = (_yaw + details.delta.dx * 0.008) % (2 * math.pi);
                    _pitch = (_pitch - details.delta.dy * 0.006)
                        .clamp(-0.5, 0.5); // Limit pitch rotation
                  });
                },
                onTapUp: (details) => _handleTap(details.localPosition, size),
                child: AnimatedBuilder(
                  animation: _pulseController,
                  builder: (context, child) {
                    return CustomPaint(
                      size: Size.infinite,
                      painter: _DigitalTwin3DPainter(
                        vertices: _bodyVertices,
                        edges: _bodyEdges,
                        nodes: widget.reserveNodes,
                        selectedNode: _selectedNode,
                        yaw: _yaw,
                        pitch: _pitch,
                        pulseValue: _pulseController.value,
                      ),
                    );
                  },
                ),
              ),

              // Top Controls Overlay
              Positioned(
                top: 14,
                left: 16,
                right: 16,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // System Status Badge
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                      decoration: BoxDecoration(
                        color: const Color(0xCC1A1B22),
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(color: const Color(0xFF323440)),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Container(
                            width: 8,
                            height: 8,
                            decoration: const BoxDecoration(
                              color: Color(0xFFCDC0E9),
                              shape: BoxShape.circle,
                            ),
                          ),
                          const SizedBox(width: 6),
                          Text(
                            'LRS: ${widget.overallLrsScore.toInt()}/100 • PhenoAge: ${widget.phenoAge} yrs',
                            style: const TextStyle(
                              color: Color(0xFFCDC0E9),
                              fontSize: 11,
                              fontWeight: FontWeight.bold,
                              letterSpacing: 0.3,
                            ),
                          ),
                        ],
                      ),
                    ),

                    // Orbit Controls
                    Row(
                      children: [
                        // Auto-Rotate Button
                        InkWell(
                          onTap: () {
                            setState(() {
                              _isAutoRotating = !_isAutoRotating;
                            });
                          },
                          borderRadius: BorderRadius.circular(8),
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 8, vertical: 5),
                            decoration: BoxDecoration(
                              color: _isAutoRotating
                                  ? const Color(0x33CDC0E9)
                                  : const Color(0xFF1E2028),
                              borderRadius: BorderRadius.circular(8),
                              border: Border.all(
                                color: _isAutoRotating
                                    ? const Color(0xFFCDC0E9)
                                    : const Color(0xFF2E313C),
                              ),
                            ),
                            child: Text(
                              _isAutoRotating ? '3D ORBIT: ON' : '3D ORBIT: PAUSE',
                              style: TextStyle(
                                color: _isAutoRotating
                                    ? const Color(0xFFCDC0E9)
                                    : const Color(0xFF8E9099),
                                fontSize: 10,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 6),
                        // Reset View
                        IconButton(
                          icon: const Icon(Icons.restart_alt,
                              color: Color(0xFF8E9099), size: 18),
                          onPressed: () {
                            setState(() {
                              _yaw = 0.35;
                              _pitch = 0.05;
                              _selectedNode = null;
                            });
                          },
                          tooltip: 'Reset 3D Angle',
                          constraints: const BoxConstraints(
                              minWidth: 28, minHeight: 28),
                          padding: EdgeInsets.zero,
                        ),
                      ],
                    ),
                  ],
                ),
              ),

              // Organ Nodes Horizontal Quick-Selector
              Positioned(
                bottom: _selectedNode != null ? 140 : 16,
                left: 12,
                right: 12,
                child: SizedBox(
                  height: 34,
                  child: ListView.separated(
                    scrollDirection: Axis.horizontal,
                    itemCount: widget.reserveNodes.length,
                    separatorBuilder: (_, __) => const SizedBox(width: 6),
                    itemBuilder: (context, index) {
                      final node = widget.reserveNodes[index];
                      final isSelected = _selectedNode?.id == node.id;

                      return InkWell(
                        onTap: () {
                          setState(() {
                            _selectedNode = isSelected ? null : node;
                            _isAutoRotating = false;
                          });
                        },
                        borderRadius: BorderRadius.circular(8),
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 10, vertical: 6),
                          decoration: BoxDecoration(
                            color: isSelected
                                ? node.color.withOpacity(0.3)
                                : const Color(0xCC1A1B22),
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(
                              color: isSelected
                                  ? node.color
                                  : const Color(0xFF282A34),
                            ),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(node.icon,
                                  color: isSelected ? node.color : const Color(0xFF8E9099),
                                  size: 13),
                              const SizedBox(width: 5),
                              Text(
                                node.title,
                                style: TextStyle(
                                  color: isSelected ? Colors.white : const Color(0xFF8E9099),
                                  fontSize: 11,
                                  fontWeight: isSelected
                                      ? FontWeight.bold
                                      : FontWeight.normal,
                                ),
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ),

              // Selected Node Telemetry Card (Slide-up)
              if (_selectedNode != null)
                Positioned(
                  bottom: 12,
                  left: 12,
                  right: 12,
                  child: _SelectedOrganTelemetryCard(
                    node: _selectedNode!,
                    onClose: () {
                      setState(() {
                        _selectedNode = null;
                      });
                    },
                  ),
                ),
            ],
          ),
        );
      },
    );
  }
}

/// Custom 3D Holographic Canvas Painter
class _DigitalTwin3DPainter extends CustomPainter {
  final List<Vector3D> vertices;
  final List<MeshEdge> edges;
  final List<SystemReserveNode> nodes;
  final SystemReserveNode? selectedNode;
  final double yaw;
  final double pitch;
  final double pulseValue;

  _DigitalTwin3DPainter({
    required this.vertices,
    required this.edges,
    required this.nodes,
    required this.selectedNode,
    required this.yaw,
    required this.pitch,
    required this.pulseValue,
  });

  @override
  void paint(Canvas canvas, Size size) {
    // 1. Draw Holographic Circular Grid Platform
    _drawHolographicPlatform(canvas, size);

    // 2. Project all body vertices
    final projectedVertices = vertices.map((v) {
      final rotated = v.rotate(yaw, pitch);
      return rotated.project(size);
    }).toList();

    // 3. Draw Body Wireframe Edges
    final linePaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeCap = StrokeCap.round;

    for (final edge in edges) {
      if (edge.startIndex < projectedVertices.length &&
          edge.endIndex < projectedVertices.length) {
        final p1 = projectedVertices[edge.startIndex];
        final p2 = projectedVertices[edge.endIndex];

        linePaint
          ..color = edge.color
          ..strokeWidth = edge.strokeWidth;

        canvas.drawLine(p1, p2, linePaint);
      }
    }

    // 4. Draw Joint Nodes
    final jointPaint = Paint()..style = PaintingStyle.fill;
    for (final p in projectedVertices) {
      jointPaint.color = const Color(0x55CDC0E9);
      canvas.drawCircle(p, 1.8, jointPaint);
    }

    // 5. Draw Interactive System Reserve Nodes (Depth-Sorted)
    final rotatedNodes = nodes.map((node) {
      final rot = node.localPosition.rotate(yaw, pitch);
      return MapEntry(node, rot);
    }).toList()
      ..sort((a, b) => b.value.z.compareTo(a.value.z)); // Draw back-to-front

    for (final entry in rotatedNodes) {
      final node = entry.key;
      final rotPos = entry.value;
      final proj = rotPos.project(size);
      final isSelected = selectedNode?.id == node.id;

      // Pulse multiplier for vascular/cardiac node
      final dynamicPulse = (node.id == 'vascular') ? (1.0 + pulseValue * 0.35) : 1.0;
      final outerRadius = (isSelected ? 16.0 : 9.0) * dynamicPulse;

      // Glow Aura
      final auraPaint = Paint()
        ..color = node.color.withOpacity(isSelected ? 0.45 : 0.20)
        ..style = PaintingStyle.fill;
      canvas.drawCircle(proj, outerRadius, auraPaint);

      // Core Circle
      final corePaint = Paint()
        ..color = node.color
        ..style = PaintingStyle.fill;
      canvas.drawCircle(proj, isSelected ? 6.5 : 4.5, corePaint);

      // White Center Dot
      canvas.drawCircle(proj, 1.8, Paint()..color = Colors.white);

      // Leader Tether Line to Central Axis
      final spineProj = Vector3D(0, node.localPosition.y, 0)
          .rotate(yaw, pitch)
          .project(size);

      final tetherPaint = Paint()
        ..color = node.color.withOpacity(isSelected ? 0.75 : 0.25)
        ..strokeWidth = isSelected ? 1.8 : 0.9;
      canvas.drawLine(spineProj, proj, tetherPaint);
    }
  }

  void _drawHolographicPlatform(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2 + 130);
    final floorPaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.0;

    // Concentric perspective rings
    for (int r = 1; r <= 3; r++) {
      floorPaint.color = const Color(0xFFCDC0E9).withOpacity(0.06 * r);
      canvas.drawOval(
        Rect.fromCenter(center: center, width: r * 80.0, height: r * 28.0),
        floorPaint,
      );
    }

    // Radial spokes rotating with yaw
    final spokePaint = Paint()
      ..color = const Color(0xFFCDC0E9).withOpacity(0.08)
      ..strokeWidth = 0.8;

    for (int i = 0; i < 8; i++) {
      final angle = yaw + (i * math.pi / 4);
      final dx = math.cos(angle) * 110.0;
      final dy = math.sin(angle) * 38.0;
      canvas.drawLine(center, center + Offset(dx, dy), spokePaint);
    }
  }

  @override
  bool shouldRepaint(covariant _DigitalTwin3DPainter oldDelegate) {
    return oldDelegate.yaw != yaw ||
        oldDelegate.pitch != pitch ||
        oldDelegate.selectedNode != selectedNode ||
        oldDelegate.pulseValue != pulseValue;
  }
}

/// Slide-up Organ Telemetry Detail Card
class _SelectedOrganTelemetryCard extends StatelessWidget {
  final SystemReserveNode node;
  final VoidCallback onClose;

  const _SelectedOrganTelemetryCard({
    Key? key,
    required this.node,
    required this.onClose,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xF2181920),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: node.color.withOpacity(0.6), width: 1.2),
        boxShadow: [
          BoxShadow(
            color: node.color.withOpacity(0.12),
            blurRadius: 16,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header Row
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(6),
                    decoration: BoxDecoration(
                      color: node.color.withOpacity(0.2),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(node.icon, color: node.color, size: 16),
                  ),
                  const SizedBox(width: 8),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        node.title,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 13,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        '${node.systemName} • Est. Age: ${node.biologicalAge} yrs',
                        style: TextStyle(
                          color: node.color,
                          fontSize: 10,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              Row(
                children: [
                  Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: node.color.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      '${node.score.toInt()}/100',
                      style: TextStyle(
                        color: node.color,
                        fontSize: 12,
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                  ),
                  const SizedBox(width: 6),
                  InkWell(
                    onTap: onClose,
                    child: const Icon(Icons.close,
                        color: Color(0xFF8E9099), size: 18),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: 8),
          // Clinical Description
          Text(
            node.clinicalSummary,
            style: const TextStyle(
              color: Color(0xFFB0B3BE),
              fontSize: 11,
              height: 1.35,
            ),
          ),
          const SizedBox(height: 8),
          // Biomarker tags
          Wrap(
            spacing: 6,
            runSpacing: 4,
            children: node.biomarkers.map((b) {
              return Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: const Color(0xFF101116),
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(color: const Color(0xFF282A34)),
                ),
                child: Text(
                  b,
                  style: const TextStyle(
                    color: Color(0xFFE2E2E9),
                    fontSize: 9.5,
                  ),
                ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }
}
