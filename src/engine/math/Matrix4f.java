package engine.math;

import org.joml.Vector3f;

public class Matrix4f {
    public static final int SIZE = 4;
    private float[] elements = new float[SIZE * SIZE];

    public static Matrix4f identity() {
        Matrix4f result = new Matrix4f();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, 0);
            }
        }

        result.set(0, 0, 1);
        result.set(1, 1, 1);
        result.set(2, 2, 1);
        result.set(3, 3, 1);

        return result;
    }

    public static Matrix4f translate(Vector3f translate) {
        Matrix4f result = Matrix4f.identity();

        result.set(3, 0, translate.x);
        result.set(3, 1, translate.y);
        result.set(3, 2, translate.z);

        return result;
    }

    public static Matrix4f rotate(float angle, Vector3f axis) {
        Matrix4f result = Matrix4f.identity();

        float cos = (float) Math.cos(Math.toRadians(angle));
        float sin = (float) Math.sin(Math.toRadians(angle));
        float C = 1 - cos;

        result.set(0, 0, cos + axis.x * axis.x * C);
        result.set(0, 1, axis.x * axis.y * C - axis.z * sin);
        result.set(0, 2, axis.z * axis.z * C + axis.y * sin);
        result.set(1, 0, axis.y * axis.x * C + axis.z * sin);
        result.set(1, 1, cos + axis.y * axis.y * C);
        result.set(1, 2, axis.y* axis.z * C - axis.x * sin);
        result.set(2, 0, axis.z * axis.x * C - axis.y * sin);
        result.set(2, 1, axis.z * axis.y * C + axis.x * sin);
        result.set(2, 2, cos + axis.z * axis.z * C);

        return result;
    }

    public static Matrix4f scale(Vector3f scalar) {
        Matrix4f result = Matrix4f.identity();

        result.set(0, 0, scalar.x);
        result.set(1, 1, scalar.y);
        result.set(2, 2, scalar.z);

        return result;
    }

    public static Matrix4f projection(float fov, float aspect, float near, float far) {
        Matrix4f result = Matrix4f.identity();

        float tanFOV = (float) Math.tan(Math.toRadians(fov / 2));
        float range = far - near;

        result.set(0, 0, 1.0f / (aspect * tanFOV));
        result.set(1, 1, 1.0f / tanFOV);
        result.set(2, 2, -((far + near) / range));
        result.set(2, 3, -1.0f);
        result.set(3, 2, -((2 * far * near) / range));
        result.set(3, 3, 0.0f);

        return result;
    }

    public static Matrix4f view(Vector3f position, Vector3f rotation) {
        Matrix4f result = Matrix4f.identity();

        Vector3f negative = new Vector3f(-position.x, -position.y, -position.z);
        Matrix4f translationMatrix = Matrix4f.translate(negative);
        Matrix4f rotXMatrix = Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0));
        Matrix4f rotYMatrix = Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0));
        Matrix4f rotZMatrix = Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1));

        Matrix4f rotationMatrix = Matrix4f.multiply(rotZMatrix, Matrix4f.multiply(rotYMatrix, rotXMatrix));

        result = Matrix4f.multiply(translationMatrix, rotationMatrix);

        return result;
    }

    public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f result = Matrix4f.identity();

        Matrix4f translationMatrix = Matrix4f.translate(position);
        Matrix4f rotXMatrix = Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0));
        Matrix4f rotYMatrix = Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0));
        Matrix4f rotZMatrix = Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1));
        Matrix4f scaleMatrix = Matrix4f.scale(scale);

        Matrix4f rotationMatrix = Matrix4f.multiply(rotXMatrix, Matrix4f.multiply(rotYMatrix, rotZMatrix));

        result = Matrix4f.multiply(translationMatrix, Matrix4f.multiply(rotationMatrix, scaleMatrix));

        return result;
    }

    public static Matrix4f multiply(Matrix4f matrix, Matrix4f other) {
        Matrix4f result = Matrix4f.identity();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, matrix.get(i, 0) * other.get(0, j) +
                        matrix.get(i, 1) * other.get(1, j) +
                        matrix.get(i, 2) * other.get(2, j) +
                        matrix.get(i, 3) * other.get(3, j));
            }
        }

        return result;
    }

    public float get(int x, int y) {
        return elements[y * SIZE + x];
    }

    public void set(int x, int y, float value) {
        elements[y * SIZE + x] = value;
    }

    public float[] getAll() {
        return elements;
    }
}