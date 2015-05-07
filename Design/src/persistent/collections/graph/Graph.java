package persistent.collctions.graph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;


public class Graph<E> {
	
	private PersistentArray vertices;
	private PersistentArray edges;
	private PersistentFactory<E> factory;
	
	/**
	 * create a new graph using 2 persistent arrays
	 * @param vertices This pa will store the vertices with the values stored in them
	 * @param edges This pa will store the data for edges between vertices
	 */
	public Graph(PersistentArray vertices, PersistentArray edges, PersistentFactory<E> factory) {
		//throw an error if either PA doesn't exist
		if(vertices == null || edges == null || factory == null) {
			throw new NullPointerException();
		}
		
		//store the persistent arrays
		this.vertices = vertices;
		this.edges = edges;
		this.factory = factory;
	}
	
	/**
	 * create a new vertex, adding it to the pa
	 * @param value the value to be stored in the vertex
	 * @return the created vertex
	 */
	public GraphVertex<E> addVertex(E value)  throws IOException {
		//create the new vertex with the desired value
		GraphVertex<E> vert = new GraphVertex<E>(factory);
		vert.setValue(value);
		vert.setFirstEdge(-1);
		
		//allocate space for the vertex in the vertices pa
		long index = vertices.allocate();
		//set the index of the vertex and put it in the pa
		vert.setIndex(index);
		saveVertex(vert);
		//return the new vertex
		return vert;
	}
	
	/**
	 * create a new edge, going from the source vertex to the destination vertex
	 * @param source 
	 * @param destination
	 * @return the created edge
	 */
	public GraphEdge addEdge(GraphVertex<E> source, GraphVertex<E> destination) throws IOException {
		//allocate space for the edge
		long index = edges.allocate();
		//create the new edge and put it in the edges pa
		GraphEdge edge = new GraphEdge(index, source.getIndex(), destination.getIndex());
		edge.setIndex(index);
		edge.setNextEdge(-1);
		saveEdge(edge);
		
		//if the source has no other edges
		if(source.getFirstEdge() == -1) {
			//store this edge in the source
			source.setFirstEdge(index);
			saveVertex(source);
		} else { //else if the source has at least one other edge
			//get the first edge
			GraphEdge e = new GraphEdge();
			e.load(edges.get(source.getFirstEdge()));
			//while there are more edges with this source
			while(e.getNextEdge() != -1) {
				//get the next one
				e = new GraphEdge();
				e.load(edges.get(e.getNextEdge()));
			}
			//set this edge as the last edge's next edge and save edge
			e.setNextEdge(index);
			saveEdge(e);
		}
		return edge;
	}
	
	public void saveEdge(GraphEdge e)  throws IOException {
		//make a buffer of the correct size
		ByteBuffer buffer = ByteBuffer.allocate(e.getSize());
		//have the edge serialize itself
		e.serialize(buffer);
		//put the edge in the pa
		edges.put(e.getIndex(), buffer);
		
	}
	
	public void saveVertex(GraphVertex<E> v)  throws IOException {
		//make a buffer of the correct size
		ByteBuffer buffer = ByteBuffer.allocate(v.getSize());
		//have the vertex serialize itself
		v.serialize(buffer);
		//put the vertex in the pa
		vertices.put(v.getIndex(), buffer);
	}
	
	/**
	 * 
	 * @param index
	 * @return the vertex stored at the index
	 */
	public GraphVertex<E> getVertexIndex(long index) throws IOException {
		//return the graph vertex from the pa
		GraphVertex<E> vert = new GraphVertex<E>(factory);
		vert.load(vertices.get(index));
		return vert;
	}
	
	/**
	 * 
	 * @param index
	 * @return the edge stored at the index
	 */
	public GraphEdge getEdgeIndex(long index) throws IOException {
		//return the graph edge from the pa
		GraphEdge e = new GraphEdge();
		e.load(edges.get(index));
		return e;
	}
	
	/**
	 * delete the indicated vertex
	 * will also delete all edges which either start at or end at this vertex
	 * @param vertex
	 */
	public void deleteVertex(GraphVertex<E> vertex) throws IOException {
		
		//delete all of the edges which start or end at this vertex
		//get a graphEdge iterator
		Iterator<GraphEdge> it = getGraphEdgeIterator();
		//while there are more edges
		while(it.hasNext()) {
			//get the current edge 
			GraphEdge current = it.next();
			//if the current edge starts or ends at the vertex to be deleted
			if(current.getDestination() == vertex.getIndex() || current.getSource() == vertex.getIndex()) {
				//delete the edge
				it.remove();
			}
		}
		
		//get the index the vertex is at
		long oldInd = vertex.getIndex();
		//set the index to -1 (to indicate it was deleted
		vertex.setIndex(-1);
		//save the vertex
		//make a buffer of the correct size
		ByteBuffer buffer = ByteBuffer.allocate(vertex.getSize());
		//have the edge serialize itself
		vertex.serialize(buffer);
		//put the edge in the pa
		vertices.put(oldInd, buffer);

		//delete the vertex from the vertices pa
		vertices.delete(oldInd);
	}
	
	/**
	 * delete the indicated vertex
	 * will also delete all edges which either start at or end at this vertex
	 * @param vertex
	 */
	public void deleteVertex(long index) throws IOException {
		//do the other delete vertex method with the actual vertex at the index
		deleteVertex(getVertexIndex(index));
	}
	
