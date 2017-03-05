package net.sf.ardengine.shapes;

/**
 * Types of supported shapes, which can be returned by IShape.getType(),
 * so renderer can create appropriate implementation.
 */
public enum ShapeType {
    CIRCLE,
    RECTANGLE,
    POLYGON,
    LINES
}