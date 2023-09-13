package cfbastian.renderer3d;


import cfbastian.renderer3d.bodies.Mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Scene {
    private ArrayList<Mesh> meshes = new ArrayList<>();

    public void addMesh(Mesh mesh)
    {
        for (Mesh m: meshes) {
            if(mesh.getKey().equals(m.getKey()))
            {
                System.err.print("Key taken");
                return;
            }
        }
        meshes.add(mesh);
    }

    public Mesh getMesh(String key)
    {
        int index = -1;
        for (int i = 0; i < meshes.size(); i++) if(key.equals(meshes.get(i).getKey())) index = i;

        if(index != -1) return meshes.get(index);
        System.err.println("Mesh with key \"" + key + "\" not found");
        return null;
    }

    public synchronized void removeMesh(String key)
    {
        int index = -1;
        for (int i = 0; i < meshes.size(); i++) if(key.equals(meshes.get(i).getKey())) index = i;

        if(index != -1) meshes.remove(index);
        else System.err.println("Mesh with key \"" + key + "\" not found");
    }

    public double[] getAllVertices()
    {
        ArrayList<Double> vertices = new ArrayList<>();
        for (Mesh m : meshes) vertices.addAll(DoubleStream.of(m.getAbsoluteVertices()).boxed().toList());
        return vertices.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public int[] getAllFaces()
    {
        ArrayList<Integer> faces = new ArrayList<>();

        int i = 0;
        for (int j = 0; j < meshes.size(); j++)
        {
            int[] facesArr = Arrays.copyOf(meshes.get(j).getFaces(), meshes.get(j).getFaces().length);
            for (int k = 0; k < facesArr.length; k++) facesArr[k] += i;
            faces.addAll(IntStream.of(facesArr).boxed().toList());
            i += meshes.get(j).getAbsoluteVertices().length/3;
        }
        return faces.stream().mapToInt(Integer::intValue).toArray();
    }

    public ArrayList<Mesh> getMeshes() {
        return meshes;
    }
}
