package persistent.collctions.graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.junit.*;

import org.junit.Test;

import graph.*;

public class GraphTest {

	@Test
	public void test() throws IOException {
		int testInt = 8;
		Graph<Integer> g = new Graph<Integer>(new InMemoryPersistentArray("", GraphVertex.SIZE + Integer.BYTES, 0), new InMemoryPersistentArray("", GraphEdge.SIZE, 0), new IntFactory());
		GraphVertex v = g.addVertex(testInt);
		
		Assert.assertTrue(v.getValue().equals(testInt));
		Assert.assertTrue(g.getVertexIndex(v.getIndex()).getValue().equals(testInt));
		
		GraphVertex two = g.addVertex(2);
		
		GraphEdge e = g.addEdge(v, two);
		
		Assert.assertTrue(v.getFirstEdge() == e.getIndex());
		Assert.assertTrue(e.getDestination() == two.getIndex());
		Assert.assertTrue(e.getSource() == v.getIndex());
		Assert.assertTrue(g.getEdgeIndex(e.getIndex()).getDestination() == two.getIndex());
		Assert.assertTrue(g.getEdgeIndex(e.getIndex()).getSource() == v.getIndex());
	
		int count = 0;
		Iterator<GraphVertex<Integer>> vertices = g.getVertexIterator();
		while(vertices.hasNext()) {
			GraphVertex<Integer> cur = vertices.next();
			count++;
			if(cur.getValue() != testInt && cur.getValue() != 2) {
				fail();
			}
		}
		Assert.assertEquals(2, count);
		
		count = 0;
		Iterator<GraphEdge> edges = g.getGraphEdgeIterator();
		while(edges.hasNext()) {
			GraphEdge cur = edges.next();
			count++;
			if(cur.getSource() != v.getIndex() || cur.getDestination() != two.getIndex()) {
				fail();
			}
		}
		Assert.assertEquals(1, count);
		
		g.deleteVertex(two);
		
		Assert.assertTrue(g.getVertexIndex(v.getIndex()).getFirstEdge() == -1);
		
		vertices = g.getVertexIterator();
		count = 0;
		while(vertices.hasNext()) {
			GraphVertex<Integer> cur = vertices.next();
			count++;
			if(cur.getValue() != testInt) {
				fail();
			}
		}
		Assert.assertEquals(1, count);
	}
	
	private class IntFactory implements PersistentFactory<Integer> {

		@Override
		public Integer deserialize(ByteBuffer buffer) {
			return buffer.getInt();
		}

		@Override
		public void serialize(ByteBuffer buffer, Integer value) {
			buffer.putInt(value);
		}

		@Override
		public int getSize() {
			return Integer.BYTES;
		}
		
	}

}