	/**
	 * delete the indicated edge
	 * will also delete the pointer to it in its source vertex
	 * @param e
	 */
	public void deleteEdge(GraphEdge e) throws IOException {
		try {
			deleteEdge(getVertexIndex(e.getSource()), getVertexIndex(e.getDestination()));
		} catch(NonExistentEdgeException ex){ 
			//shouldn't happen...
		}
	}
	
	/**
	 * delete the indicated edge
	 * will also delete the pointer to it in its source vertex
	 * @param from the source vertex
	 * @param to the destination vertex
	 * @throws NonExistentEdgeException
	 */
	public void deleteEdge(GraphVertex<E> from, GraphVertex<E> to) throws NonExistentEdgeException, IOException {
		//get the first edge from the "from" vertex
		GraphEdge current = getEdgeIndex(from.getFirstEdge());
		//if this is the edge to delete
		if(current.getDestination() == to.getIndex()) {
			//set the first edge to the to-be-deleted edge's next edge
			from.setFirstEdge(current.getNextEdge());
			saveVertex(from);
		} else {
			//create a last graphedge object
			GraphEdge last;
			//do
			do {
				//set last to current
				last = current;
				//set current to the value of current's next edge
				current = getEdgeIndex(current.getNextEdge());
			//while the current edge is not the one we want to delete and there are still more edges
			} while(current.getDestination() != to.getIndex() && current.getNextEdge() != -1);
			
			//if this is not the edge to be deleted 
			if(current.getDestination() != to.getIndex()) {
				//throw a new nonexistentedgeexception - because the edge to be deleted doesn't exist
				throw new NonExistentEdgeException();
			}
			
			//set last's next edge to current's next edge
			last.setNextEdge(current.getNextEdge());
			saveEdge(last);
		}
		//delete the edge
		
		long oldInd = current.getIndex();
		//nullify the index
		current.setIndex(-1);
		//save the nullified edge
		//make a buffer of the correct size
		ByteBuffer buffer = ByteBuffer.allocate(current.getSize());
		//have the edge serialize itself
		current.serialize(buffer);
		//put the edge in the pa
		edges.put(oldInd, buffer);
		
		
		//actually delete the value (because Blake refuses to delete any content himself)
		edges.delete(oldInd);
	}
	
	/**
	 * 
	 * @return an iterator of all vertices in the graph
	 */
	public Iterator<GraphVertex<E>> getVertexIterator() {
		//return a new vertex iterator
		return new VertexIterator();
	}
	
	/**
	 * 
	 * @return an iterator of all edges in the graph
	 */
	public Iterator<GraphEdge> getGraphEdgeIterator() {
		//return a new edge iterator
		return new EdgeIterator();
	}
	
	public class VertexIterator implements Iterator<GraphVertex<E>> {
		private long position = 0;

		/**
		 * returns whether or not there are more vertices in the graph without advancing the position
		 */
		@Override
		public boolean hasNext() {
			//temp position
			long temp = position;
			GraphVertex<E> v = null;
			long count = vertices.getRecordCount();
			try {
				if(position < count) {
					v = getVertexIndex(position);
				}
				//while there are still indices in vertices and the current vertex's index value == -1 (meaning it exists, but was deleted)
				while(temp < count && (v.getIndex() == -1)) {
					//get the next index at temp position++
					v = getVertexIndex(temp);
					temp++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//return whether there are still indices in vertices
			return temp < count;
		}

		/**
		 * returns the next value
		 */
		@Override
		public GraphVertex<E> next() {
			GraphVertex<E> v = null;
			long count = vertices.getRecordCount();
			try {
				v = getVertexIndex(position);
				//while there are still indices and the current index's index value == -1
				while(position < count && v.getIndex() == -1) {
					//get the next index at position++
					v = getVertexIndex(position);
					position++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			position++;
			//return the current index value
			return v;
		}

		/**
		 * DELETES THE VERTEX FROM THE GRAPH
		 * not just from the iterator
		 */
		@Override
		public void remove() {
			//delete the value at the current index
			try {
				deleteVertex(getVertexIndex(position));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public class EdgeIterator implements Iterator<GraphEdge> {
		private long position = 0;
		
		/**
		 * returns whether or not there are more edge values in the graph
		 */
		@Override
		public boolean hasNext() {
			//temp position
			long temp = position;
			GraphEdge e = null;
			long count = edges.getRecordCount();
			try {
				if(position < count) {
					e = getEdgeIndex(position);
				}
				//while there are still indices in edges and the current edge's index value == -1 (meaning it exists, but was deleted)
				while(temp < count && e.getIndex() == -1) {
					//get the next index at temp position++
					e = getEdgeIndex(temp);
					temp++;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			//return whether there are still indices in edges
			return temp < count;
		}

		/**
		 * returns the next value
		 */
		@Override
		public GraphEdge next() {
			GraphEdge e = null;
			long count = vertices.getRecordCount();
			try {
				e = getEdgeIndex(position);
				//while there are still indices and the current index's index value == -1
				while(position < count && e.getIndex() == -1) {
					//get the next index at position++
					e = getEdgeIndex(position);
					position++;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			position++;
			//return the current index value
			return e;
		}

		/**
		 * DELETES THE EDGE FROM THE GRAPH
		 * not just from the iterator
		 */
		@Override
		public void remove() {
			//delete the value at the current index
			try {
				deleteEdge(getEdgeIndex(position - 1));
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
