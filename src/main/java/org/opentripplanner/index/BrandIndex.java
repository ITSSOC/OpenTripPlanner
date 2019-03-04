package org.opentripplanner.index;


import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.opentripplanner.model.Brand;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.standalone.OTPMain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BrandIndex {

	//private final Map<List<String>, String> brandForOperatorIdPattern = Maps.newHashMap();
	
	private static final LinkedHashMap<String, String> brandForOperatorIdPattern = new LinkedHashMap<>();

    public static final String BRANDS_FILENAME = "brands.json";

    static {
		
		/*
		 * Comentarios: El orden de la siguiente lista es importante: 
		 * 
		 * 1) Los patrones más restrictivos (largos) se han de introducir al principo 
		 *    de la lista ("2:1.99" es más largo que "2:1").
		 * 
		 * 2) El úlitmo elemento es el elemento por defecto. Si no se quieren introducir 
		 *    todos los valores. 
		 */
//		JSONObject brandJson = new JSONObject();
//
//        brandJson.put("2:1.99", "Funicular");
//        brandJson.put("2:1,2:E", "TMBMetro");
//        brandJson.put("3:", "FGC");
//        brandJson.put("TRAMBAIX:,TRAMBESÒS:", "Tram");
//        brandJson.put("1071VC:", "Rodalies");
//        brandJson.put("2:2,1:,22:,25:,28:,34:,35:,36:,44:,47:", "BusMetropolita");
//        brandJson.put("AA:,AC:,AD:,AE:,AF:,AG:,AH:,AK:,AL:,AQ:,AT:,AV:,AW:,AY:,BA:,BB:,BC:,BD:,BE:,BJ:,BR:,BS:,BT:,BU:,BY:,CC:,CD:,CF:,CG:,CI:,CJ:,CK:,CS:,CU:,CW:,CY:,DA:,DD:,DF:,DH:,DI:,DM:,DP:,DQ:,DV:,DY:,EA:,EC:,ED:,EI:,EJ:,EL:,EM:,EN:,EO:,EP:,EQ:,ER:,ES:,ET:,EU:,EW:,EX:,EY:,EZ:,FA:,FB:,FC:,FD:,FE:,FG:,FH:,FI:,FJ:,FK:,FL:,FM:,FN:,FO:,FP:,FQ:,FR:,FS:,FT:,FV:,FW:,FX:,FY:,FZ:,GA:,GB:,GC:,GD:,GE:,GF:,GG:,GI:,GJ:,GK:,GL:,GM:,GN:,GW:,GX:,GZ:,HC:,HD:,HE:,HF:,HG:,L:,M:,O:,Q:,R:,Y:,Z:", "BusInterurba");

        ObjectMapper mapper = new ObjectMapper();

		try {
	        JsonNode brandsJson = OTPMain.loadJson(new File("C:\\Users\\Emovilitat\\Documents\\dvlp\\otp\\graph\\default", BRANDS_FILENAME));

	        Collection<Brand> brands;
	        
			brands = Arrays.asList(mapper.readValue(brandsJson.toString(), Brand[].class));
			
			for (Brand brand : brands) {
				String[] patterns = brand.getPattern().split(",");
				for ( String pattern : patterns ) {
					brandForOperatorIdPattern.put(pattern, brand.getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static String getBrand(FeedScopedId feedScopeId) {
		
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
			brand = "BusInterurba";
		}
		
		return brand;
		
	}

	
	public static void main(String[] args) {
		
		BrandIndex brandIndex = new BrandIndex();
		
		String result = brandIndex.getBrand(new FeedScopedId("ATM", "ROD_50T0057R7"));
		
		System.out.println("Brand = "+ result);

	}
	
}
