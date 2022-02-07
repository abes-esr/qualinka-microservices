package fr.abes.findrc;

import fr.abes.findrc.domain.repository.ReferenceAutoriteOracle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FindrcApplicationTests {

	@Autowired
	ReferenceAutoriteOracle referenceAutoriteOracle;

	/*@Test
	void contextLoads() {
	}*/

	@Test
	void LoadOracle() {
		System.out.println("ok");
	}
	/*@Test
	void getAutFromDatabase() throws InterruptedException {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		List<ReferenceAutorite> referenceAutoriteList = new ArrayList<>();
		AtomicInteger counter = new AtomicInteger(0);
		Flux<ReferenceAutoriteFromOracle> referenceAutoriteFromOracleFlux = referenceAutoriteOracle.getEntityWithPos("055714153");

		referenceAutoriteFromOracleFlux.collectSortedList(Comparator.comparing(ReferenceAutoriteFromOracle::getPosfield))
				.map(v -> {
					v.stream().collect(Collectors.groupingBy(ReferenceAutoriteFromOracle::getPosfield))
					.entrySet().stream().filter(t -> t.getValue().stream().noneMatch(e -> e.getTag().contains("700$3")))
					.forEach(t -> {
						counter.getAndIncrement();
						ReferenceAutorite referenceAutorite = new ReferenceAutorite();
						t.getValue().forEach(e -> {
							referenceAutorite.setPpn(e.getPpn()+"-"+counter.get());
							if (e.getTag().matches("700\\$a|701\\$a|702\\$a")) {
								referenceAutorite.setLastName(e.getDatas());
							}
							if (e.getTag().matches("700\\$b|701\\$b|702\\$b")) {
								referenceAutorite.setFirstName(e.getDatas());
							}

						});
						referenceAutoriteList.add(referenceAutorite);
					});
					return referenceAutoriteList;
					}
				)
				.subscribe(System.out::println, System.out::println, countDownLatch::countDown);

		countDownLatch.await();


		referenceAutoriteFromOracleFlux.collectSortedList(Comparator.comparing(ReferenceAutoriteFromOracle::getPosfield))
				.map(v -> v.stream().collect(Collectors.groupingBy(ReferenceAutoriteFromOracle::getPosfield))
					.entrySet().stream().peek(s -> counter.getAndIncrement())
					.filter(t -> t.getValue().stream().noneMatch(e -> e.getTag().contains("$3")))
					.reduce( referenceAutoriteList,(s, e) -> {
						ReferenceAutorite referenceAutorite = new ReferenceAutorite();
						e.getValue().forEach(t -> {
							referenceAutorite.setPpn(t.getPpn()+"-"+ counter.get());
							if (t.getTag().contains("$a")) {
								referenceAutorite.setLastName(t.getDatas());
							}
							if (t.getTag().contains("$b")) {
								referenceAutorite.setFirstName(t.getDatas());
							}
						});
						s.add(referenceAutorite);
						return s;
					},(s1, s2) -> null)
				)
				.subscribe(System.out::println, System.out::println, countDownLatch::countDown);

		countDownLatch.await();

	}*/

}
