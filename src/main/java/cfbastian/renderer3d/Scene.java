package cfbastian.renderer3d;


import cfbastian.renderer3d.bodies.Mesh;

import java.util.ArrayList;

public class Scene {
    ArrayList<Mesh> meshes = new ArrayList<>();

    public void addMesh(Mesh m)
    {
        meshes.add(m);
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
}
