package co.com.bancolombia.usecase.eSubscription;

import co.com.bancolombia.model.eSubscription.ESubscription;
import co.com.bancolombia.model.enterprise.CreditState;
import co.com.bancolombia.model.enterprise.Enterprise;
import co.com.bancolombia.model.enterprise.EnterpriseNotFoundException;
import co.com.bancolombia.model.enterprise.EnterpriseNotValidException;
import co.com.bancolombia.model.enterprise.SuperIntReport;
import co.com.bancolombia.model.enterprise.gateways.EnterpriseService;
import co.com.bancolombia.model.enterpriseValidation.EnterpriseValidation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

@RequiredArgsConstructor
public class ESubscriptionUseCase {
	private final EnterpriseService entService;

	public Mono<ESubscription> subscribeEnterprise(Enterprise enterprise){
		return entService.validateEnterprise(enterprise)
				.switchIfEmpty(Mono.defer(() -> Mono.error(new EnterpriseNotFoundException())))
				.flatMap(this::searchRestrictions)
				.flatMap(this::searchCreditStateAndReports)
				.map(this::addInfoToValidation)
				.map(ESubscription::createSubscription)
				.onErrorMap((e) -> !(e instanceof EnterpriseNotFoundException),
						(e2) -> new EnterpriseNotValidException(e2.getMessage()));
	}

	private EnterpriseValidation addInfoToValidation(Tuple3<CreditState, SuperIntReport, EnterpriseValidation> t) {
		return t.getT3().toBuilder()
				.enterprise(t.getT3().getEnterprise())
				.creditState(t.getT1())
				.superIntReport(t.getT2())
				.build();
	}

	private Mono<Tuple3<CreditState, SuperIntReport, EnterpriseValidation>> searchCreditStateAndReports(EnterpriseValidation ev) {
		return Mono.zip(entService.searchCreditState(ev.getEnterprise()),
				entService.searchSuperIntReports(ev.getEnterprise()),
				Mono.just(ev));
	}

	private Mono<EnterpriseValidation> searchRestrictions(EnterpriseValidation ev) {
		return entService.searchRestrictions(ev.getEnterprise())
				.map((e) -> {
					boolean hasRes = e.getRestrictions().size() != 0;
					return ev.toBuilder().enterprise(ev.getEnterprise()).hasRestrictions(hasRes).build();
				});
	}

}
