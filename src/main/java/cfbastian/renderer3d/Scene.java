package cfbastian.renderer3d;

import cfbastian.renderer3d.entities.Entity;
import cfbastian.renderer3d.entities.lights.Light;

import java.util.ArrayList;

public class Scene {
    private ArrayList<Entity> entityList = new ArrayList<>();
    private ArrayList<Light> lightList = new ArrayList<>();

    public Scene(ArrayList<Entity> entityList, ArrayList<Light> lightList) {
        this.entityList = entityList;
        this.lightList = lightList;
    }

    public Scene() {}

    public void addToEntityList(Entity e) {
        entityList.add(e);
    }

    public void removeFromEntityList(int i) {
        entityList.remove(i);
    }

    public void addToLightList(Light i) {
        lightList.add(i);
    }

    public void removeFromLightList(int i) {
        entityList.remove(i);
    }

    public int findEntityIndex(String key)
    {
        for (int i = 0; i < entityList.size(); i++) if(key.equals(entityList.get(i).getKey())) return i;
        return -1;
    }

    public int findLightIndex(String key)
    {
        for (int i = 0; i < lightList.size(); i++) if(key.equals(lightList.get(i).getKey())) return i;
        return -1;
    }

    public Entity getFromEntityList(int i)
    {
        return entityList.get(i);
    }

    public Light getFromLightList(int i)
    {
        return lightList.get(i);
    }

    public ArrayList<Entity> getEntityList() {
        return entityList;
    }

    public ArrayList<Light> getLightList() {
        return lightList;
    }

    public String[] getKeys()
    {
        String[] s = new String[entityList.size() + lightList.size()];

        for (int i = 0; i < entityList.size(); i++) s[i] = entityList.get(i).getKey();
        for (int i = 0; i < lightList.size(); i++) s[i + entityList.size()] = lightList.get(i).getKey();

        return s;
    }
}
