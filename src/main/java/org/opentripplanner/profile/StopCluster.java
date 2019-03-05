package org.opentripplanner.profile;

import java.util.List;
import java.util.stream.Collectors;

import org.opentripplanner.model.Route;
import org.opentripplanner.model.Stop;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.graph.GraphIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Groups stops by geographic proximity and name similarity.
 * This will at least half the number of distinct stop places. In profile routing this means a lot less branching
 * and a lot less transfers to consider.
 *
 */
public class StopCluster {

    private static final Logger LOG = LoggerFactory.getLogger(StopCluster.class);

    public final String id;
    public final String name;
    public double lon;
    public double lat;
    public String code; 
    public final List<Stop> children = Lists.newArrayList();
    public List<String> brands = Lists.newArrayList();
    public int wheelchairBoarding;
    public TraverseMode vehicleMode;
    public List<Route> routes = Lists.newArrayList();
    public List<TripPattern> patterns = Lists.newArrayList();
    
    public StopCluster(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setCoordinates(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public void computeCluster(GraphIndex index) {
        double lonSum = 0, latSum = 0;
        code = null;
        
        if ( !children.isEmpty() ) {
        	
        	for (Stop stop : children) {
        		lonSum += stop.getLon();
        		latSum += stop.getLat();
        		
        		for ( String brand : stop.getBrands()) {
        			if ( !brands.contains(brand) ) brands.add(brand);
        		}
        		
        		routes.addAll(index.patternsForStop.get(stop)
                	.stream()
                	.map(pattern -> pattern.route)
                	.distinct()
                	.collect(Collectors.toList()));
        		
        		patterns.addAll(index.patternsForStop.get(stop));
        		
        		if ( code == null && stop.getCode() != null ) code = stop.getCode();
        	}
        	lon = lonSum / children.size();
        	lat = latSum / children.size();
        	
        	wheelchairBoarding = children.get(0).getWheelchairBoarding();
			vehicleMode = index.getVehicleMode(children.get(0));
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
