package co.com.bancolombia.model.enterpriseValidation;

import co.com.bancolombia.model.enterprise.CreditState;
import co.com.bancolombia.model.enterprise.Enterprise;
import co.com.bancolombia.model.enterprise.SuperIntReport;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class EnterpriseValidation{
	private Enterprise enterprise;
	private boolean nitIsValidity;
	private boolean hasRestrictions;
	private CreditState creditState;
	private SuperIntReport superIntReport;

}
