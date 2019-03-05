package org.opentripplanner.index;


import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.opentripplanner.model.Brand;
import org.opentripplanner.model.FeedScopedId;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

public class BrandIndex {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(BrandIndex.class);

	private final LinkedHashMap<String, String> brandForOperatorIdPattern = new LinkedHashMap<>();

    private final String routerConfig;

	public BrandIndex(String _routerConfig) {
		routerConfig = _routerConfig;
	}
		
	public void buildBrandIndex() {
		/*
		 * Comentarios: El orden de la siguiente lista es importante: 
		 * 
		 * 1) Los patrones más restrictivos (largos) se han de introducir al principo 
		 *    de la lista ("2:1.99" es más largo que "2:1").
		 * 
		 * 2) El úlitmo elemento es el elemento por defecto. Si no se quieren introducir 
		 *    todos los valores. 
		 */
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		try {
			if (routerConfig != null) {
				
				JsonNode brandsJson = mapper.readTree(routerConfig).get("brands");
				
				if (brandsJson != null) {
					Collection<Brand> brands;
					
					brands = Arrays.asList(mapper.readValue(brandsJson.toString(), Brand[].class));
					
					for (Brand brand : brands) {
						String[] patterns = brand.getPattern().split(",");
						for ( String pattern : patterns ) {
							brandForOperatorIdPattern.put(pattern, brand.getId());
						}
					}
					LOG.info("Loaded brands from routing parameters from JSON:" + brandForOperatorIdPattern.values().stream().distinct().collect(Collectors.toList()));
				}
			}
			
		} catch (Exception e) {
			LOG.error("Error loading brands: " + e.getMessage());
		}
		
	}

	public String getBrand(FeedScopedId feedScopeId) {
		
		String brand = null;
		
		String id  = feedScopeId.getId();
		
		while(id.length() >= 2){
		
			if ( brandForOperatorIdPattern.containsKey(id) ){
				brand = brandForOperatorIdPattern.get(id);
				break;
			}else{
				id = id.substring(0, id.length()-1);
			}
			
		};
		
		if ( brand == null ){
			brand = "none";
		}
		
		return brand;
		
	}

}
