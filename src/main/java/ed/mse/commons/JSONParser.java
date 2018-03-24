package ed.mse.commons;

import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Mohamad Nachabe
 *
 */
public class JSONParser extends Parser {

	public Graph<String, DefaultWeightedEdge> parse(String result) {
		Graph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Logger.getLogger().log("Generating Graph");
		try {
			JSONObject root = new JSONObject(result);
			JSONArray elements = root.getJSONArray("elements");
			HashMap<Long, MapNode> hash = new HashMap<Long, MapNode>(); 
			
			for(int i=0; i < elements.length(); i++) {
				JSONObject o = elements.getJSONObject(i);
				if(o.getString("type").equals("node")) {
					MapNode node = null;
					double longitude = o.getDouble("lon");
					double latitude = o.getDouble("lat");
					long id = o.getLong("id");
					node = new MapNode(id, latitude, longitude);
					hash.put(node.getId(), node);
				} else if(o.getString("type").equals("way")) {
					JSONArray nodes = o.getJSONArray("nodes");
					Long lastNodeId = nodes.getLong(0);
					g.addVertex(String.valueOf(lastNodeId));
					for(int j=1; j < nodes.length(); j++) {
						Long nodeId = nodes.getLong(j);
						g.addVertex(String.valueOf(nodeId));
						MapNode node1 = hash.get(nodeId), node2 = hash.get(lastNodeId);
						DefaultWeightedEdge e = g.addEdge(String.valueOf(node1.getId()), String.valueOf(node2.getId()));
						if(e == null) {
							continue;
						}
						double distance = MathUtils.haversine(node1.getLatitude(), node1.getLongitude(), node2.getLatitude(), node2.getLongitude());
						g.setEdgeWeight(e, distance);
						lastNodeId = nodeId;
					}
				}
			}
			
			Logger.getLogger().log("Graph generated successfully");
	
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return g;
	}	

}