package cfbastian.renderer3d.bodies;

import cfbastian.renderer3d.math.ArrayOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class BoundingVolumeHierarchy {
    private LinkedList<BoundingVolumeHierarchy> childNodes;
    private AxisAlignedBoundingBox aabb;
    private int[] faces;

    public BoundingVolumeHierarchy(int[] faces, float[] vertices, int splitAxis, int splits, int maxDepth)
    {
        childNodes = new LinkedList<>();
        splitAxis = splitAxis > 2 ? 0 : splitAxis;

        faces = ArrayOperations.sortTris(faces, vertices, splitAxis);
        aabb = new AxisAlignedBoundingBox(faces, vertices);

        int numFaces = faces.length/3;

        if(0 < maxDepth) {
            for (int i = 0; i < splits + 1; i++) {
                int width = numFaces / (splits + 1);
                int end = i == splits? faces.length : width*3*(i+1);
                if(width*3*i != end) {
                    childNodes.add(new BoundingVolumeHierarchy(Arrays.copyOfRange(faces, width * 3 * i, end), vertices, splitAxis + 1, splits, maxDepth, 1));
                }
            }
        }
        else if (0 == maxDepth) this.faces = faces;
    }

    private BoundingVolumeHierarchy(int[] faces, float[] vertices, int splitAxis, int splits, int maxDepth, int depth)
    {
        childNodes = new LinkedList<>();
        splitAxis = splitAxis > 2 ? 0 : splitAxis;

        faces = ArrayOperations.sortTris(faces, vertices, splitAxis);
        aabb = new AxisAlignedBoundingBox(faces, vertices);

        int numFaces = faces.length/3;

        if(depth < maxDepth) {
            for (int i = 0; i < splits + 1; i++) {
                int width = numFaces / (splits + 1);
                int end = i == splits? faces.length : width*3*(i+1);
                if(width*3*i != end) {
                    childNodes.add(new BoundingVolumeHierarchy(Arrays.copyOfRange(faces, width * 3 * i, end), vertices, splitAxis + 1, splits, maxDepth, depth + 1));
                }
            }
        }
        else if (depth == maxDepth) this.faces = faces;
    }

    public AxisAlignedBoundingBox getAabb() {
        return aabb;
    }

    public BoundingVolumeHierarchy[] hitLeaf(float[] cameraPos, float[] ray, float padding)
    {
        ArrayList<BoundingVolumeHierarchy> leaves = new ArrayList<>();
        int[] depth = new int[1];

        if(aabb.hitAABB(cameraPos, ray, padding))
        {
            if(childNodes.isEmpty()) leaves.add(this);
            else for (BoundingVolumeHierarchy child : childNodes) child.hitLeaf(cameraPos, ray, padding, leaves, depth);
        }

        BoundingVolumeHierarchy[] leavesArr = new BoundingVolumeHierarchy[leaves.size()];
        for (int i = 0; i < leaves.size(); i++) leavesArr[i] = leaves.get(i);
        return leavesArr;
    }

    private void hitLeaf(float[] cameraPos, float[] ray, float padding, ArrayList<BoundingVolumeHierarchy> leaves, int[] depth)
    {
        depth[0]++;
        if(aabb.hitAABB(cameraPos, ray, padding))
        {
            if(childNodes.isEmpty()) leaves.add(this);
            else for (BoundingVolumeHierarchy child : childNodes) child.hitLeaf(cameraPos, ray, padding);
        }
    }

    public BoundingVolumeHierarchy[] getLeaves()
    {
        ArrayList<BoundingVolumeHierarchy> leaves = new ArrayList<>();
        if(childNodes.isEmpty()) leaves.add(this);
        else for (BoundingVolumeHierarchy child : childNodes) child.getLeaves(leaves);

        BoundingVolumeHierarchy[] leavesArr = new BoundingVolumeHierarchy[leaves.size()];
        for (int i = 0; i < leaves.size(); i++) leavesArr[i] = leaves.get(i);
        return leavesArr;
    }

    private void getLeaves(ArrayList<BoundingVolumeHierarchy> leaves)
    {
        if(childNodes.isEmpty()) leaves.add(this);
        else for (BoundingVolumeHierarchy child : childNodes) child.getLeaves(leaves);
    }

    public LinkedList<BoundingVolumeHierarchy> getChildNodes() {
        return childNodes;
    }

    public int[] getFaces() {
        return faces;
    }
}
