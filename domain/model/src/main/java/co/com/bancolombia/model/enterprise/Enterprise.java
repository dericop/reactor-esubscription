package co.com.bancolombia.model.enterprise;

import java.util.LinkedList;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Enterprise{
	private boolean validity;
	private LinkedList<Restriction> restrictions;
}
