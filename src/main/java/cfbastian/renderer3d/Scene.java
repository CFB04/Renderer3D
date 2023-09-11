package cfbastian.renderer3d;


import cfbastian.renderer3d.bodies.Mesh;
import cfbastian.renderer3d.bodies.TestSphere;

import java.util.ArrayList;
import java.util.Arrays;

public class Scene {
    ArrayList<Mesh> meshes = new ArrayList<>();
    ArrayList<TestSphere> spheres = new ArrayList<>(); //TODO REMOVE, THIS IS FOR EASE OF TESTING RAY TRACING

    private double[] meshesVertices;
    private int[] meshesTris;

    private double[] spheresCoords;
    private double[] spheresRadii;

    public void addMesh(Mesh m)
    {
        meshes.add(m);
    }

    public void addSphere(TestSphere s) // TODO REMOVE
    {
        spheres.add(s);
    }

    public synchronized void removeMesh(String key)
    {
        int index = -1;
        for (int i = 0; i < meshes.size(); i++) if(key.equals(meshes.get(i).getKey())) index = i;

        if(index != -1) meshes.remove(index);
        else System.err.println("Mesh with key \"" + key + "\" not found");
    }

    public void calculateMeshArrays()
    {
        ArrayList<Double> meshesPoints = new ArrayList<>();
        ArrayList<Integer> meshesTris = new ArrayList<>();
        for (Mesh m : meshes) {
            meshesPoints.addAll(Arrays.stream(m.getAbsoluteVertices()).boxed().toList());
            meshesTris.addAll(Arrays.stream(m.getTris()).boxed().toList());
        }

        this.meshesVertices = meshesPoints.stream().mapToDouble(Double::doubleValue).toArray();
        this.meshesTris = meshesTris.stream().mapToInt(Integer::intValue).toArray();
    }

    public void calculateSphereArrays() // TODO DELETE
    {
        ArrayList<Double> spheresCoords = new ArrayList<>();
        ArrayList<Double> spheresRadii = new ArrayList<>();
        for (TestSphere ts : spheres) {
            spheresCoords.addAll(Arrays.stream(ts.pos).boxed().toList());
            spheresRadii.add(ts.radius);
        }

        this.spheresCoords = spheresCoords.stream().mapToDouble(Double::doubleValue).toArray();
        this.spheresRadii = spheresRadii.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public double[] getMeshesVertices() {
        return meshesVertices;
    }

    public int[] getMeshesTris() {
        return meshesTris;
    }

    public double[] getSpheresCoords() { // TODO DELETE
        return spheresCoords;
    }

    public double[] getSpheresRadii() { // TODO DELETE
        return spheresRadii;
    }
}
