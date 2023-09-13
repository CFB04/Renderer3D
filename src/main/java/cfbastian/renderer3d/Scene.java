package cfbastian.renderer3d;


import cfbastian.renderer3d.bodies.Mesh;

import java.util.ArrayList;
import java.util.Arrays;
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

    public float[] getAllVertices()
    {
        ArrayList<Float> vertices = new ArrayList<>();
        for (Mesh m : meshes) for (float f : m.getAbsoluteVertices()) vertices.add(f);
//        for (Mesh m : meshes) vertices.addAll(DoubleStream.of(m.getAbsoluteVertices()).boxed().toList());
        float[] verticesArr = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) verticesArr[i] = vertices.get(i);
        //return vertices.stream().mapToDouble(Double::doubleValue).toArray();
        return verticesArr;
    }

    public int[] getAllFaces()
    {
        ArrayList<Integer> faces = new ArrayList<>();

        int i = 0;
        for (Mesh mesh : meshes) {
            int[] facesArr = Arrays.copyOf(mesh.getFaces(), mesh.getFaces().length);
            for (int j = 0; j < facesArr.length; j++) facesArr[j] += i;
            faces.addAll(IntStream.of(facesArr).boxed().toList());
            i += mesh.getAbsoluteVertices().length / 3;
        }
        return faces.stream().mapToInt(Integer::intValue).toArray();
    }
}
