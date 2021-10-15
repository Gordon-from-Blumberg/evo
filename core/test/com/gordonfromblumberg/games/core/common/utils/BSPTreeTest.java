package com.gordonfromblumberg.games.core.common.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.gordonfromblumberg.games.core.common.model.GameObject;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class BSPTreeTest {
    private int nextId = 1;

    @Test
    void checkLeafCount() {
        BSPTree tree = new BSPTree(0, 0, 100, 100, 2);
        assertEquals(4, tree.leafCount());

        tree = new BSPTree(0, 0, 100, 100);
        assertEquals(8, tree.leafCount());
    }

    @Test
    void eachObjectInsideOwnZone() {
        BSPTree tree = new BSPTree(0, 0, 100, 100, 2);
        tree.addObject(createGameObject(25, 25));
        tree.addObject(createGameObject(25, 75));
        tree.addObject(createGameObject(75, 25));
        tree.addObject(createGameObject(75, 75));

        Array<IntArray> result = countIterations(tree);
        Array<IntArray> expectedResult = builder()
                .add(0).add(0).add(0).add(0)
                .build();

        assertIterations(expectedResult, result);
    }

    @Test
    void oneObjectOnBorder() {
        BSPTree tree = new BSPTree(0, 0, 100, 100, 2);
        tree.addObject(createGameObject(25, 25));
        tree.addObject(createGameObject(25, 75));
        tree.addObject(createGameObject(75, 25));
        tree.addObject(createGameObject(75, 50));

        Array<IntArray> result = countIterations(tree);

        Array<IntArray> expectedResult = builder()
                .add(0).add(0).add(1, 0).add(0)
                .build();

        assertIterations(expectedResult, result);
    }

    @Test
    void twoObjectsOnBorder() {
        BSPTree tree = new BSPTree(0, 0, 100, 100, 2);
        tree.addObject(createGameObject(25, 50));
        tree.addObject(createGameObject(25, 75));
        tree.addObject(createGameObject(75, 25));
        tree.addObject(createGameObject(75, 50));

        Array<IntArray> result = countIterations(tree);

        Array<IntArray> expectedResult = builder()
                .add(0).add(1, 0).add(1, 0).add(0)
                .build();

        assertIterations(expectedResult, result);
    }

    @Test
    void twoObjectsOnTheSameBorder() {
        BSPTree tree = new BSPTree(0, 0, 100, 100, 2);
        tree.addObject(createGameObject(25, 25));
        tree.addObject(createGameObject(25, 75));
        tree.addObject(createGameObject(75, 25));
        tree.addObject(createGameObject(85, 50));
        tree.addObject(createGameObject(75, 50));

        Array<IntArray> result = countIterations(tree);

        Array<IntArray> expectedResult = builder()
                .add(0).add(0).add(2, 1, 0).add(1, 0)
                .build();

        assertIterations(expectedResult, result);
    }

    @Test
    void oneObjectInsideRoot() {
        BSPTree tree = new BSPTree(0, 0, 100, 100, 2);
        tree.addObject(createGameObject(25, 25));
        tree.addObject(createGameObject(25, 75));
        tree.addObject(createGameObject(75, 25));
        tree.addObject(createGameObject(75, 75));
        tree.addObject(createGameObject(50, 50));

        Array<IntArray> result = countIterations(tree);
        Array<IntArray> expectedResult = builder()
                .add(1, 0).add(1, 0).add(1, 0).add(1, 0)
                .build();

        assertIterations(expectedResult, result);
    }

    @Test
    void compareDifferentLevels() {
        Array<GameObject> objects = new Array<>();
        objects.add(createGameObject(16, 25));
        objects.add(createGameObject(16, 50));
        objects.add(createGameObject(16, 75));
        objects.add(createGameObject(33, 25));
        objects.add(createGameObject(33, 50));
        objects.add(createGameObject(33, 75));

        // 2 parties
        BSPTree tree = new BSPTree(0, 0, 100, 100, 1);
        for (GameObject go : objects)
            tree.addObject(go);

        Array<IntArray> result = countIterations(tree);
        Array<IntArray> expectedResult = builder()
                .add(5, 4, 3, 2, 1, 0).add()    // 5+4+3+2+1 = 15 iterations
                .build();
        assertIterations(expectedResult, result);

        // 4 parties
        tree = new BSPTree(0, 0, 100, 100, 2);
        for (GameObject go : objects)
            tree.addObject(go);

        result = countIterations(tree);
        expectedResult = builder()
                .add(3, 2, 1, 0).add(3, 2, 1, 0).add().add()    // 3+2+1 + 3+2+1 = 12 iterations
                .build();
        assertIterations(expectedResult, result);

        // 8 parties
        tree = new BSPTree(0, 0, 100, 100, 3);
        for (GameObject go : objects)
            tree.addObject(go);

        result = countIterations(tree);
        expectedResult = builder()
                .add(2, 1, 0).add(2, 1, 0).add(2, 1, 0).add(2, 1, 0)  // 2+1 + 2+1 + 2+1 + 2+1 = 12 iterations
                .add().add().add().add()
                .build();
        assertIterations(expectedResult, result);
    }

    private TestObject createGameObject(float x, float y) {
        final TestObject object = new TestObject();
        object.setPosition(x, y);
        object.setSize(5f, 5f);
        object.setId(nextId++);
        return object;
    }

    private Array<IntArray> countIterations(BSPTree tree) {
        final Array<IntArray> result = new Array<>();
        while (tree.hasNext()) {
            IntArray internalResult = new IntArray();
            final Iterator<GameObject> iterator = tree.next();
            final Iterator<GameObject> internalIterator = tree.internalIterator();
            while (iterator.hasNext()) {
                final GameObject gameObject = iterator.next();
                int count = 0;
                while (internalIterator.hasNext()) {
                    final GameObject internalGameObject = internalIterator.next();
                    count++;
                }
                internalResult.add(count);
            }
            result.add(internalResult);
        }

        return result;
    }

    private void assertIterations(Array<IntArray> expected, Array<IntArray> actual) {
        assertEquals(expected.size, actual.size, String.format("Tree should have %d leafs", expected.size));

        for (int i = 0; i < expected.size; i++) {
            final IntArray expectedLeaf = expected.get(i);
            final IntArray actualLeaf = actual.get(i);
            assertEquals(expectedLeaf.size, actualLeaf.size, String.format("From leaf #%d should be %d iterations", i, 1));

            for (int j = 0; j < expectedLeaf.size; j++) {
                assertEquals(expectedLeaf.get(j), actualLeaf.get(j), String.format("Inside iteration #%d of leaf #%d should be %d internal iterations",
                        j, i, expectedLeaf.get(j)));
            }
        }
    }

    private static class TestObject extends GameObject {
    }

    private static IterationCountBuilder builder() {
        return new IterationCountBuilder();
    }

    private static class IterationCountBuilder {
        private final Array<IntArray> result = new Array<>();

        IterationCountBuilder add(int... iterations) {
            result.add(new IntArray(iterations));
            return this;
        }

        Array<IntArray> build() {
            return result;
        }
    }
}
