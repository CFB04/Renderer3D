package cfbastian.renderer3d.math;

import java.util.Arrays;

public final class ArrayOperations {

    public static float[] everyNthElement(float[] arr, int n, int offset)
    {
        float[] ret = new float[arr.length/n];
        for (int i = 0; i < arr.length/n; i++) ret[i] = arr[i * n + offset];
        return ret;
    }

    public static void quicksort(float[] arr)
    {
        quicksort(arr, 0, arr.length - 1);
    }

    private static void quicksort(float[] arr, int start, int end)
    {
        if(start < end)
        {
            int pi = partition(arr, start, end);

            quicksort(arr, start, pi - 1);
            quicksort(arr, pi + 1, end);
        }
    }

    private static int partition(float[] arr, int start, int end)
    {

        float pivot = arr[end];
        int i = start - 1;
        for (int j = start; j < end; j++) {
            if(arr[j] < pivot)
            {
                i++;

                float temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        float temp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = temp;

        return i+1;
    }

    public static int[] sortTris(int[] faces, float[] vertices, int axis)
    {
        int[] faceIdxs = new int[faces.length/3];
        for (int i = 0; i < faceIdxs.length; i++) faceIdxs[i] = i;

        quicksort(faceIdxs, faces, vertices, 0, faces.length/3 - 1, axis);

        int[] ret = new int[faces.length];

        for (int i = 0; i < faceIdxs.length; i++) {
            ret[i*3] = faces[faceIdxs[i]*3];
            ret[i*3+1] = faces[faceIdxs[i]*3+1];
            ret[i*3+2] = faces[faceIdxs[i]*3+2];
        }

        return ret;
    }

    private static void quicksort(int[] faceIdxs, int[] faces, float[] vertices, int start, int end, int axis)
    {
        if(start < end)
        {
            int pi = partition(faceIdxs, faces, vertices, start, end, axis);

            quicksort(faceIdxs, faces, vertices, start, pi - 1, axis);
            quicksort(faceIdxs, faces, vertices, pi + 1, end, axis);
        }
    }

    private static int partition(int[] faceIdxs, int[] faces, float[] vertices, int start, int end, int axis)
    {
        int i = start - 1;
        for (int j = start; j < end; j++)
        {
            float a = ScalarMath.min(vertices[faces[faceIdxs[j]*3]*3 + axis], vertices[faces[faceIdxs[j]*3+1]*3 + axis], vertices[faces[faceIdxs[j]*3+2]*3 + axis]);
            float b = ScalarMath.min(vertices[faces[faceIdxs[end]*3]*3 + axis], vertices[faces[faceIdxs[end]*3+1]*3 + axis], vertices[faces[faceIdxs[end]*3+2]*3 + axis]);

            if(a < b)
            {
                i++;

                int temp = faceIdxs[i];
                faceIdxs[i] = faceIdxs[j];
                faceIdxs[j] = temp;
            }
        }

        int temp = faceIdxs[i+1];
        faceIdxs[i+1] = faceIdxs[end];
        faceIdxs[end] = temp;

        return i+1;
    }
}
